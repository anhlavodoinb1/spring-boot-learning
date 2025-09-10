package me.loda.spring.depositcert.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * DTO cho việc BSC đăng ký mua CCTG sơ cấp (Bước 3)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}