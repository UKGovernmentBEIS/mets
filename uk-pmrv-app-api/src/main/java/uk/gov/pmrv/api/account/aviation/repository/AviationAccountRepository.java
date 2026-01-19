package uk.gov.pmrv.api.account.aviation.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountIdAndNameDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;
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
    @Query("SELECT a.id FROM AviationAccount a WHERE a.status IN :statuses")
    List<Long> findIdsByStatusIn(@Param("statuses") List<AviationAccountStatus> accountStatuses);
    
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

    boolean existsByIdAndRegistryIdIsNotNull(Long id);
    
    /**
     * @param competentAuthority must not be null
     * @param statuses Must not be null. If empty, no filtering is applied on the field
     * @param emissionTradingSchemes Must not be null. If empty, no filtering is applied on the field
     * @param reportingStatuses Must not be null. if empty, no filtering is applied on the field
     */
	@Transactional(readOnly = true)
    @Query(value = "SELECT acc.id as accountId, acc.name as accountName "
            + "FROM account acc "
            + "INNER JOIN account_aviation acc_av ON acc_av.id = acc.id "
            + "WHERE acc.competent_authority = :#{#competentAuthority.name()} "
            + "AND (:#{#statuses.size() == 0} = true OR acc_av.status IN :#{#statuses.![name()]}) "
            + "AND (:#{#emissionTradingSchemes.size() == 0} = true OR acc.emission_trading_scheme IN :#{#emissionTradingSchemes.![name()]}) "
            + "AND (:#{#reportingStatuses.size() == 0} = true OR EXISTS ("
            + "    SELECT 1 FROM account_aviation_reporting_status rs "
            + "    WHERE rs.account_id = acc_av.id "
            + "    AND rs.status IN :#{#reportingStatuses.![name()]} "
            + "    AND ("
            + "        rs.year = EXTRACT(YEAR FROM CURRENT_DATE) - 1 "
            + "        OR (rs.year = EXTRACT(YEAR FROM CURRENT_DATE) AND NOT EXISTS ("
            + "            SELECT 1 FROM account_aviation_reporting_status rs_sub "
            + "            WHERE rs_sub.account_id = acc_av.id AND rs_sub.year = EXTRACT(YEAR FROM CURRENT_DATE) - 1"
            + "        ))"
            + "    )"
            + "))", nativeQuery = true)
	Set<AviationAccountIdAndNameDTO> findAllByCAAndStatusesAndEmissionTradingSchemesAndReportingStatuses(
			CompetentAuthorityEnum competentAuthority, Set<AviationAccountStatus> statuses,
			Set<EmissionTradingScheme> emissionTradingSchemes, Set<AviationAccountReportingStatusType> reportingStatuses);

}
