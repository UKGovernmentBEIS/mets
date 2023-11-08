package uk.gov.pmrv.api.mireport.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.mireport.common.domain.MiReportEntity;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportSearchResult;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class MiReportRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private MiReportRepository miReportRepository;

    @Test
    void findByCompetentAuthorityAndAccountType() {
        CompetentAuthorityEnum[] competentAuthorities = CompetentAuthorityEnum.values();
        MiReportType[] miReportTypes = MiReportType.values();
        AccountType[] accountTypes = AccountType.values();
        Set<String> reportNames = Arrays.stream(miReportTypes).map(MiReportType::getName).collect(Collectors.toSet());

        int index = 1;
        for (CompetentAuthorityEnum authority : competentAuthorities) {
            for (MiReportType miReportType : miReportTypes) {
                for (AccountType accountType : accountTypes) {
                    MiReportEntity entity = MiReportEntity.builder()
                        .id(index++)
                        .competentAuthority(authority)
                        .miReportType(miReportType)
                        .accountType(accountType)
                        .build();
                    miReportRepository.save(entity);
                }
            }
        }
        miReportRepository.flush();


        for (CompetentAuthorityEnum ca : competentAuthorities) {
            for (AccountType accountType : accountTypes) {
                List<MiReportSearchResult> result = miReportRepository.findByCompetentAuthorityAndAccountType(ca, accountType);
                assertThat(result).hasSize(miReportTypes.length);
                assertThat(result.stream().map(m -> m.getMiReportType().getName()).allMatch(reportNames::contains)).isTrue();
            }
        }
    }

}