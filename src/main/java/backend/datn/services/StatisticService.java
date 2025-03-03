package backend.datn.services;

import backend.datn.dto.response.statistic.*;
import backend.datn.repositories.StatisticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticService {

    @Autowired
    private StatisticRepository statisticRepository;

    // Doanh thu theo ngày
    public List<DailyRevenueResponse> getDailyRevenue() {
        List<Object[]> rawData = statisticRepository.getDailyRevenue();
        List<DailyRevenueResponse> result = new ArrayList<>();

        for (Object[] record :rawData) {
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

    // Lấy top 5 sản phẩm bán chạy nhất trong khoảng thời gian:
    public List<ProductDetailDTO> getTop5BestSellingProductDetailInAPeriodOfTime(String startDate, String endDate) {
        return statisticRepository.getTop5BestSellingProductDetailInAPeriodOfTime(startDate, endDate);
    }

    // Lấy tổng doanh thu
    public BigDecimal getTotalRevenue(){
        return statisticRepository.getTotalRevenue();
    }

    // Lấy số lượng khách hàng
    public Integer getNumberOfCustomers(){
        return statisticRepository.getNumberOfCustomers();
    }

    // Lấy tổng số hóa đơn
    public Integer getNumberOfInvoices(){
        return statisticRepository.getNumberOfInvoices();
    }

    // Lấy số lượng Admin
    public Integer getNumberOfAdmin(){
        return statisticRepository.getNumberOfAdmin();
    }

    // Lấy số lượng nhân viên
    public Integer getNumberOfStaff(){
        return statisticRepository.getNumberOfStaff();
    }

}
