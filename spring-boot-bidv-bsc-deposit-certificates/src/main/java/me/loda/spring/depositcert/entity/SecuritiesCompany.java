package me.loda.spring.depositcert.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Thông tin Công ty chứng khoán (Securities Company Information)
 */
@Entity
@Table(name = "securities_companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecuritiesCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "CIF không được để trống")
    @Column(name = "cif", unique = true, nullable = false)
    private String cif;

    @NotBlank(message = "Tên đối tác không được để trống")
    @Column(name = "partner_name", nullable = false)
    private String partnerName;

    @NotBlank(message = "Số tài khoản ngân hàng không được để trống")
    @Column(name = "bank_account_number", nullable = false)
    private String bankAccountNumber;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CompanyStatus status = CompanyStatus.ACTIVE;

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

    public enum CompanyStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }
}