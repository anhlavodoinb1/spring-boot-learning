package me.loda.spring.depositcert.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Service mô phỏng hệ thống Core Banking
 * Thực hiện hạch toán ghi nợ tài khoản BSC, ghi có tài khoản BIDV
 */
@Service
public class CoreBankingService {

    private static final Logger log = LoggerFactory.getLogger(CoreBankingService.class);
    private final Random random = new Random();

    /**
     * Thực hiện hạch toán tiền
     * 
     * @param fromAccount Tài khoản nguồn (BSC)
     * @param toAccount Tài khoản đích (BIDV)
     * @param amount Số tiền
     * @param description Mô tả giao dịch
     * @return ID giao dịch nếu thành công
     * @throws AccountingException nếu hạch toán thất bại
     */
    @Retryable(value = {AccountingException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public String performAccounting(String fromAccount, String toAccount, BigDecimal amount, String description) 
            throws AccountingException {
        
        log.info("Đang thực hiện hạch toán: {} -> {}, số tiền: {}, mô tả: {}", 
                fromAccount, toAccount, amount, description);
        
        // Mô phỏng thời gian xử lý
        try {
            Thread.sleep(1000 + random.nextInt(2000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Mô phỏng lỗi ngẫu nhiên (20% tỷ lệ lỗi)
        if (random.nextInt(100) < 20) {
            log.error("Hạch toán thất bại cho giao dịch: {} -> {}", fromAccount, toAccount);
            throw new AccountingException("Lỗi hệ thống core banking - Không thể thực hiện giao dịch");
        }
        
        String transactionId = "TXN" + System.currentTimeMillis() + "_" + random.nextInt(1000);
        log.info("Hạch toán thành công với ID giao dịch: {}", transactionId);
        
        return transactionId;
    }

    /**
     * Hoàn tiền (reverse transaction)
     * 
     * @param originalTransactionId ID giao dịch gốc
     * @param fromAccount Tài khoản nguồn (BIDV)
     * @param toAccount Tài khoản đích (BSC)
     * @param amount Số tiền
     * @return ID giao dịch hoàn tiền
     * @throws AccountingException nếu hoàn tiền thất bại
     */
    public String reverseTransaction(String originalTransactionId, String fromAccount, String toAccount, BigDecimal amount) 
            throws AccountingException {
        
        log.info("Đang thực hiện hoàn tiền cho giao dịch gốc: {}, {} -> {}, số tiền: {}", 
                originalTransactionId, fromAccount, toAccount, amount);
        
        // Mô phỏng thời gian xử lý
        try {
            Thread.sleep(500 + random.nextInt(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Hoàn tiền có tỷ lệ thành công cao hơn (5% tỷ lệ lỗi)
        if (random.nextInt(100) < 5) {
            log.error("Hoàn tiền thất bại cho giao dịch: {}", originalTransactionId);
            throw new AccountingException("Lỗi hệ thống core banking - Không thể hoàn tiền");
        }
        
        String reverseTransactionId = "REV" + System.currentTimeMillis() + "_" + random.nextInt(1000);
        log.info("Hoàn tiền thành công với ID giao dịch: {}", reverseTransactionId);
        
        return reverseTransactionId;
    }

    /**
     * Exception cho lỗi hạch toán
     */
    public static class AccountingException extends Exception {
        public AccountingException(String message) {
            super(message);
        }
    }
}