package uk.gov.pmrv.api.settings.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.settings.domain.dto.FeeRowDTO;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Repository
public interface SettingsFeeRepository extends JpaRepository<PaymentFeeMethod, Long> {

    Optional<PaymentFeeMethod> findByCompetentAuthorityAndRequestType(CompetentAuthorityEnum competentAuthority,
                                                                      RequestType requestType);

    @Transactional(readOnly = true)
    @Query("""
            SELECT DISTINCT fm FROM PaymentFeeMethod fm
            JOIN FETCH fm.fees fees
            WHERE fm.id IN (
                SELECT DISTINCT fm2.id FROM PaymentFeeMethod fm2
                JOIN fm2.fees fees2
                WHERE VALUE(fees2).scheduledDate IS NOT NULL
                AND VALUE(fees2).scheduledDate <= :today
                AND VALUE(fees2).scheduledAmount IS NOT NULL
            )
            """)
    List<PaymentFeeMethod> findWithDueScheduledFees(LocalDate today);

    @Transactional(readOnly = true)
    @Query("""
            SELECT new uk.gov.pmrv.api.settings.domain.dto.FeeRowDTO(
                fm.id, fm.requestType, KEY(fees), VALUE(fees).amount, VALUE(fees).scheduledAmount, VALUE(fees).scheduledDate)
            FROM PaymentFeeMethod fm
            JOIN fm.fees fees
            WHERE fm.competentAuthority = :competentAuthority
            AND VALUE(fees).changeable = true
            ORDER BY fm.requestType, KEY(fees)
            """)
    List<FeeRowDTO> findChangeableFeesByCompetentAuthority(CompetentAuthorityEnum competentAuthority);
}
