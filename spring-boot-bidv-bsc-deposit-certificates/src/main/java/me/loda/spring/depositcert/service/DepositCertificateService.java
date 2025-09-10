package me.loda.spring.depositcert.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.loda.spring.depositcert.dto.CertificatePurchaseRequest;
import me.loda.spring.depositcert.dto.PrimaryDepositCertificateRequest;
import me.loda.spring.depositcert.dto.SecuritiesCompanyRequest;
import me.loda.spring.depositcert.entity.PrimaryDepositCertificate;
import me.loda.spring.depositcert.entity.PurchaseContract;
import me.loda.spring.depositcert.entity.SecuritiesCompany;
import me.loda.spring.depositcert.repository.PrimaryDepositCertificateRepository;
import me.loda.spring.depositcert.repository.PurchaseContractRepository;
import me.loda.spring.depositcert.repository.SecuritiesCompanyRepository;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service chính cho luồng phát hành thứ cấp giữa BIDV và BSC
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DepositCertificateService {

    private final PrimaryDepositCertificateRepository certificateRepository;
    private final SecuritiesCompanyRepository companyRepository;
    private final PurchaseContractRepository contractRepository;
    private final CoreBankingService coreBankingService;
    private final BscService bscService;

    /**
     * Bước 1: Nghiệp vụ khai báo chứng chỉ tiền gửi sơ cấp
     */
    @Transactional
    public PrimaryDepositCertificate declarePrimaryCertificate(PrimaryDepositCertificateRequest request) {
        log.info("Bước 1: Khai báo chứng chỉ tiền gửi sơ cấp - Serial: {}", request.getSerial());

        // Kiểm tra trùng lặp serial
        if (certificateRepository.findBySerial(request.getSerial()).isPresent()) {
            throw new IllegalArgumentException("Serial đã tồn tại: " + request.getSerial());
        }

        PrimaryDepositCertificate certificate = new PrimaryDepositCertificate();
        certificate.setSerial(request.getSerial());
        certificate.setCertificateName(request.getCertificateName());
        certificate.setPrimaryTermDays(request.getPrimaryTermDays());
        certificate.setFaceValue(request.getFaceValue());
        certificate.setQuantity(request.getQuantity());
        certificate.setTotalAmount(request.getTotalAmount());
        certificate.setPrimaryInterestRate(request.getPrimaryInterestRate());
        certificate.setInterestPaymentPeriod(request.getInterestPaymentPeriod());

        PrimaryDepositCertificate saved = certificateRepository.save(certificate);
        log.info("Đã khai báo thành công chứng chỉ tiền gửi - ID: {}, Serial: {}", saved.getId(), saved.getSerial());

        return saved;
    }

    /**
     * Bước 2: Nghiệp vụ khai báo thông tin Công ty chứng khoán
     */
    @Transactional
    public SecuritiesCompany declareSecuritiesCompany(SecuritiesCompanyRequest request) {
        log.info("Bước 2: Khai báo thông tin Công ty chứng khoán - CIF: {}", request.getCif());

        // Kiểm tra trùng lặp CIF
        Optional<SecuritiesCompany> existing = companyRepository.findByCif(request.getCif());
        if (existing.isPresent()) {
            log.info("Công ty chứng khoán đã tồn tại - CIF: {}, cập nhật thông tin", request.getCif());
            SecuritiesCompany company = existing.get();
            updateCompanyInfo(company, request);
            return companyRepository.save(company);
        }

        SecuritiesCompany company = new SecuritiesCompany();
        company.setCif(request.getCif());
        company.setPartnerName(request.getPartnerName());
        company.setBankAccountNumber(request.getBankAccountNumber());
        company.setBankName(request.getBankName());
        company.setContactEmail(request.getContactEmail());
        company.setContactPhone(request.getContactPhone());
        company.setAddress(request.getAddress());

        SecuritiesCompany saved = companyRepository.save(company);
        log.info("Đã khai báo thành công Công ty chứng khoán - ID: {}, CIF: {}", saved.getId(), saved.getCif());

        return saved;
    }

    /**
     * Bước 3 & 4: BSC đăng ký mua CCTG sơ cấp và xử lý toàn bộ luồng
     */
    @Transactional
    public PurchaseContract processCertificatePurchase(CertificatePurchaseRequest request) {
        log.info("Bước 3 & 4: Xử lý đăng ký mua CCTG - Company CIF: {}, Certificate Serial: {}, Quantity: {}",
                request.getCompanyFif(), request.getCertificateSerial(), request.getPurchaseQuantity());

        // Kiểm tra công ty chứng khoán
        SecuritiesCompany company = companyRepository.findByCif(request.getCompanyFif())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy công ty chứng khoán với CIF: " + request.getCompanyFif()));

        // Kiểm tra chứng chỉ tiền gửi và số lượng
        PrimaryDepositCertificate certificate = certificateRepository
                .findBySerialWithSufficientQuantity(request.getCertificateSerial(), request.getPurchaseQuantity())
                .orElseThrow(() -> new IllegalArgumentException("Không đủ chứng chỉ tiền gửi khả dụng"));

        // Tính tổng số tiền
        BigDecimal totalAmount = certificate.getFaceValue().multiply(new BigDecimal(request.getPurchaseQuantity()));

        // Tạo hợp đồng mua
        PurchaseContract contract = createPurchaseContract(company, certificate, request, totalAmount);
        contract = contractRepository.save(contract);

        try {
            // Cập nhật số lượng khả dụng
            certificate.setAvailableQuantity(certificate.getAvailableQuantity() - request.getPurchaseQuantity());
            if (certificate.getAvailableQuantity() == 0) {
                certificate.setStatus(PrimaryDepositCertificate.CertificateStatus.SOLD_OUT);
            }
            certificateRepository.save(certificate);

            // Thực hiện hạch toán
            contract = performAccountingWithRetry(contract, company, totalAmount);

            // Cập nhật BSC nếu hạch toán thành công
            if (contract.getStatus() == PurchaseContract.ContractStatus.ACCOUNTING_SUCCESS) {
                contract = updateBscWithRetry(contract, company, certificate, request.getPurchaseQuantity(), totalAmount);
            }

        } catch (Exception e) {
            log.error("Lỗi trong quá trình xử lý hợp đồng: {}", e.getMessage());
            contract.setStatus(PurchaseContract.ContractStatus.FAILED);
            contract.setErrorMessage(e.getMessage());
            contract = contractRepository.save(contract);
        }

        return contract;
    }

    /**
     * Thực hiện hạch toán với retry logic
     */
    private PurchaseContract performAccountingWithRetry(PurchaseContract contract, SecuritiesCompany company, BigDecimal amount) {
        try {
            log.info("Thực hiện hạch toán cho hợp đồng: {}", contract.getContractNumber());

            String transactionId = coreBankingService.performAccounting(
                    company.getBankAccountNumber(),  // Tài khoản BSC
                    "BIDV-DEPOSIT-ACCOUNT",          // Tài khoản BIDV
                    amount,
                    "Mua chứng chỉ tiền gửi - " + contract.getContractNumber()
            );

            contract.setAccountingTransactionId(transactionId);
            contract.setStatus(PurchaseContract.ContractStatus.ACCOUNTING_SUCCESS);
            log.info("Hạch toán thành công cho hợp đồng: {}, Transaction ID: {}", contract.getContractNumber(), transactionId);

        } catch (CoreBankingService.AccountingException e) {
            log.error("Hạch toán thất bại cho hợp đồng: {}, Lỗi: {}", contract.getContractNumber(), e.getMessage());
            contract.setStatus(PurchaseContract.ContractStatus.ACCOUNTING_FAILED);
            contract.setErrorMessage(e.getMessage());
        }

        return contractRepository.save(contract);
    }

    /**
     * Cập nhật BSC với retry logic
     */
    private PurchaseContract updateBscWithRetry(PurchaseContract contract, SecuritiesCompany company,
                                                PrimaryDepositCertificate certificate, Long quantity, BigDecimal amount) {
        try {
            log.info("Cập nhật BSC cho hợp đồng: {}", contract.getContractNumber());

            boolean success = bscService.updateCertificateBalance(
                    company.getCif(),
                    certificate.getSerial(),
                    quantity,
                    amount
            );

            if (success) {
                contract.setStatus(PurchaseContract.ContractStatus.COMPLETED);
                log.info("Cập nhật BSC thành công cho hợp đồng: {}", contract.getContractNumber());
            }

        } catch (BscService.BscUpdateException e) {
            log.error("Cập nhật BSC thất bại cho hợp đồng: {}, Lỗi: {}", contract.getContractNumber(), e.getMessage());
            contract.setStatus(PurchaseContract.ContractStatus.BSC_UPDATE_FAILED);
            contract.setErrorMessage(e.getMessage());
            contract.setRetryCount(contract.getRetryCount() + 1);

            // Nếu retry nhiều lần vẫn thất bại, thực hiện hoàn tiền
            if (contract.getRetryCount() >= 3) {
                performReverseTransaction(contract, company, amount);
            }
        }

        return contractRepository.save(contract);
    }

    /**
     * Thực hiện hoàn tiền
     */
    private void performReverseTransaction(PurchaseContract contract, SecuritiesCompany company, BigDecimal amount) {
        try {
            log.info("Thực hiện hoàn tiền cho hợp đồng: {}", contract.getContractNumber());

            String reverseTransactionId = coreBankingService.reverseTransaction(
                    contract.getAccountingTransactionId(),
                    "BIDV-DEPOSIT-ACCOUNT",
                    company.getBankAccountNumber(),
                    amount
            );

            contract.setStatus(PurchaseContract.ContractStatus.REVERSED);
            contract.setErrorMessage("Đã hoàn tiền do không thể cập nhật BSC. Reverse Transaction ID: " + reverseTransactionId);
            log.info("Hoàn tiền thành công cho hợp đồng: {}, Reverse Transaction ID: {}", 
                    contract.getContractNumber(), reverseTransactionId);

        } catch (CoreBankingService.AccountingException e) {
            log.error("Hoàn tiền thất bại cho hợp đồng: {}, Lỗi: {}", contract.getContractNumber(), e.getMessage());
            contract.setErrorMessage(contract.getErrorMessage() + ". Hoàn tiền thất bại: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách chứng chỉ tiền gửi khả dụng
     */
    public List<PrimaryDepositCertificate> getAvailableCertificates() {
        return certificateRepository.findAvailableCertificates();
    }

    /**
     * Lấy danh sách hợp đồng theo CIF công ty
     */
    public List<PurchaseContract> getContractsByCompany(String cif) {
        return contractRepository.findBySecuritiesCompanyCif(cif);
    }

    // Helper methods
    private void updateCompanyInfo(SecuritiesCompany company, SecuritiesCompanyRequest request) {
        company.setPartnerName(request.getPartnerName());
        company.setBankAccountNumber(request.getBankAccountNumber());
        company.setBankName(request.getBankName());
        company.setContactEmail(request.getContactEmail());
        company.setContactPhone(request.getContactPhone());
        company.setAddress(request.getAddress());
    }

    private PurchaseContract createPurchaseContract(SecuritiesCompany company, PrimaryDepositCertificate certificate,
                                                   CertificatePurchaseRequest request, BigDecimal totalAmount) {
        PurchaseContract contract = new PurchaseContract();
        contract.setContractNumber("CONTRACT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        contract.setSecuritiesCompany(company);
        contract.setPrimaryCertificate(certificate);
        contract.setBuyerCif(request.getBuyerCif());
        contract.setBuyerName(request.getBuyerName());
        contract.setPurchaseQuantity(request.getPurchaseQuantity());
        contract.setTotalAmount(totalAmount);
        contract.setStatus(PurchaseContract.ContractStatus.PENDING);
        return contract;
    }
}