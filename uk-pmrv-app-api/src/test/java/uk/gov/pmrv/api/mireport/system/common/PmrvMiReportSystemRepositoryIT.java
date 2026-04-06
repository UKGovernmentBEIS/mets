package uk.gov.pmrv.api.mireport.system.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.system.MiReportSystemType;
import uk.gov.netz.api.mireport.system.MiReportSystemSearchResult;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class PmrvMiReportSystemRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private PmrvMiReportSystemRepository pmrvMiReportRepository;

    @Test
    void findByCompetentAuthorityAndAccountType() {
        CompetentAuthorityEnum[] competentAuthorities = CompetentAuthorityEnum.values();
        Set<String> reportNames = Set.of(MiReportSystemType.LIST_OF_ACCOUNTS_ASSIGNED_REGULATOR_SITE_CONTACTS,
                MiReportSystemType.REGULATOR_OUTSTANDING_REQUEST_TASKS,
                MiReportSystemType.COMPLETED_WORK,
                MiReportSystemType.LIST_OF_ACCOUNTS_USERS_CONTACTS);

        int index = 1;
        for (CompetentAuthorityEnum authority : competentAuthorities) {
            for (String miReportType : reportNames) {
                PmrvMiReportSystemEntity entity = PmrvMiReportSystemEntity.builder()
                        .id(index++)
                        .competentAuthority(authority)
                        .miReportType(miReportType)
                        .accountType(AccountType.INSTALLATION)
                        .build();
                pmrvMiReportRepository.save(entity);
            }
        }
        pmrvMiReportRepository.flush();


        for (CompetentAuthorityEnum ca : competentAuthorities) {
            List<MiReportSystemSearchResult> result = pmrvMiReportRepository.findByCompetentAuthorityAndAccountType(ca, AccountType.INSTALLATION);
            assertThat(result).hasSize(reportNames.size());
            assertThat(result.stream().map(MiReportSystemSearchResult::getMiReportType).allMatch(reportNames::contains)).isTrue();
        }
    }
    
    @Test
    void findByCompetentAuthorityAndAccountTypeAndMiReportType() {
    	CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
    	AccountType accountType = AccountType.INSTALLATION;
    	String miReportType = "miReportType1";
    	PmrvMiReportSystemEntity entity = PmrvMiReportSystemEntity.builder()
                .competentAuthority(competentAuthority)
                .miReportType(miReportType)
                .accountType(accountType)
                .build();
        pmrvMiReportRepository.save(entity);
        pmrvMiReportRepository.flush();

		assertTrue(pmrvMiReportRepository.existsByCompetentAuthorityAndAccountTypeAndMiReportType(competentAuthority,
				accountType, miReportType));
		assertFalse(pmrvMiReportRepository.existsByCompetentAuthorityAndAccountTypeAndMiReportType(CompetentAuthorityEnum.NORTHERN_IRELAND,
				accountType, miReportType));
		assertFalse(pmrvMiReportRepository.existsByCompetentAuthorityAndAccountTypeAndMiReportType(competentAuthority,
				AccountType.AVIATION, miReportType));
		assertFalse(pmrvMiReportRepository.existsByCompetentAuthorityAndAccountTypeAndMiReportType(competentAuthority,
				accountType, "ANOTHER"));
    }
}