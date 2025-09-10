package me.loda.spring.depositcert.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.loda.spring.depositcert.dto.ApiResponse;
import me.loda.spring.depositcert.dto.CertificatePurchaseRequest;
import me.loda.spring.depositcert.dto.PrimaryDepositCertificateRequest;
import me.loda.spring.depositcert.dto.SecuritiesCompanyRequest;
import me.loda.spring.depositcert.entity.PrimaryDepositCertificate;
import me.loda.spring.depositcert.entity.PurchaseContract;
import me.loda.spring.depositcert.entity.SecuritiesCompany;
import me.loda.spring.depositcert.service.DepositCertificateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller cho luồng phát hành thứ cấp giữa BIDV và BSC
 * 
 * API Endpoints:
 * - POST /certificates - Bước 1: Khai báo chứng chỉ tiền gửi sơ cấp
 * - POST /companies - Bước 2: Khai báo thông tin Công ty chứng khoán
 * - POST /purchase - Bước 3&4: BSC đăng ký mua CCTG và xử lý luồng
 * - GET /certificates/available - Lấy danh sách chứng chỉ khả dụng
 * - GET /contracts/{cif} - Lấy danh sách hợp đồng theo CIF công ty
 */
@RestController
@RequestMapping("/deposit-certificates")
@RequiredArgsConstructor
@Validated
@Slf4j
public class DepositCertificateController {

    private final DepositCertificateService depositCertificateService;

    /**
     * Bước 1: Nghiệp vụ khai báo chứng chỉ tiền gửi sơ cấp
     */
    @PostMapping("/certificates")
    public ResponseEntity<ApiResponse<PrimaryDepositCertificate>> declarePrimaryCertificate(
            @Valid @RequestBody PrimaryDepositCertificateRequest request) {
        
        log.info("API - Bước 1: Khai báo chứng chỉ tiền gửi sơ cấp - Serial: {}", request.getSerial());
        
        try {
            PrimaryDepositCertificate certificate = depositCertificateService.declarePrimaryCertificate(request);
            return ResponseEntity.ok(ApiResponse.success("Khai báo chứng chỉ tiền gửi sơ cấp thành công", certificate));
            
        } catch (IllegalArgumentException e) {
            log.error("Lỗi khai báo chứng chỉ: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "CERTIFICATE_DECLARATION_ERROR"));
                    
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi khai báo chứng chỉ: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống", "SYSTEM_ERROR"));
        }
    }

    /**
     * Bước 2: Nghiệp vụ khai báo thông tin Công ty chứng khoán
     */
    @PostMapping("/companies")
    public ResponseEntity<ApiResponse<SecuritiesCompany>> declareSecuritiesCompany(
            @Valid @RequestBody SecuritiesCompanyRequest request) {
        
        log.info("API - Bước 2: Khai báo thông tin Công ty chứng khoán - CIF: {}", request.getCif());
        
        try {
            SecuritiesCompany company = depositCertificateService.declareSecuritiesCompany(request);
            return ResponseEntity.ok(ApiResponse.success("Khai báo thông tin Công ty chứng khoán thành công", company));
            
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi khai báo công ty: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống", "SYSTEM_ERROR"));
        }
    }

    /**
     * Bước 3 & 4: BSC đăng ký mua CCTG sơ cấp và xử lý toàn bộ luồng
     */
    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<PurchaseContract>> processCertificatePurchase(
            @Valid @RequestBody CertificatePurchaseRequest request) {
        
        log.info("API - Bước 3&4: BSC đăng ký mua CCTG - Company CIF: {}, Certificate Serial: {}", 
                request.getCompanyFif(), request.getCertificateSerial());
        
        try {
            PurchaseContract contract = depositCertificateService.processCertificatePurchase(request);
            
            // Trả về response phù hợp với trạng thái hợp đồng
            if (contract.getStatus() == PurchaseContract.ContractStatus.COMPLETED) {
                return ResponseEntity.ok(ApiResponse.success("Đăng ký mua chứng chỉ tiền gửi thành công", contract));
            } else if (contract.getStatus() == PurchaseContract.ContractStatus.FAILED) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Đăng ký mua chứng chỉ tiền gửi thất bại: " + contract.getErrorMessage(), 
                                "PURCHASE_FAILED"));
            } else {
                return ResponseEntity.ok(ApiResponse.success("Đăng ký mua chứng chỉ tiền gửi đang xử lý", contract));
            }
            
        } catch (IllegalArgumentException e) {
            log.error("Lỗi đăng ký mua chứng chỉ: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "PURCHASE_VALIDATION_ERROR"));
                    
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi đăng ký mua chứng chỉ: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống", "SYSTEM_ERROR"));
        }
    }

    /**
     * Lấy danh sách chứng chỉ tiền gửi khả dụng
     */
    @GetMapping("/certificates/available")
    public ResponseEntity<ApiResponse<List<PrimaryDepositCertificate>>> getAvailableCertificates() {
        
        try {
            List<PrimaryDepositCertificate> certificates = depositCertificateService.getAvailableCertificates();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách chứng chỉ khả dụng thành công", certificates));
            
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi lấy danh sách chứng chỉ: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống", "SYSTEM_ERROR"));
        }
    }

    /**
     * Lấy danh sách hợp đồng theo CIF công ty
     */
    @GetMapping("/contracts/{cif}")
    public ResponseEntity<ApiResponse<List<PurchaseContract>>> getContractsByCompany(@PathVariable String cif) {
        
        try {
            List<PurchaseContract> contracts = depositCertificateService.getContractsByCompany(cif);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách hợp đồng thành công", contracts));
            
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi lấy danh sách hợp đồng: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống", "SYSTEM_ERROR"));
        }
    }
}