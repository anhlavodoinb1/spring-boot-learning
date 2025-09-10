package me.loda.spring.depositcert.repository;

import me.loda.spring.depositcert.entity.SecuritiesCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecuritiesCompanyRepository extends JpaRepository<SecuritiesCompany, Long> {

    Optional<SecuritiesCompany> findByCif(String cif);

    List<SecuritiesCompany> findByStatus(SecuritiesCompany.CompanyStatus status);

    boolean existsByCif(String cif);
}