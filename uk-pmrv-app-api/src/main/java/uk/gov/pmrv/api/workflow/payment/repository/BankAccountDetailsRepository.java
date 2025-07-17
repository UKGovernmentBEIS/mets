package uk.gov.pmrv.api.workflow.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.payment.domain.BankAccountDetails;

import java.util.Optional;

@Repository
public interface BankAccountDetailsRepository extends JpaRepository<BankAccountDetails, Long> {

    @Transactional(readOnly = true)
    Optional<BankAccountDetails> findByCompetentAuthority(CompetentAuthorityEnum competentAuthority);
}
