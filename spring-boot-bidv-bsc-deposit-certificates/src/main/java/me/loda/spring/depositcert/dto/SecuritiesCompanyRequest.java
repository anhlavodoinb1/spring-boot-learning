package me.loda.spring.depositcert.dto;

import javax.validation.constraints.NotBlank;

/**
 * DTO cho việc khai báo thông tin Công ty chứng khoán (Bước 2)
 */
public class SecuritiesCompanyRequest {

    @NotBlank(message = "CIF không được để trống")
    private String cif;

    @NotBlank(message = "Tên đối tác không được để trống")
    private String partnerName;

    @NotBlank(message = "Số tài khoản ngân hàng không được để trống")
    private String bankAccountNumber;

    private String bankName;
    private String contactEmail;
    private String contactPhone;
    private String address;

    // Constructors
    public SecuritiesCompanyRequest() {}

    public SecuritiesCompanyRequest(String cif, String partnerName, String bankAccountNumber, 
                                  String bankName, String contactEmail, String contactPhone, String address) {
        this.cif = cif;
        this.partnerName = partnerName;
        this.bankAccountNumber = bankAccountNumber;
        this.bankName = bankName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.address = address;
    }

    // Getters and Setters
    public String getCif() { return cif; }
    public void setCif(String cif) { this.cif = cif; }

    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}