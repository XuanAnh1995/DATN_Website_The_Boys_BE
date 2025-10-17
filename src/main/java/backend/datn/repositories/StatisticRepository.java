package backend.datn.repositories;

import backend.datn.dto.response.statistic.ProductDetailDTO;
import backend.datn.entities.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<ProductDetail, Long> {

    // Doanh thu theo ngày
    @Query(value = """
            SELECT
                 DAY(o.create_date) AS dayNumber,
                 MONTH(o.create_date) AS monthNumber,
                 YEAR(o.create_date) AS yearNumber,
                 COALESCE(SUM(o.total_bill), 0) AS dailyRevenue
            FROM [order] o
            WHERE o.status_order = 5
            GROUP BY YEAR(o.create_date), MONTH(o.create_date), DAY(o.create_date)
            ORDER BY yearNumber, monthNumber, dayNumber;
            """, nativeQuery = true)
    List<Object[]> getDailyRevenue();


    // Doanh thu theo tuần
    @Query(value = """
            SELECT 
                DATEPART(WEEK, o.create_date) AS weekNumber, 
                YEAR(o.create_date) AS yearNumber, 
                ISNULL(SUM(o.total_bill), 0) AS weeklyRevenue
            FROM [order] o
            WHERE o.status_order = 5
            GROUP BY DATEPART(WEEK, o.create_date), YEAR(o.create_date)
            ORDER BY yearNumber DESC, weekNumber DESC;
            """, nativeQuery = true)
    List<Object[]> getWeeklyRevenue();


    // Doanh thu theo tháng
    @Query(value = """
            SELECT 
                MONTH(o.create_date) AS monthNumber, 
                YEAR(o.create_date) AS yearNumber, 
                ISNULL(SUM(o.total_bill), 0) AS monthlyRevenue
            FROM [order] o
            WHERE o.status_order = 5
            GROUP BY MONTH(o.create_date), YEAR(o.create_date)
            ORDER BY yearNumber DESC, monthNumber DESC;
            """, nativeQuery = true)
    List<Object[]> getMonthlyRevenue();


    // Doanh thu theo năm
    @Query(value = """
            SELECT 
                YEAR(o.create_date) AS year,
                ISNULL(SUM(o.total_bill), 0) AS yearlyRevenue
            FROM [order] o
            WHERE o.status_order = 5
            GROUP BY YEAR(o.create_date)
            ORDER BY YEAR(o.create_date) DESC;
            """, nativeQuery = true)
    List<Object[]> getYearlyRevenue();


    // Top 5 sản phẩm bán chạy nhất trong khoang thời gian
    @Query(value = """
                SELECT TOP 5
                               CONCAT(p.product_name, ' ', c.color_name, ' ', s.size_name) AS productDetailName,
                               SUM(od.quantity) AS totalQuantitySold,
                               SUM(od.quantity * pd.sale_price) AS totalRevenue
                           FROM [product] p
                           JOIN product_detail pd ON pd.product_id = p.id
                           JOIN size s ON pd.size_id = s.id
                           JOIN color c ON c.id = pd.color_id
                           JOIN order_detail od ON od.product_detail_id = pd.id
                           JOIN [order] o ON o.id = od.order_id
                           WHERE o.status_order = 5 
                           AND o.create_date BETWEEN :startDate AND :endDate -- Khoảng thời gian cần lấy dữ liệu
                           GROUP BY p.product_name, c.color_name, s.size_name
                           ORDER BY totalQuantitySold DESC, totalRevenue DESC; -- Ưu tiên theo số lượng bán, nếu trùng thì xét doanh thu
            """, nativeQuery = true)
    List<ProductDetailDTO> getTop5BestSellingProductDetailInAPeriodOfTime(@Param("startDate") String startDate, @Param("endDate") String endDate);


    // Tổng doanh thu của hóa đơn ở trạng thái hoàn thành
    @Query(value = """
                SELECT COALESCE(SUM(total_bill), 0)
                FROM [order]
                WHERE status_order = 5
            """, nativeQuery = true)
    BigDecimal getTotalRevenue();


    // Tổng số lượng khách hàng
    @Query(value = """
            SELECT COUNT(id)
            FROM customer
            """, nativeQuery = true)
    Integer getNumberOfCustomers();


    // Tổng số hóa đơn
    @Query(value = """
            SELECT COUNT(id)
            FROM [order]
            """, nativeQuery = true)
    Integer getNumberOfInvoices();

    // Tổng số lượng admin - quản lý
    @Query(value = """
            SELECT COUNT(id)
            FROM [employee]
            WHERE role_id = 1
            """, nativeQuery = true)
    Integer getNumberOfAdmin();

    // Tổng số lượng employee - nhân viên
    @Query(value = """
            SELECT COUNT(id)
            FROM [employee]
            WHERE role_id = 2
            """, nativeQuery = true)
    Integer getNumberOfStaff();

    // doanh thu theo kênh:
    @Query(value = """
            SELECT 
                DAY(o.create_date) AS dayNumber,
                MONTH(o.create_date) AS monthNumber,
                YEAR(o.create_date) AS yearNumber,
                SUM(CASE WHEN o.kind_of_order = 0 THEN o.total_bill ELSE 0 END) AS onlineRevenue,
                SUM(CASE WHEN o.kind_of_order = 1 THEN o.total_bill ELSE 0 END) AS inStoreRevenue
            FROM [order] o
            WHERE o.status_order = 5
            GROUP BY YEAR(o.create_date), MONTH(o.create_date), DAY(o.create_date)
            ORDER BY yearNumber, monthNumber, dayNumber;
            """, nativeQuery = true)
    List<Object[]> getChannelRevenue();

    @Query(value = """
            SELECT 
                o.status_order AS statusOrder,
                CASE 
                    WHEN o.status_order = -1 THEN N'Đã hủy'
                    WHEN o.status_order = 0 THEN N'Chờ xác nhận'
                    WHEN o.status_order = 1 THEN N'Chờ thanh toán'
                    WHEN o.status_order = 2 THEN N'Đã xác nhận'
                    WHEN o.status_order = 3 THEN N'Đang giao hàng'
                    WHEN o.status_order = 4 THEN N'Giao hàng không thành công'
                    WHEN o.status_order = 5 THEN N'Hoàn thành'
                    ELSE N'Không xác định'
                END AS statusName,
                COUNT(o.id) AS orderCount
            FROM [order] o
            GROUP BY o.status_order
            ORDER BY o.status_order;
            """, nativeQuery = true)
    List<Object[]> getOrderStatusDistribution();

    @Query(value = """
            SELECT 
                o.payment_method AS paymentMethod,
                CASE 
                    WHEN o.payment_method = 0 THEN N'Tiền mặt'
                    WHEN o.payment_method = 1 THEN N'VNPay'
                    ELSE N'Không xác định'
                END AS methodName,
                COUNT(o.id) AS orderCount
            FROM [order] o
            WHERE o.status_order = 5
            GROUP BY o.payment_method;
            """, nativeQuery = true)
    List<Object[]> getPaymentMethodDistribution();

    @Query(value = """
            SELECT TOP 5
                ISNULL(c.fullname, N'Khách hàng không xác định') AS customerName,
                COUNT(o.id) AS totalOrders,
                ISNULL(SUM(o.total_bill), 0) AS totalSpent
            FROM [order] o
            JOIN customer c ON c.id = o.customer_id
            WHERE o.status_order = 5
            GROUP BY c.fullname
            ORDER BY totalSpent DESC;
            """, nativeQuery = true)
    List<Object[]> getTop5Customers();

    @Query(value = """
            SELECT TOP 5
                CONCAT(p.product_name, ' ', c.color_name, ' ', s.size_name) AS productDetailName,
                pd.quantity AS quantity
            FROM product_detail pd
            JOIN [product] p ON p.id = pd.product_id
            JOIN color c ON c.id = pd.color_id
            JOIN size s ON s.id = pd.size_id
            WHERE pd.status = 1
            ORDER BY pd.quantity DESC;
            """, nativeQuery = true)
    List<Object[]> getTop5InventoryProducts();

}
