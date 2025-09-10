package me.loda.spring.depositcert.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * DTO cho việc khai báo thông tin Công ty chứng khoán (Bước 2)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}