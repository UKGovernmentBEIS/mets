package uk.gov.pmrv.api.account.installation.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountIdAndNameAndLegalEntityNameDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.repository.InstallationAccountRepository;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class InstallationAccountRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private InstallationAccountRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAllByStatusIs() {
        InstallationAccount account1 = createAccount(1L, "account1", InstallationAccountStatus.LIVE,
            CompetentAuthorityEnum.ENGLAND, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName1", LegalEntityStatus.ACTIVE);
        createAccount(2L, "account2", InstallationAccountStatus.AWAITING_SURRENDER,
            CompetentAuthorityEnum.ENGLAND, 2L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName2", LegalEntityStatus.ACTIVE);
        InstallationAccount account3 = createAccount(3L, "account3", InstallationAccountStatus.LIVE,
            CompetentAuthorityEnum.ENGLAND, 3L, EmissionTradingScheme.UK_ETS_INSTALLATIONS,"leName3", LegalEntityStatus.ACTIVE);

        flushAndClear();

        List<InstallationAccount> result = repository.findAllByStatusIs(InstallationAccountStatus.LIVE);

        assertThat(result).containsExactlyInAnyOrder(account1, account3);
    }

    @Test
    void findByIdAndStatusNotIn() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        InstallationAccount account1 = createAccount(accountId1, "account1", InstallationAccountStatus.LIVE,
            CompetentAuthorityEnum.ENGLAND, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName1", LegalEntityStatus.ACTIVE);
        createAccount(accountId2, "account2", InstallationAccountStatus.UNAPPROVED,
            CompetentAuthorityEnum.ENGLAND, 2L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName2", LegalEntityStatus.ACTIVE);

        flushAndClear();

        Optional<InstallationAccount> result = repository.findByIdAndStatusNotIn(accountId1, List.of(InstallationAccountStatus.UNAPPROVED));

        assertThat(result).isNotEmpty();
        assertEquals(account1, result.get());
    }

    @Test
    void findAccountContactsByCaAndContactTypeAndStatusNotIn() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        AccountContactType serviceContactType = AccountContactType.SERVICE;

        InstallationAccount account1 = createAccount(1L, "account1", InstallationAccountStatus.LIVE,
            competentAuthority, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName1", LegalEntityStatus.ACTIVE);
        account1.getContacts().put(AccountContactType.PRIMARY, "primary1");
        account1.getContacts().put(AccountContactType.SERVICE, "service1");
        repository.save(account1);

        InstallationAccount account2 = createAccount(2L, "account2", InstallationAccountStatus.UNAPPROVED,
            competentAuthority, 2L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName2", LegalEntityStatus.ACTIVE);
        account2.getContacts().put(AccountContactType.PRIMARY, "primary2");
        account2.getContacts().put(AccountContactType.SERVICE, "service2");
        repository.save(account2);

        InstallationAccount account3 = createAccount(3L, "account3", InstallationAccountStatus.LIVE,
            CompetentAuthorityEnum.ENGLAND, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName3", LegalEntityStatus.ACTIVE);
        account3.getContacts().put(AccountContactType.PRIMARY, "primary3");
        account3.getContacts().put(AccountContactType.SERVICE, "service3");
        repository.save(account3);

        InstallationAccount account4 = createAccount(4L, "account4", InstallationAccountStatus.AWAITING_SURRENDER,
            competentAuthority, 2L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName4", LegalEntityStatus.ACTIVE);
        account4.getContacts().put(AccountContactType.PRIMARY, "primary4");
        repository.save(account4);

        List<AccountContactInfoDTO> expectedAccountContactInfos = List.of(
            AccountContactInfoDTO.builder().accountId(1L).accountName("account1").userId("service1").build(),
            AccountContactInfoDTO.builder().accountId(4L).accountName("account4").build()
        );

        flushAndClear();

        Page<AccountContactInfoDTO> result = repository.findAccountContactsByCaAndContactTypeAndStatusNotIn(PageRequest.of(0, 5), competentAuthority,
            serviceContactType, List.of(InstallationAccountStatus.UNAPPROVED));

        assertThat(result.getNumberOfElements()).isEqualTo(2);
        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedAccountContactInfos);
    }

    @Test
    void findAccountIdsByCaAndStatusNotIn() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        Long accountId3 = 3L;
        Long accountId4 = 4L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;

        createAccount(accountId1, "account1", InstallationAccountStatus.LIVE,
            competentAuthority, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName1", LegalEntityStatus.ACTIVE);
        createAccount(accountId2, "account2", InstallationAccountStatus.UNAPPROVED,
            competentAuthority, 2L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName2", LegalEntityStatus.ACTIVE);
        createAccount(accountId3, "account3", InstallationAccountStatus.LIVE,
            CompetentAuthorityEnum.ENGLAND, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName3", LegalEntityStatus.ACTIVE);
        createAccount(accountId4, "account4", InstallationAccountStatus.AWAITING_SURRENDER,
            competentAuthority, 2L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName4", LegalEntityStatus.ACTIVE);

        flushAndClear();

        List<Long> result = repository.findAccountIdsByCaAndStatusNotIn(competentAuthority, List.of(InstallationAccountStatus.UNAPPROVED));

        assertThat(result.size()).isEqualTo(2);
        assertThat(result).containsExactlyInAnyOrder(accountId1, accountId4);
    }

    @Test
    void findAllByCAAndStatusesAndInstallationCategoriesAndEmitterTypes() {
    	CompetentAuthorityEnum caEngland = CompetentAuthorityEnum.ENGLAND;
    	Long accountId1 = 1L;
    	Long accountId4 = 4L;

        createAccount(accountId1, "account1", InstallationAccountStatus.LIVE,
        		caEngland, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName1", LegalEntityStatus.ACTIVE, InstallationCategory.A, EmitterType.GHGE);
        createAccount(2L, "account2", InstallationAccountStatus.UNAPPROVED,
        		caEngland, 2L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName2", LegalEntityStatus.ACTIVE, InstallationCategory.A, EmitterType.GHGE);
        createAccount(3L, "account3", InstallationAccountStatus.LIVE,
        		caEngland, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName3", LegalEntityStatus.ACTIVE, InstallationCategory.B, EmitterType.GHGE);
        createAccount(accountId4, "account4", InstallationAccountStatus.LIVE,
        		caEngland, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName4", LegalEntityStatus.ACTIVE, InstallationCategory.N_A, EmitterType.HSE);
        createAccount(5L, "account5", InstallationAccountStatus.LIVE,
        		CompetentAuthorityEnum.WALES, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName5", LegalEntityStatus.ACTIVE, InstallationCategory.A, EmitterType.GHGE);

        flushAndClear();

		Set<InstallationAccountIdAndNameAndLegalEntityNameDTO> result = repository
				.findAllByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(
						caEngland,
						Set.of(InstallationAccountStatus.LIVE), 
						Set.of(EmitterType.GHGE, EmitterType.HSE),
						Set.of(InstallationCategory.A));

		assertThat(result).extracting(InstallationAccountIdAndNameAndLegalEntityNameDTO::getAccountId)
				.containsExactly(accountId1, accountId4);
    }
    
    @Test
    void findAllByCAAndStatusesAndInstallationCategoriesAndEmitterTypes_no_hse() {
    	CompetentAuthorityEnum caEngland = CompetentAuthorityEnum.ENGLAND;
    	Long accountId1 = 1L;

        createAccount(accountId1, "account1", InstallationAccountStatus.LIVE,
        		caEngland, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName1", LegalEntityStatus.ACTIVE, InstallationCategory.A, EmitterType.GHGE);
        createAccount(2L, "account2", InstallationAccountStatus.UNAPPROVED,
        		caEngland, 2L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName2", LegalEntityStatus.ACTIVE, InstallationCategory.A, EmitterType.GHGE);
        createAccount(3L, "account3", InstallationAccountStatus.LIVE,
        		caEngland, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName3", LegalEntityStatus.ACTIVE, InstallationCategory.B, EmitterType.GHGE);
        createAccount(4L, "account4", InstallationAccountStatus.LIVE,
        		caEngland, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName4", LegalEntityStatus.ACTIVE, InstallationCategory.N_A, EmitterType.HSE);
        createAccount(5L, "account5", InstallationAccountStatus.LIVE,
        		CompetentAuthorityEnum.WALES, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName5", LegalEntityStatus.ACTIVE, InstallationCategory.A, EmitterType.GHGE);

        flushAndClear();

		Set<InstallationAccountIdAndNameAndLegalEntityNameDTO> result = repository
				.findAllByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(
						caEngland,
						Set.of(InstallationAccountStatus.LIVE), 
						Set.of(EmitterType.GHGE),
						Set.of(InstallationCategory.A));

		assertThat(result).extracting(InstallationAccountIdAndNameAndLegalEntityNameDTO::getAccountId)
				.containsExactly(accountId1);
    }
    
    @Test
    void findAllByCAAndStatusesAndInstallationCategoriesAndEmitterTypes_no_ghge() {
    	CompetentAuthorityEnum caEngland = CompetentAuthorityEnum.ENGLAND;
    	Long accountId4 = 4L;

        createAccount(1L, "account1", InstallationAccountStatus.LIVE,
        		caEngland, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName1", LegalEntityStatus.ACTIVE, InstallationCategory.A, EmitterType.GHGE);
        createAccount(2L, "account2", InstallationAccountStatus.UNAPPROVED,
        		caEngland, 2L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName2", LegalEntityStatus.ACTIVE, InstallationCategory.A, EmitterType.GHGE);
        createAccount(3L, "account3", InstallationAccountStatus.LIVE,
        		caEngland, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName3", LegalEntityStatus.ACTIVE, InstallationCategory.B, EmitterType.GHGE);
        createAccount(accountId4, "account4", InstallationAccountStatus.LIVE,
        		caEngland, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName4", LegalEntityStatus.ACTIVE, InstallationCategory.N_A, EmitterType.HSE);
        createAccount(5L, "account5", InstallationAccountStatus.LIVE,
        		CompetentAuthorityEnum.WALES, 1L, EmissionTradingScheme.EU_ETS_INSTALLATIONS, "leName5", LegalEntityStatus.ACTIVE, InstallationCategory.A, EmitterType.GHGE);

        flushAndClear();

		Set<InstallationAccountIdAndNameAndLegalEntityNameDTO> result = repository
				.findAllByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(
						caEngland,
						Set.of(InstallationAccountStatus.LIVE), 
						Set.of(EmitterType.HSE),
						Set.of());

		assertThat(result).extracting(InstallationAccountIdAndNameAndLegalEntityNameDTO::getAccountId)
				.containsExactly(accountId4);
    }
    
	private InstallationAccount createAccount(Long id, String accountName, InstallationAccountStatus accountStatus,
			CompetentAuthorityEnum ca, Long verificationBodyId, EmissionTradingScheme ets, String leName,
			LegalEntityStatus leStatus) {
		return createAccount(id, accountName, accountStatus, ca, verificationBodyId, ets, leName, leStatus, null, null);
	}

	private InstallationAccount createAccount(Long id, String accountName, InstallationAccountStatus accountStatus,
			CompetentAuthorityEnum ca, Long verificationBodyId, EmissionTradingScheme ets, String leName,
			LegalEntityStatus leStatus, InstallationCategory installationCategory, EmitterType emitterType) {
        InstallationAccount account = InstallationAccount.builder()
            .id(id)
            .legalEntity(createLegalEntity(leName, leStatus))
            .accountType(AccountType.INSTALLATION)
            .emitterType(emitterType)
            .applicationType(ApplicationType.NEW_PERMIT)
            .commencementDate(LocalDate.now())
            .competentAuthority(ca)
            .verificationBodyId(verificationBodyId)
            .status(accountStatus)
            .installationCategory(installationCategory)
            .location(
                LocationOnShore.builder()
                    .gridReference("grid")
                    .address(
                        Address.builder()
                            .city("city")
                            .country("GR")
                            .line1("line")
                            .postcode("postcode")
                            .build())
                    .build())
            .name(accountName)
            .siteName(accountName)
            .emissionTradingScheme(ets)
            .emitterId("EM" + String.format("%05d", id))
            .build();

        entityManager.persist(account);
        return account;
    }

    private LegalEntity createLegalEntity(String leName, LegalEntityStatus leStatus) {
        LegalEntity le = LegalEntity.builder()
            .location(
                LocationOnShore.builder()
                    .gridReference("grid")
                    .address(
                        Address.builder()
                            .city("city")
                            .country("GR")
                            .line1("line")
                            .postcode("postcode")
                            .build())
                    .build())
            .name(leName)
            .status(leStatus)
            .referenceNumber("regNumber")
            .type(LegalEntityType.LIMITED_COMPANY)
            .build();

        entityManager.persist(le);
        return le;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
