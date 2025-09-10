package me.loda.spring.depositcert.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

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