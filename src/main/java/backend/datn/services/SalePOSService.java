package backend.datn.services;

import backend.datn.dto.request.OrderDetailCreateRequest;
import backend.datn.dto.request.OrderPOSCreateRequest;
import backend.datn.dto.response.OrderResponse;
import backend.datn.entities.*;
import backend.datn.mapper.OrderMapper;
import backend.datn.repositories.OrderRepository;
import backend.datn.repositories.OrderDetailRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalePOSService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProductDetailService productDetailService;

    @Autowired
    private VoucherService voucherService;

//    public OrderResponse createOrder(OrderPOSCreateRequest request) {
//        Order order = new Order();
//        order.setCustomer(getCustomerById(request.getCustomerId()));
//        order.setEmployee(getEmployeeById(request.getEmployeeId()));
//        order.setVoucher(getVoucherById(request.getVoucherId()));
//        order.setPaymentMethod(request.getPaymentMethod());
//        order.setStatusOrder(request.getStatusOrder());
//        order.setKindOfOrder(request.getKindOfOrder());
//
//        // Tạo danh sách OrderDetail
//        List<OrderDetail> orderDetails = request.getOrderDetails().stream()
//                .map(detailReq -> createOrderDetail(detailReq, order))
//                .toList();
//
//        // Gán danh sách OrderDetail vào Order (đảm bảo quan hệ 2 chiều)
//        order.setOrderDetails(orderDetails);
//
//        // Chỉ cần lưu Order, OrderDetail sẽ tự động lưu theo do CascadeType.ALL
//        orderRepository.save(order);
//
//        return OrderMapper.toOrderResponse(order);
//    }


    // thêm hóa don rỗng
    public Order createEmptyOrder(Customer customer, Employee employee, Integer paymentMethod) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setEmployee(employee);
        order.setPaymentMethod(paymentMethod);
        order.setStatusOrder(1); // Trạng thái mới tạo
        order.setKindOfOrder(true); // Loại đơn hàng POS
        order.setOrderDetails(new ArrayList<>());
        order.setTotalAmount(0);

        return orderRepository.save(order);
    }

    private Customer getCustomerById(Integer customerId) {
        return customerService.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng với ID: " + customerId));
    }

    private Employee getEmployeeById(Integer employeeId) {
        return employeeService.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhân viên với ID: " + employeeId));
    }

    private Voucher getVoucherById(Integer voucherId) {
        return (voucherId != null) ? voucherService.findById(voucherId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy voucher với ID: " + voucherId)) : null;
    }

    private OrderDetail createOrderDetail(OrderDetailCreateRequest detailReq, Order order) {
        ProductDetail productDetail = productDetailService.findById(detailReq.getProductDetailId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với ID: " + detailReq.getProductDetailId()));

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProductDetail(productDetail);
        orderDetail.setQuantity(detailReq.getQuantity());
        orderDetail.setOrder(order);

        return orderDetail;
    }



    public OrderResponse updateOrderStatusAfterPayment(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Kiểm tra nếu đơn hàng đã hoàn thành
        if (order.getStatusOrder() == 5) {
            throw new IllegalStateException("Đơn hàng đã được thanh toán trước đó!");
        }

        // Kiểm tra tồn kho trước khi trừ
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            ProductDetail productDetail = orderDetail.getProductDetail();
            int quantity = productDetail.getQuantity();
            int orderedQuantity = orderDetail.getQuantity();

            if (quantity < orderedQuantity) {
                throw new IllegalArgumentException("Sản phẩm " + productDetail.getProduct().getProductName() + " không đủ hàng trong kho!");
            }
        }

        // Trừ tồn kho
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            ProductDetail productDetail = orderDetail.getProductDetail();
            productDetail.setQuantity(productDetail.getQuantity() - orderDetail.getQuantity());
            productDetailService.update(productDetail);
        }

        // Cập nhật trạng thái đơn hàng thành "Hoàn thành"
        order.setStatusOrder(5);
        return OrderMapper.toOrderResponse(orderRepository.save(order));
    }


    // thêm sản phẩn vào giỏ hàng
    public OrderResponse addProductToCart(Integer orderId, OrderDetailCreateRequest detailReq) {
        // Tìm đơn hàng theo ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Tìm sản phẩm theo ID
        ProductDetail productDetail = productDetailService.findById(detailReq.getProductDetailId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với ID: " + detailReq.getProductDetailId()));

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        OrderDetail existingOrderDetail = order.getOrderDetails().stream()
                .filter(od -> od.getProductDetail().getId().equals(detailReq.getProductDetailId()))
                .findFirst()
                .orElse(null);

        if (existingOrderDetail != null) {
            // Nếu sản phẩm đã có trong giỏ hàng, tăng số lượng
            existingOrderDetail.setQuantity(existingOrderDetail.getQuantity() + detailReq.getQuantity());
        } else {
            // Nếu chưa có, tạo mới
            OrderDetail newOrderDetail = new OrderDetail();
            newOrderDetail.setOrder(order);
            newOrderDetail.setProductDetail(productDetail);
            newOrderDetail.setQuantity(detailReq.getQuantity());

            order.getOrderDetails().add(newOrderDetail);
        }

        // Lưu đơn hàng sau khi cập nhật giỏ hàng
        return OrderMapper.toOrderResponse(orderRepository.save(order));
    }


}
