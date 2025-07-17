package uk.gov.pmrv.api.mireport.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.MiReportType;
import uk.gov.netz.api.mireport.domain.MiReportSearchResult;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class PmrvMiReportRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private PmrvMiReportRepository pmrvMiReportRepository;

    @Test
    void findByCompetentAuthorityAndAccountType() {
        CompetentAuthorityEnum[] competentAuthorities = CompetentAuthorityEnum.values();
        Set<String> reportNames = Set.of(MiReportType.LIST_OF_ACCOUNTS_ASSIGNED_REGULATOR_SITE_CONTACTS,
                MiReportType.REGULATOR_OUTSTANDING_REQUEST_TASKS,
                MiReportType.COMPLETED_WORK,
                MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS,
                MiReportType.CUSTOM);

        AccountType[] accountTypes = AccountType.values();

        int index = 1;
        for (CompetentAuthorityEnum authority : competentAuthorities) {
            for (String miReportType : reportNames) {
                PmrvMiReportEntity entity = PmrvMiReportEntity.builder()
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
            List<MiReportSearchResult> result = pmrvMiReportRepository.findByCompetentAuthorityAndAccountType(ca, AccountType.INSTALLATION);
            assertThat(result).hasSize(reportNames.size());
            assertThat(result.stream().map(MiReportSearchResult::getMiReportType).allMatch(reportNames::contains)).isTrue();
        }
    }
}