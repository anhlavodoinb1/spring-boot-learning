package me.loda.spring.depositcert.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * DTO cho việc khai báo chứng chỉ tiền gửi sơ cấp (Bước 1)
 */
public class PrimaryDepositCertificateRequest {

    @NotBlank(message = "Serial không được để trống")
    private String serial;

    @NotBlank(message = "Tên chứng chỉ tiền gửi không được để trống")
    private String certificateName;

    @NotNull(message = "Kỳ hạn sơ cấp không được để trống")
    @Positive(message = "Kỳ hạn sơ cấp phải lớn hơn 0")
    private Integer primaryTermDays;

    @NotNull(message = "Mệnh giá không được để trống")
    @Positive(message = "Mệnh giá phải lớn hơn 0")
    private BigDecimal faceValue;

    @NotNull(message = "Số lượng không được để trống")
    @Positive(message = "Số lượng phải lớn hơn 0")
    private Long quantity;

    @NotNull(message = "Tổng số tiền không được để trống")
    @Positive(message = "Tổng số tiền phải lớn hơn 0")
    private BigDecimal totalAmount;

    @NotNull(message = "Lãi suất sơ cấp không được để trống")
    @Positive(message = "Lãi suất sơ cấp phải lớn hơn 0")
    private BigDecimal primaryInterestRate;

    @NotBlank(message = "Kỳ trả lãi không được để trống")
    private String interestPaymentPeriod;

    // Constructors
    public PrimaryDepositCertificateRequest() {}

    public PrimaryDepositCertificateRequest(String serial, String certificateName, Integer primaryTermDays, 
                                          BigDecimal faceValue, Long quantity, BigDecimal totalAmount, 
                                          BigDecimal primaryInterestRate, String interestPaymentPeriod) {
        this.serial = serial;
        this.certificateName = certificateName;
        this.primaryTermDays = primaryTermDays;
        this.faceValue = faceValue;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.primaryInterestRate = primaryInterestRate;
        this.interestPaymentPeriod = interestPaymentPeriod;
    }

    // Getters and Setters
    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }

    public String getCertificateName() { return certificateName; }
    public void setCertificateName(String certificateName) { this.certificateName = certificateName; }

    public Integer getPrimaryTermDays() { return primaryTermDays; }
    public void setPrimaryTermDays(Integer primaryTermDays) { this.primaryTermDays = primaryTermDays; }

    public BigDecimal getFaceValue() { return faceValue; }
    public void setFaceValue(BigDecimal faceValue) { this.faceValue = faceValue; }

    public Long getQuantity() { return quantity; }
    public void setQuantity(Long quantity) { this.quantity = quantity; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getPrimaryInterestRate() { return primaryInterestRate; }
    public void setPrimaryInterestRate(BigDecimal primaryInterestRate) { this.primaryInterestRate = primaryInterestRate; }

    public String getInterestPaymentPeriod() { return interestPaymentPeriod; }
    public void setInterestPaymentPeriod(String interestPaymentPeriod) { this.interestPaymentPeriod = interestPaymentPeriod; }
}