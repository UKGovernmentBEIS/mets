package uk.gov.pmrv.api.account.installation.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountIdAndNameAndLegalEntityNameDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.domain.enumeration.TransferCodeStatus;
import uk.gov.pmrv.api.account.repository.AccountBaseRepository;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface InstallationAccountRepository extends AccountBaseRepository<InstallationAccount>, InstallationAccountCustomRepository {

    @Transactional(readOnly = true)
    @Query(name = InstallationAccount.NAMED_QUERY_FIND_INSTALLATION_ACCOUNT_FULL_INFO_BY_ID)
    Optional<InstallationAccount> findAccountFullInfoById(Long id);

    @Transactional(readOnly = true)
    @Query(name = InstallationAccount.NAMED_QUERY_FIND_INSTALLATION_ACCOUNT_WITH_LOC_AND_LE_BY_ID)
    Optional<InstallationAccount> findAccountWithLocAndLeWithLocById(Long id);

    @Transactional(readOnly = true)
    @Query(name = InstallationAccount.NAMED_QUERY_FIND_INSTALLATION_ACCOUNT_WITH_LE_BY_ID)
    Optional<InstallationAccount> findAccountWithLeById(Long id);

    @Transactional(readOnly = true)
    List<InstallationAccount> findAllByStatusIs(InstallationAccountStatus status);

    @Transactional(readOnly = true)
    boolean existsByTransferCode(String transferCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InstallationAccount> findAccountByTransferCodeAndTransferCodeStatus(String transferCode, TransferCodeStatus transferCodeStatus);
    
    /**
     * @param competentAuthority must not be null
     * @param statuses Must not be null. If empty, no filtering is applied on the field
     * @param emitterTypes Must not be null. if empty, no filtering is applied on the field
     * @param installationCategories categories (applies for GHGE emitter types only). Must not be null. If empty, no filtering is applied on the field 
     */
	@Transactional(readOnly = true)
	@Query(value = "select ai.id as accountId, a.name as accountName, ale.name as legalEntityName "
			+ "from account_installation ai "
			+ "join account a on a.id = ai.id "
			+ "join account_legal_entity ale on ale.id = a.legal_entity_id "
			+ "where a.competent_authority = :#{#competentAuthority.name()} "
			+ "and (:#{#statuses.size() == 0} = true or ai.status in :#{#statuses.![name()]}) "
			+ "and (:#{#emitterTypes.size() == 0} = true or ai.emitter_type in :#{#emitterTypes.![name()]}) "
			+ "and (:#{#installationCategories.size() == 0} = true or ai.emitter_type = 'HSE' or ai.installation_category in :#{#installationCategories.![name()]}) ", nativeQuery = true)
	Set<InstallationAccountIdAndNameAndLegalEntityNameDTO> findAllByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(
			CompetentAuthorityEnum competentAuthority, Set<InstallationAccountStatus> statuses,
			Set<EmitterType> emitterTypes, Set<InstallationCategory> installationCategories);
}
