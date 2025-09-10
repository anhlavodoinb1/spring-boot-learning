package me.loda.spring.depositcert.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Chứng chỉ tiền gửi sơ cấp (Primary Deposit Certificate)
 */
@Entity
@Table(name = "primary_deposit_certificates")
public class PrimaryDepositCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Serial không được để trống")
    @Column(name = "serial", unique = true, nullable = false)
    private String serial;

    @NotBlank(message = "Tên chứng chỉ tiền gửi không được để trống")
    @Column(name = "certificate_name", nullable = false)
    private String certificateName;

    @NotNull(message = "Kỳ hạn sơ cấp không được để trống")
    @Positive(message = "Kỳ hạn sơ cấp phải lớn hơn 0")
    @Column(name = "primary_term_days", nullable = false)
    private Integer primaryTermDays;

    @NotNull(message = "Mệnh giá không được để trống")
    @Positive(message = "Mệnh giá phải lớn hơn 0")
    @Column(name = "face_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal faceValue;

    @NotNull(message = "Số lượng không được để trống")
    @Positive(message = "Số lượng phải lớn hơn 0")
    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @NotNull(message = "Tổng số tiền không được để trống")
    @Positive(message = "Tổng số tiền phải lớn hơn 0")
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @NotNull(message = "Lãi suất sơ cấp không được để trống")
    @Positive(message = "Lãi suất sơ cấp phải lớn hơn 0")
    @Column(name = "primary_interest_rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal primaryInterestRate;

    @NotBlank(message = "Kỳ trả lãi không được để trống")
    @Column(name = "interest_payment_period", nullable = false)
    private String interestPaymentPeriod;

    @Column(name = "available_quantity", nullable = false)
    private Long availableQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CertificateStatus status = CertificateStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public PrimaryDepositCertificate() {}

    public PrimaryDepositCertificate(Long id, String serial, String certificateName, Integer primaryTermDays, 
                                   BigDecimal faceValue, Long quantity, BigDecimal totalAmount, 
                                   BigDecimal primaryInterestRate, String interestPaymentPeriod, 
                                   Long availableQuantity, CertificateStatus status, 
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.serial = serial;
        this.certificateName = certificateName;
        this.primaryTermDays = primaryTermDays;
        this.faceValue = faceValue;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.primaryInterestRate = primaryInterestRate;
        this.interestPaymentPeriod = interestPaymentPeriod;
        this.availableQuantity = availableQuantity;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (availableQuantity == null) {
            availableQuantity = quantity;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Long getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Long availableQuantity) { this.availableQuantity = availableQuantity; }

    public CertificateStatus getStatus() { return status; }
    public void setStatus(CertificateStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum CertificateStatus {
        ACTIVE, INACTIVE, SOLD_OUT
    }
}