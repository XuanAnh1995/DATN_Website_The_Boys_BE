package backend.datn.services;

import backend.datn.dto.request.CartItemRequest;
import backend.datn.dto.response.CartItemResponse;
import backend.datn.entities.Cart;
import backend.datn.entities.Customer;
import backend.datn.entities.ProductDetail;
import backend.datn.exceptions.BadRequestException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.mapper.CartMapper;
import backend.datn.repositories.CartRepository;
import backend.datn.repositories.CustomerRepository;
import backend.datn.repositories.ProductDetailRepository;
import backend.datn.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Transactional
    public List<CartItemResponse> getAllCartItems() {
        Customer customer = getCurrentCustomer();
        List<Cart> cartItems = cartRepository.findByCustomer(customer);
        return cartItems.stream()
                .map(CartMapper::toCartItemResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CartItemResponse addProductToCart(CartItemRequest request) {
        Customer customer = getCurrentCustomer();
        ProductDetail productDetail = productDetailRepository.findById(request.getProductDetailId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chi tiết sản phẩm"));

        int stockQuantity = productDetail.getQuantity();
        Cart cart = cartRepository.findByCustomerAndProductDetail(customer, productDetail)
                .orElse(null);

        int newQuantity = request.getQuantity();

        if (cart != null) {
            newQuantity += cart.getQuantity();
        }

        if (newQuantity > stockQuantity) {
            throw new BadRequestException("Số lượng sản phẩm trong kho không đủ!");
        }

        if (cart != null) {
            cart.setQuantity(newQuantity);
        } else {
            cart = new Cart();
            cart.setCustomer(customer);
            cart.setProductDetail(productDetail);
            cart.setQuantity(request.getQuantity());
        }

        cartRepository.save(cart);
        return CartMapper.toCartItemResponse(cart);
    }



    @Transactional
    public void removeProductFromCart(Long cartItemId) {
        cartRepository.deleteById(cartItemId);
    }

    @Transactional
    public void clearCart() {
        Customer customer = getCurrentCustomer();
        cartRepository.deleteByCustomer(customer);
    }

    @Transactional
    public CartItemResponse updateCartItemQuantity(Long cartItemId, Integer quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Số lượng phải lớn hơn 0");
        }

        Cart cart = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));

        ProductDetail productDetail = cart.getProductDetail();
        int stockQuantity = productDetail.getQuantity();

        if (quantity > stockQuantity) {
            throw new BadRequestException("Số lượng sản phẩm trong kho không đủ!");
        }

        cart.setQuantity(quantity);
        cartRepository.save(cart);

        return CartMapper.toCartItemResponse(cart);
    }


    private Customer getCurrentCustomer() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerRepository.findByUsername(userDetails.getUsername());
        if (customer == null) {
            throw new EntityNotFoundException("Không tìm thấy khách hàng");
        }
        return customer;
    }
}