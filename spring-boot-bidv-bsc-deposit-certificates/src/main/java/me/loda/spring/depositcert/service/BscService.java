package me.loda.spring.depositcert.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Service mô phỏng hệ thống BSC
 * Cập nhật số dư chứng chỉ tiền gửi bên BSC
 */
@Service
public class BscService {

    private static final Logger log = LoggerFactory.getLogger(BscService.class);
    private final Random random = new Random();

    /**
     * Cập nhật số dư chứng chỉ tiền gửi bên BSC
     * 
     * @param companyCif CIF công ty chứng khoán
     * @param certificateSerial Serial chứng chỉ tiền gửi
     * @param quantity Số lượng chứng chỉ
     * @param amount Số tiền
     * @return true nếu cập nhật thành công
     * @throws BscUpdateException nếu cập nhật thất bại
     */
    @Retryable(value = {BscUpdateException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public boolean updateCertificateBalance(String companyCif, String certificateSerial, Long quantity, BigDecimal amount) 
            throws BscUpdateException {
        
        log.info("Đang cập nhật số dư chứng chỉ tiền gửi BSC - CIF: {}, Serial: {}, Số lượng: {}, Số tiền: {}", 
                companyCif, certificateSerial, quantity, amount);
        
        // Mô phỏng thời gian xử lý
        try {
            Thread.sleep(500 + random.nextInt(1500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Mô phỏng lỗi ngẫu nhiên (15% tỷ lệ lỗi)
        if (random.nextInt(100) < 15) {
            log.error("Cập nhật BSC thất bại cho CIF: {}, Serial: {}", companyCif, certificateSerial);
            throw new BscUpdateException("Lỗi hệ thống BSC - Không thể cập nhật số dư chứng chỉ tiền gửi");
        }
        
        log.info("Cập nhật BSC thành công cho CIF: {}, Serial: {}", companyCif, certificateSerial);
        return true;
    }

    /**
     * Kiểm tra trạng thái kết nối với BSC
     * 
     * @return true nếu kết nối OK
     */
    public boolean isServiceAvailable() {
        // Mô phỏng kiểm tra kết nối
        return random.nextInt(100) < 95; // 95% khả năng kết nối OK
    }

    /**
     * Exception cho lỗi cập nhật BSC
     */
    public static class BscUpdateException extends Exception {
        public BscUpdateException(String message) {
            super(message);
        }
    }
}