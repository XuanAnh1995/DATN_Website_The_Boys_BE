package backend.datn.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class EmailRequest {
    private String voucherId;
    private String fromEmail;
    private List<String> toEmails;
    private String voucherCode;
    private String voucherName;
    private BigDecimal minCondition;
    private BigDecimal maxDiscount;
    private Double reducedPercent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant endDate;

    // Getters và Setters
    public String getVoucherId() { return voucherId; }
    public void setVoucherId(String voucherId) { this.voucherId = voucherId; }
    public String getFromEmail() { return fromEmail; }
    public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }
    public List<String> getToEmails() { return toEmails; }
    public void setToEmails(List<String> toEmails) { this.toEmails = toEmails; }
    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }
    public String getVoucherName() { return voucherName; }
    public void setVoucherName(String voucherName) { this.voucherName = voucherName; }
    public BigDecimal getMinCondition() { return minCondition; }
    public void setMinCondition(BigDecimal minCondition) { this.minCondition = minCondition; }
    public BigDecimal getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(BigDecimal maxDiscount) { this.maxDiscount = maxDiscount; }
    public Double getReducedPercent() { return reducedPercent; }
    public void setReducedPercent(Double reducedPercent) { this.reducedPercent = reducedPercent; }
    public Instant getStartDate() { return startDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }
    public Instant getEndDate() { return endDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }
}