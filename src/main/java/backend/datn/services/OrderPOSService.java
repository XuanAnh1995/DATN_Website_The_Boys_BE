package backend.datn.services;

import backend.datn.dto.response.OrderPOSResponse;
import backend.datn.entities.OrderPOS;
import backend.datn.mapper.OrderPOSMapper;
import backend.datn.repositories.OrderPOSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderPOSService {

    @Autowired
    private OrderPOSRepository orderPOSRepository;

    /**
     * Lấy danh sách hóa đơn POS với tìm kiếm và phân trang
     * @param search Từ khóa tìm kiếm (có thể null)
     * @param page Số trang (bắt đầu từ 0)
     * @param size Kích thước trang
     * @param sortKey Trường sắp xếp
     * @param sortDirection Hướng sắp xếp (asc/desc)
     * @return Page<OrderPOSResponse> Danh sách hóa đơn POS đã phân trang
     */
    public Page<OrderPOSResponse> getAllPOSOrders(String search, int page, int size, String sortKey, String sortDirection) {
        // Xác định hướng sắp xếp (ascending hoặc descending)
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortKey).ascending() :
                Sort.by(sortKey).descending();

        // Tạo đối tượng Pageable từ page, size và sort
        Pageable pageable = PageRequest.of(page, size, sort);

        // Nếu search không rỗng, thêm ký tự '%' vào đầu và cuối
        String formattedSearch = (search == null || search.isEmpty()) ? null : "%" + search.toLowerCase() + "%";

        // Chỉ lấy hóa đơn POS (kindOfOrder = true)
        Page<OrderPOS> posOrders = orderPOSRepository.findAllByKindOfOrderWithSearchAndJoin(true, search, pageable);

        // Ánh xạ sang OrderPOSResponse
        return posOrders.map(OrderPOSMapper::toOrderPOSResponse);
    }

    /**
     * Tìm hóa đơn POS theo ID
     * @param id ID của đơn hàng
     * @return OrderPOSResponse hoặc null nếu không tìm thấy
     */
    public OrderPOSResponse getPOSOrderById(Integer id) {
        Optional<OrderPOS> orderOpt = orderPOSRepository.findOrderPOSByIdWithKindOfOrder(id, true);
        return orderOpt.map(OrderPOSMapper::toOrderPOSResponse).orElse(null);
    }


}
