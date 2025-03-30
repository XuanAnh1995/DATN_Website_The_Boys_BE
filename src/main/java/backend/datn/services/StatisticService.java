package backend.datn.services;

import backend.datn.dto.response.statistic.*;
import backend.datn.repositories.StatisticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class StatisticService {

    @Autowired
    private StatisticRepository statisticRepository;

    // Doanh thu theo ngày
    public List<DailyRevenueResponse> getDailyRevenue() {
        List<Object[]> rawData = statisticRepository.getDailyRevenue();
        List<DailyRevenueResponse> result = new ArrayList<>();

        for (Object[] record : rawData) {
            DailyRevenueResponse dto = new DailyRevenueResponse();
            dto.setDayNumber((Integer) record[0]);
            dto.setMonthNumber((Integer) record[1]);
            dto.setYearNumber((Integer) record[2]);
            dto.setDailyRevenue((BigDecimal) record[3]);
            result.add(dto);
        }

        return result;
    }

    // Doanh thu theo tuần
    public List<WeeklyRevenueResponse> getWeeklyRevenue() {
        List<Object[]> rawData = statisticRepository.getWeeklyRevenue();
        List<WeeklyRevenueResponse> result = new ArrayList<>();

        for (Object[] record : rawData) {
            WeeklyRevenueResponse dto = new WeeklyRevenueResponse();
            dto.setWeekNumber((Integer) record[0]);
            dto.setYearNumber((Integer) record[1]);
            dto.setWeeklyRevenue((BigDecimal) record[2]);
            result.add(dto);
        }

        return result;
    }

    // Doanh thu theo tháng
    public List<MonthlyRevenueResponse> getMonthlyRevenue() {
        List<Object[]> rawData = statisticRepository.getMonthlyRevenue();
        List<MonthlyRevenueResponse> result = new ArrayList<>();

        for (Object[] record : rawData) {
            MonthlyRevenueResponse dto = new MonthlyRevenueResponse();
            dto.setMonthNumber((Integer) record[0]);
            dto.setYearNumber((Integer) record[1]);
            dto.setMonthlyRevenue((BigDecimal) record[2]);
            result.add(dto);
        }

        return result;
    }

    // Doanh thu theo năm
    public List<YearlyRevenueResponse> getYearlyRevenue() {
        List<Object[]> rawData = statisticRepository.getYearlyRevenue();
        List<YearlyRevenueResponse> result = new ArrayList<>();

        for (Object[] record : rawData) {
            YearlyRevenueResponse dto = new YearlyRevenueResponse();
            dto.setYearNumber((Integer) record[0]);
            dto.setYearlyRevenue((BigDecimal) record[1]);
            result.add(dto);
        }

        return result;
    }

    // doanh thu theo kênh:
    public List<ChannelRevenueResponse> getChannelRevenue() {
        List<Object[]> rawData = statisticRepository.getChannelRevenue();
        List<ChannelRevenueResponse> result = new ArrayList<>();

        for (Object[] record : rawData) {
            ChannelRevenueResponse dto = new ChannelRevenueResponse();
            dto.setDayNumber((Integer) record[0]);
            dto.setMonthNumber((Integer) record[1]);
            dto.setYearNumber((Integer) record[2]);
            dto.setOnlineRevenue((BigDecimal) record[3]);
            dto.setInStoreRevenue((BigDecimal) record[4]);
            result.add(dto);
        }

        return result;
    }

    // Tỷ Lệ Đơn Hàng Theo Trạng Thái
    public List<OrderStatusDistributionResponse> getOrderStatusDistribution() {
        try {
            List<Object[]> rawData = statisticRepository.getOrderStatusDistribution();
            List<OrderStatusDistributionResponse> result = new ArrayList<>();

            // Kiểm tra nếu rawData là null hoặc rỗng
            if (rawData == null || rawData.isEmpty()) {
                return result; // Trả về danh sách rỗng nếu không có dữ liệu
            }

            for (Object[] record : rawData) {
                OrderStatusDistributionResponse dto = new OrderStatusDistributionResponse();

                try {
                    // Ánh xạ statusOrder
                    dto.setStatusOrder(record[0] != null ? (Integer) record[0] : -2); // -2 là giá trị mặc định nếu null
                    // Ánh xạ statusName
                    dto.setStatusName(record[1] != null ? (String) record[1] : "Không xác định");
                    // Ánh xạ orderCount
                    if (record[2] instanceof BigInteger) {
                        dto.setOrderCount(((BigInteger) record[2]).intValue());
                    } else if (record[2] instanceof Long) {
                        dto.setOrderCount(((Long) record[2]).intValue());
                    } else if (record[2] instanceof Integer) {
                        dto.setOrderCount((Integer) record[2]);
                    } else {
                        dto.setOrderCount(0); // Giá trị mặc định nếu kiểu không xác định
                        System.err.println("Kiểu dữ liệu không mong đợi cho orderCount: " + (record[2] != null ? record[2].getClass().getName() : "null"));
                    }
                    result.add(dto);
                } catch (ClassCastException e) {
                    // Ghi log lỗi ánh xạ
//                    System.err.println("Lỗi ánh xạ dữ liệu cho record: " + Arrays.toString(record));
                    e.printStackTrace();
                }
            }

            return result;
        } catch (Exception e) {
            // Ghi log lỗi
            System.err.println("Lỗi khi truy vấn tỷ lệ đơn hàng theo trạng thái: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi truy vấn tỷ lệ đơn hàng theo trạng thái: " + e.getMessage());
        }
    }

    // Tỷ Lệ Thanh Toán Theo Phương Thức
    public List<PaymentMethodDistributionResponse> getPaymentMethodDistribution() {
        try {
            List<Object[]> rawData = statisticRepository.getPaymentMethodDistribution();
            List<PaymentMethodDistributionResponse> result = new ArrayList<>();

            // Kiểm tra nếu rawData là null hoặc rỗng
            if (rawData == null || rawData.isEmpty()) {
                return result; // Trả về danh sách rỗng nếu không có dữ liệu
            }

            for (Object[] record : rawData) {
                PaymentMethodDistributionResponse dto = new PaymentMethodDistributionResponse();
                try {
                    // Ánh xạ paymentMethod
                    dto.setPaymentMethod(record[0] != null ? (Integer) record[0] : -1); // -1 là giá trị mặc định nếu null
                    // Ánh xạ methodName
                    dto.setMethodName(record[1] != null ? (String) record[1] : "Không xác định");
                    // Ánh xạ orderCount (xử lý cả Integer và Long)
                    if (record[2] instanceof BigInteger) {
                        dto.setOrderCount(((BigInteger) record[2]).intValue());
                    } else if (record[2] instanceof Long) {
                        dto.setOrderCount(((Long) record[2]).intValue());
                    } else if (record[2] instanceof Integer) {
                        dto.setOrderCount((Integer) record[2]);
                    } else {
                        dto.setOrderCount(0); // Giá trị mặc định nếu kiểu không xác định
                        System.err.println("Kiểu dữ liệu không mong đợi cho orderCount: " + (record[2] != null ? record[2].getClass().getName() : "null"));
                    }
                    result.add(dto);
                } catch (Exception e) {
//                    System.err.println("Lỗi ánh xạ dữ liệu cho record: " + Arrays.toString(record));
                    e.printStackTrace();
                }
            }

            return result;
        } catch (Exception e) {
            System.err.println("Lỗi khi truy vấn tỷ lệ thanh toán theo phương thức: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi truy vấn tỷ lệ thanh toán theo phương thức: " + e.getMessage());
        }
    }

    // Top 5 Khách Hàng Mua Nhiều Nhất
    public List<TopCustomerResponse> getTop5Customers() {
        try {
            List<Object[]> rawData = statisticRepository.getTop5Customers();
            List<TopCustomerResponse> result = new ArrayList<>();

            // Kiểm tra nếu rawData là null hoặc rỗng
            if (rawData == null || rawData.isEmpty()) {
                return result; // Trả về danh sách rỗng nếu không có dữ liệu
            }

            for (Object[] record : rawData) {
                TopCustomerResponse dto = new TopCustomerResponse();
                try {
                    // Ánh xạ customerName
                    dto.setCustomerName(record[0] != null ? (String) record[0] : "Khách hàng không xác định");
                    // Ánh xạ totalOrders (xử lý cả Integer và Long)
                    if (record[1] instanceof BigInteger) {
                        dto.setTotalOrders(((BigInteger) record[1]).intValue());
                    } else if (record[1] instanceof Long) {
                        dto.setTotalOrders(((Long) record[1]).intValue());
                    } else if (record[1] instanceof Integer) {
                        dto.setTotalOrders((Integer) record[1]);
                    } else {
                        dto.setTotalOrders(0); // Giá trị mặc định nếu kiểu không xác định
                        System.err.println("Kiểu dữ liệu không mong đợi cho totalOrders: " + (record[1] != null ? record[1].getClass().getName() : "null"));
                    }
                    // Ánh xạ totalSpent
                    dto.setTotalSpent(record[2] != null ? (BigDecimal) record[2] : BigDecimal.ZERO);
                    result.add(dto);
                } catch (Exception e) {
//                    System.err.println("Lỗi ánh xạ dữ liệu cho record: " + Arrays.toString(record));
                    e.printStackTrace();
                }
            }

            return result;
        } catch (Exception e) {
            System.err.println("Lỗi khi truy vấn top 5 khách hàng mua nhiều nhất: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi truy vấn top 5 khách hàng mua nhiều nhất: " + e.getMessage());
        }
    }

    // Top 5 Sản Phẩm Tồn Kho Nhiều Nhất
    public List<TopInventoryProductResponse> getTop5InventoryProducts() {
        List<Object[]> rawData = statisticRepository.getTop5InventoryProducts();
        List<TopInventoryProductResponse> result = new ArrayList<>();

        for (Object[] record : rawData) {
            TopInventoryProductResponse dto = new TopInventoryProductResponse();
            dto.setProductDetailName((String) record[0]);
            dto.setQuantity((Integer) record[1]);
            result.add(dto);
        }

        return result;
    }


    // Lấy top 5 sản phẩm bán chạy nhất trong khoảng thời gian:
    public List<ProductDetailDTO> getTop5BestSellingProductDetailInAPeriodOfTime(String startDate, String endDate) {
        return statisticRepository.getTop5BestSellingProductDetailInAPeriodOfTime(startDate, endDate);
    }

    // Lấy tổng doanh thu
    public BigDecimal getTotalRevenue() {
        return statisticRepository.getTotalRevenue();
    }

    // Lấy số lượng khách hàng
    public Integer getNumberOfCustomers() {
        return statisticRepository.getNumberOfCustomers();
    }

    // Lấy tổng số hóa đơn
    public Integer getNumberOfInvoices() {
        return statisticRepository.getNumberOfInvoices();
    }

    // Lấy số lượng Admin
    public Integer getNumberOfAdmin() {
        return statisticRepository.getNumberOfAdmin();
    }

    // Lấy số lượng nhân viên
    public Integer getNumberOfStaff() {
        return statisticRepository.getNumberOfStaff();
    }

}
