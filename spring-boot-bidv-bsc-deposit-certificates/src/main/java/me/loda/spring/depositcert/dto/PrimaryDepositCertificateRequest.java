package me.loda.spring.depositcert.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * DTO cho việc khai báo chứng chỉ tiền gửi sơ cấp (Bước 1)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}