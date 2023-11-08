package uk.gov.pmrv.api.emissionsmonitoringplan.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpIssuingAuthority;

import java.util.List;

public interface EmpIssuingAuthorityRepository extends JpaRepository<EmpIssuingAuthority, Long> {

    @Transactional(readOnly = true)
    @Query(name = EmpIssuingAuthority.NAMED_QUERY_FIND_ALL_ISSUING_AUTHORITY_NAMES)
    List<String> findAllIssuingAuthorityNames();

    @Transactional(readOnly = true)
    boolean existsByName(String name);
}
