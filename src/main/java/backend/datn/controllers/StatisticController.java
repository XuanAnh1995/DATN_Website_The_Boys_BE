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
