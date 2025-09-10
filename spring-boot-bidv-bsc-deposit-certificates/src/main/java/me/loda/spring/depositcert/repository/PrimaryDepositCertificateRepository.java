package me.loda.spring.depositcert.repository;

import me.loda.spring.depositcert.entity.PrimaryDepositCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrimaryDepositCertificateRepository extends JpaRepository<PrimaryDepositCertificate, Long> {

    Optional<PrimaryDepositCertificate> findBySerial(String serial);

    @Query("SELECT p FROM PrimaryDepositCertificate p WHERE p.status = 'ACTIVE' AND p.availableQuantity > 0")
    List<PrimaryDepositCertificate> findAvailableCertificates();

    @Query("SELECT p FROM PrimaryDepositCertificate p WHERE p.serial = :serial AND p.availableQuantity >= :quantity")
    Optional<PrimaryDepositCertificate> findBySerialWithSufficientQuantity(@Param("serial") String serial, 
                                                                           @Param("quantity") Long quantity);
}