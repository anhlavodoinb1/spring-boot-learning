package me.loda.spring.depositcert.repository;

import me.loda.spring.depositcert.entity.PurchaseContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseContractRepository extends JpaRepository<PurchaseContract, Long> {

    Optional<PurchaseContract> findByContractNumber(String contractNumber);

    List<PurchaseContract> findByStatus(PurchaseContract.ContractStatus status);

    @Query("SELECT p FROM PurchaseContract p WHERE p.status IN :statuses")
    List<PurchaseContract> findByStatusIn(@Param("statuses") List<PurchaseContract.ContractStatus> statuses);

    @Query("SELECT p FROM PurchaseContract p WHERE p.securitiesCompany.cif = :cif")
    List<PurchaseContract> findBySecuritiesCompanyCif(@Param("cif") String cif);

    @Query("SELECT p FROM PurchaseContract p WHERE p.status = 'BSC_UPDATE_FAILED' AND p.retryCount < :maxRetryCount")
    List<PurchaseContract> findFailedContractsForRetry(@Param("maxRetryCount") Integer maxRetryCount);
}