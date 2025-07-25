package uk.gov.pmrv.api.workflow.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Optional;

@Repository
public interface PaymentFeeMethodRepository extends JpaRepository<PaymentFeeMethod, Long> {

    @Transactional(readOnly = true)
    Optional<PaymentFeeMethod> findByCompetentAuthorityAndRequestType(CompetentAuthorityEnum competentAuthority,
                                                                      RequestType requestType);

    @Transactional(readOnly = true)
    Optional<PaymentFeeMethod> findByCompetentAuthorityAndRequestTypeAndType(CompetentAuthorityEnum competentAuthority,
                                                                             RequestType requestType, FeeMethodType feeMethodType);

}
