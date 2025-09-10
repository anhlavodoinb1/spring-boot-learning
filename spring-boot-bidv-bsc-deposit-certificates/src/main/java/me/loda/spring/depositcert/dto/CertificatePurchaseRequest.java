package me.loda.spring.depositcert.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * DTO cho việc BSC đăng ký mua CCTG sơ cấp (Bước 3)
 */
public class CertificatePurchaseRequest {

    @NotBlank(message = "CIF công ty chứng khoán không được để trống")
    private String companyFif;

    @NotBlank(message = "Serial chứng chỉ tiền gửi không được để trống")
    private String certificateSerial;

    @NotBlank(message = "CIF khách hàng mua không được để trống")
    private String buyerCif;

    @NotBlank(message = "Tên người mua không được để trống")
    private String buyerName;

    @NotNull(message = "Số lượng mua không được để trống")
    @Positive(message = "Số lượng mua phải lớn hơn 0")
    private Long purchaseQuantity;

    // Constructors
    public CertificatePurchaseRequest() {}

    public CertificatePurchaseRequest(String companyFif, String certificateSerial, String buyerCif, 
                                    String buyerName, Long purchaseQuantity) {
        this.companyFif = companyFif;
        this.certificateSerial = certificateSerial;
        this.buyerCif = buyerCif;
        this.buyerName = buyerName;
        this.purchaseQuantity = purchaseQuantity;
    }

    // Getters and Setters
    public String getCompanyFif() { return companyFif; }
    public void setCompanyFif(String companyFif) { this.companyFif = companyFif; }

    public String getCertificateSerial() { return certificateSerial; }
    public void setCertificateSerial(String certificateSerial) { this.certificateSerial = certificateSerial; }

    public String getBuyerCif() { return buyerCif; }
    public void setBuyerCif(String buyerCif) { this.buyerCif = buyerCif; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public Long getPurchaseQuantity() { return purchaseQuantity; }
    public void setPurchaseQuantity(Long purchaseQuantity) { this.purchaseQuantity = purchaseQuantity; }
}