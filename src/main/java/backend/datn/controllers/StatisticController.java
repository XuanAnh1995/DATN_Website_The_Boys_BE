package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.response.statistic.*;
import backend.datn.services.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @GetMapping("/daily-revenue")
    public ResponseEntity<ApiResponse> getDailyRevenue() {
        try {
            List<DailyRevenueResponse> data = statisticService.getDailyRevenue();
            ApiResponse response = new ApiResponse("success", "Truy vấn doanh thu hàng ngày thành công", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Truy vấn doanh thu hàng ngày thất bại", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/weekly-revenue")
    public ResponseEntity<ApiResponse> getWeeklyRevenue() {
        try {
            List<WeeklyRevenueResponse> data = statisticService.getWeeklyRevenue();
            ApiResponse response = new ApiResponse("success", "Truy vấn doanh thu hàng tuần thành công", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Truy vấn doanh thu hàng tuần thất bại", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/monthly-revenue")
    public ResponseEntity<ApiResponse> getMonthlyRevenue() {
        try {
            List<MonthlyRevenueResponse> data = statisticService.getMonthlyRevenue();
            ApiResponse response = new ApiResponse("success", "Truy vấn doanh thu hàng tháng thành công", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Truy vấn doanh thu hàng tháng thất bại", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/yearly-revenue")
    public ResponseEntity<ApiResponse> getYearlyRevenue() {
        try {
            List<YearlyRevenueResponse> data = statisticService.getYearlyRevenue();
            ApiResponse response = new ApiResponse("success", "Truy vấn doanh thu hàng năm thành công", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Truy vấn doanh thu hàng năm thất bại", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/channel-revenue")
    public ResponseEntity<ApiResponse> getChannelRevenue() {
        try {
            List<ChannelRevenueResponse> data = statisticService.getChannelRevenue();
            ApiResponse response = new ApiResponse("success", "Truy vấn doanh thu theo kênh thành công", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Truy vấn doanh thu theo kênh thất bại", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/order-status-distribution")
    public ResponseEntity<ApiResponse> getOrderStatusDistribution() {
        try {
            List<OrderStatusDistributionResponse> data = statisticService.getOrderStatusDistribution();
            if (data.isEmpty()) {
                ApiResponse response = new ApiResponse("success", "Không có dữ liệu về tỷ lệ đơn hàng theo trạng thái", data);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            ApiResponse response = new ApiResponse("success", "Truy vấn tỷ lệ đơn hàng theo trạng thái thành công", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Ghi log lỗi
            System.err.println("Lỗi khi truy vấn tỷ lệ đơn hàng theo trạng thái: " + e.getMessage());
            e.printStackTrace();
            ApiResponse response = new ApiResponse("error", "Truy vấn tỷ lệ đơn hàng theo trạng thái thất bại: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/payment-method-distribution")
    public ResponseEntity<ApiResponse> getPaymentMethodDistribution() {
        try {
            List<PaymentMethodDistributionResponse> data = statisticService.getPaymentMethodDistribution();
            if (data.isEmpty()) {
                ApiResponse response = new ApiResponse("success", "Không có dữ liệu về tỷ lệ thanh toán theo phương thức", data);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            ApiResponse response = new ApiResponse("success", "Truy vấn tỷ lệ thanh toán theo phương thức thành công", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Lỗi khi truy vấn tỷ lệ thanh toán theo phương thức: " + e.getMessage());
            e.printStackTrace();
            ApiResponse response = new ApiResponse("error", "Truy vấn tỷ lệ thanh toán theo phương thức thất bại: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/top-5-customers")
    public ResponseEntity<ApiResponse> getTop5Customers() {
        try {
            List<TopCustomerResponse> data = statisticService.getTop5Customers();
            if (data.isEmpty()) {
                ApiResponse response = new ApiResponse("success", "Không có dữ liệu về top 5 khách hàng mua nhiều nhất", data);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            ApiResponse response = new ApiResponse("success", "Truy vấn top 5 khách hàng mua nhiều nhất thành công", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Lỗi khi truy vấn top 5 khách hàng mua nhiều nhất: " + e.getMessage());
            e.printStackTrace();
            ApiResponse response = new ApiResponse("error", "Truy vấn top 5 khách hàng mua nhiều nhất thất bại: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/top-5-inventory-products")
    public ResponseEntity<ApiResponse> getTop5InventoryProducts() {
        try {
            List<TopInventoryProductResponse> data = statisticService.getTop5InventoryProducts();
            ApiResponse response = new ApiResponse("success", "Truy vấn top 5 sản phẩm tồn kho nhiều nhất thành công", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Truy vấn top 5 sản phẩm tồn kho nhiều nhất thất bại", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/top-5-products")
    public ResponseEntity<ApiResponse> getTopSellingProducts(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            List<ProductDetailDTO> data = statisticService.getTop5BestSellingProductDetailInAPeriodOfTime(startDate, endDate);
            ApiResponse response = new ApiResponse("success", "Truy vấn top 5 sản phẩm bán chạy nhất thành công", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Truy vấn top 5 sản phẩm bán chạy nhất thất bại", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/total-revenue")
    public ResponseEntity<ApiResponse> getTotalRevenue() {
        try {
            BigDecimal data = statisticService.getTotalRevenue();
            ApiResponse response = new ApiResponse("success", "Truy vấn tổng doanh thu thành công", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Truy vấn tổng doanh thu thất bại", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/total-customers")
    public ResponseEntity<ApiResponse> getTotalCustomers() {
        try {
            Integer data = statisticService.getNumberOfCustomers();
            ApiResponse response = new ApiResponse("success", "Truy vấn tổng số khách hàng thành công", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Truy vấn tổng số khách hàng thất bại", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/total-invoices")
    public ResponseEntity<ApiResponse> getTotalInvoices() {
        try {
            Integer data = statisticService.getNumberOfInvoices();
            ApiResponse response = new ApiResponse("success", "Truy vấn tổng số hóa đơn thành công", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Truy vấn tổng số hóa đơn thất bại", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/total-admins")
    public ResponseEntity<ApiResponse> getTotalAdmins() {
        try {
            Integer data = statisticService.getNumberOfAdmin();
            ApiResponse response = new ApiResponse("success", "Truy vấn tổng số admin thành công", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Truy vấn tổng số admin thất bại", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/total-staff")
    public ResponseEntity<ApiResponse> getTotalStaff() {
        try {
            Integer data = statisticService.getNumberOfStaff();
            ApiResponse response = new ApiResponse("success", "Truy vấn tổng số nhân viên thành công", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Truy vấn tổng số nhân viên thất bại", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
