package uk.gov.pmrv.api.account.aviation.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountIdAndNameDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.repository.AccountBaseRepository;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AviationAccountRepository extends AccountBaseRepository<AviationAccount>, AviationAccountCustomRepository {

    @Transactional(readOnly = true)
    @EntityGraph(value = "reporting-status-history-graph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<AviationAccount> findAviationAccountById(Long id);

    @Transactional(readOnly = true)
    List<AviationAccount> findAllByStatusIn(List<AviationAccountStatus> accountStatuses);
    
    @Transactional(readOnly = true)
    List<AviationAccount> findAllByEmissionTradingSchemeAndStatusInAndMigratedAccountIdNotNull(
    		EmissionTradingScheme scheme, List<AviationAccountStatus> accountStatuses);
    
    @Transactional(readOnly = true)
    List<AviationAccount> findByEmissionTradingSchemeAndStatusInAndMigratedAccountIdIn(
    		EmissionTradingScheme scheme, List<AviationAccountStatus> accountStatuses, List<String> ids);

    @Transactional(readOnly = true)
    boolean existsByNameAndCompetentAuthorityAndEmissionTradingScheme(String name, CompetentAuthorityEnum ca, EmissionTradingScheme ets);

    @Transactional(readOnly = true)
    boolean existsByCrcoCodeAndCompetentAuthorityAndEmissionTradingScheme(String crcoCode, CompetentAuthorityEnum ca, EmissionTradingScheme ets);
    
    @Transactional(readOnly = true)
    boolean existsByNameAndCompetentAuthorityAndEmissionTradingSchemeAndIdNot(String name, CompetentAuthorityEnum ca, EmissionTradingScheme ets, Long accountId);
    
    @Transactional(readOnly = true)
    boolean existsByCrcoCodeAndCompetentAuthorityAndEmissionTradingSchemeAndIdNot(String crcoCode, CompetentAuthorityEnum ca, EmissionTradingScheme ets, Long accountId);
    
    /**
     * @param competentAuthority must not be null
     * @param statuses Must not be null. If empty, no filtering is applied on the field
     * @param emissionTradingSchemes Must not be null. If empty, no filtering is applied on the field
     * @param reportingStatuses Must not be null. if empty, no filtering is applied on the field
     */
	@Transactional(readOnly = true)
	@Query(value = "select acc.id as accountId, acc.name as accountName "
			+ "from account acc "
			+ "inner join account_aviation acc_av on acc_av.id = acc.id "
			+ "where acc.competent_authority = :#{#competentAuthority.name()} "
			+ "and (:#{#statuses.size() == 0} = true or acc_av.status in :#{#statuses.![name()]}) "
			+ "and (:#{#emissionTradingSchemes.size() == 0} = true or acc.emission_trading_scheme in :#{#emissionTradingSchemes.![name()]}) "
			+ "and (:#{#reportingStatuses.size() == 0} = true or acc_av.reporting_status in :#{#reportingStatuses.![name()]}) ", nativeQuery = true)
	Set<AviationAccountIdAndNameDTO> findAllByCAAndStatusesAndEmissionTradingSchemesAndReportingStatuses(
			CompetentAuthorityEnum competentAuthority, Set<AviationAccountStatus> statuses,
			Set<EmissionTradingScheme> emissionTradingSchemes, Set<AviationAccountReportingStatus> reportingStatuses);

}
