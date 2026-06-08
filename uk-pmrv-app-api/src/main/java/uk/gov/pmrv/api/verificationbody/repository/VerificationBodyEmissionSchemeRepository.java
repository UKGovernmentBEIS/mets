package uk.gov.pmrv.api.verificationbody.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBodyEmissionScheme;

@Repository
public interface VerificationBodyEmissionSchemeRepository extends JpaRepository<VerificationBodyEmissionScheme, Long> {

    @Transactional(readOnly = true)
    boolean existsByAccreditationReferenceNumber(String accreditationReferenceNumber);

    @Transactional(readOnly = true)
    boolean existsByAccreditationReferenceNumberAndVerificationBodyIdNot(String accreditationReferenceNumber, Long verificationBodyId);

}
