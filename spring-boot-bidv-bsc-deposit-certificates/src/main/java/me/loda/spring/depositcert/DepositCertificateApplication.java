package me.loda.spring.depositcert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

/**
 * BIDV-BSC Secondary Deposit Certificate Issuance Flow Application
 * 
 * This application demonstrates the secondary issuance flow between
 * BIDV bank and BSC securities company for deposit certificates.
 */
@SpringBootApplication
@EnableRetry
public class DepositCertificateApplication {

    public static void main(String[] args) {
        SpringApplication.run(DepositCertificateApplication.class, args);
    }
}