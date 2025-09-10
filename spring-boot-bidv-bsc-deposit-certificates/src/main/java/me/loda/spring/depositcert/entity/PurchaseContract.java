package me.loda.spring.depositcert.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Hợp đồng mua chứng chỉ tiền gửi (Purchase Contract)
 */
@Entity
@Table(name = "purchase_contracts")
public class PurchaseContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Số hợp đồng không được để trống")
    @Column(name = "contract_number", unique = true, nullable = false)
    private String contractNumber;

    @NotNull(message = "Công ty chứng khoán không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "securities_company_id", nullable = false)
    private SecuritiesCompany securitiesCompany;

    @NotNull(message = "Chứng chỉ tiền gửi không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_certificate_id", nullable = false)
    private PrimaryDepositCertificate primaryCertificate;

    @NotBlank(message = "CIF khách hàng mua không được để trống")
    @Column(name = "buyer_cif", nullable = false)
    private String buyerCif;

    @NotBlank(message = "Tên người mua không được để trống")
    @Column(name = "buyer_name", nullable = false)
    private String buyerName;

    @NotNull(message = "Số lượng mua không được để trống")
    @Positive(message = "Số lượng mua phải lớn hơn 0")
    @Column(name = "purchase_quantity", nullable = false)
    private Long purchaseQuantity;

    @NotNull(message = "Tổng số tiền không được để trống")
    @Positive(message = "Tổng số tiền phải lớn hơn 0")
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContractStatus status = ContractStatus.PENDING;

    @Column(name = "accounting_transaction_id")
    private String accountingTransactionId;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public PurchaseContract() {}

    public PurchaseContract(Long id, String contractNumber, SecuritiesCompany securitiesCompany, 
                          PrimaryDepositCertificate primaryCertificate, String buyerCif, String buyerName, 
                          Long purchaseQuantity, BigDecimal totalAmount, ContractStatus status, 
                          String accountingTransactionId, String errorMessage, Integer retryCount, 
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.contractNumber = contractNumber;
        this.securitiesCompany = securitiesCompany;
        this.primaryCertificate = primaryCertificate;
        this.buyerCif = buyerCif;
        this.buyerName = buyerName;
        this.purchaseQuantity = purchaseQuantity;
        this.totalAmount = totalAmount;
        this.status = status;
        this.accountingTransactionId = accountingTransactionId;
        this.errorMessage = errorMessage;
        this.retryCount = retryCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContractNumber() { return contractNumber; }
    public void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }

    public SecuritiesCompany getSecuritiesCompany() { return securitiesCompany; }
    public void setSecuritiesCompany(SecuritiesCompany securitiesCompany) { this.securitiesCompany = securitiesCompany; }

    public PrimaryDepositCertificate getPrimaryCertificate() { return primaryCertificate; }
    public void setPrimaryCertificate(PrimaryDepositCertificate primaryCertificate) { this.primaryCertificate = primaryCertificate; }

    public String getBuyerCif() { return buyerCif; }
    public void setBuyerCif(String buyerCif) { this.buyerCif = buyerCif; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public Long getPurchaseQuantity() { return purchaseQuantity; }
    public void setPurchaseQuantity(Long purchaseQuantity) { this.purchaseQuantity = purchaseQuantity; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public ContractStatus getStatus() { return status; }
    public void setStatus(ContractStatus status) { this.status = status; }

    public String getAccountingTransactionId() { return accountingTransactionId; }
    public void setAccountingTransactionId(String accountingTransactionId) { this.accountingTransactionId = accountingTransactionId; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum ContractStatus {
        PENDING,           // Đang chờ xử lý
        ACCOUNTING_SUCCESS, // Hạch toán thành công
        ACCOUNTING_FAILED,  // Hạch toán thất bại
        BSC_UPDATED,       // Đã cập nhật BSC thành công
        BSC_UPDATE_FAILED, // Cập nhật BSC thất bại
        COMPLETED,         // Hoàn thành
        FAILED,            // Thất bại hoàn toàn
        REVERSED           // Đã hoàn tiền
    }
}