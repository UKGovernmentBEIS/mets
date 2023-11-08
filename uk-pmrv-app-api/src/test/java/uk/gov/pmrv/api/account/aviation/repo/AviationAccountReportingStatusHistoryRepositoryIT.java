package uk.gov.pmrv.api.account.aviation.repo;

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
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountReportingStatusHistoryRepository;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class AviationAccountReportingStatusHistoryRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AviationAccountReportingStatusHistoryRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByAccountIdOrderBySubmissionDateDesc() {
        AviationAccount account = createAccount(1L, "account", CompetentAuthorityEnum.ENGLAND, "crcoCode", 1L, AviationAccountStatus.LIVE);

        AviationAccountReportingStatusHistory reportingStatusEntry1 = createReportingStatusHistory(AviationAccountReportingStatus.REQUIRED_TO_REPORT, "reason 1",
                "submitterName 1", "submitterId 1", LocalDateTime.now(), account);
        AviationAccountReportingStatusHistory reportingStatusEntry2 = createReportingStatusHistory(AviationAccountReportingStatus.EXEMPT_COMMERCIAL, "reason 2",
                "submitterName 2", "submitterId 2", LocalDateTime.now().plusSeconds(10), account);
        flushAndClear();


        final Page<AviationAccountReportingStatusHistory> reportingStatusEntries =
                repo.findByAccountIdOrderBySubmissionDateDesc(PageRequest.of(0, 2), account.getId());
        assertThat(reportingStatusEntries.getTotalElements()).isEqualTo(2L);
        assertThat(reportingStatusEntries.get().map(AviationAccountReportingStatusHistory::getSubmitterName)).containsOnly(reportingStatusEntry1.getSubmitterName(), reportingStatusEntry2.getSubmitterName());
        assertThat(reportingStatusEntries.get().map(AviationAccountReportingStatusHistory::getStatus)).containsOnly(reportingStatusEntry1.getStatus(), reportingStatusEntry2.getStatus());
        assertThat(reportingStatusEntries.get().map(AviationAccountReportingStatusHistory::getReason)).containsOnly(reportingStatusEntry1.getReason(), reportingStatusEntry2.getReason());
        final List<AviationAccountReportingStatusHistory> reportingStatusEntriesList = reportingStatusEntries.get().toList();
        assertEquals(reportingStatusEntry2.getReason(), reportingStatusEntriesList.get(0).getReason());
        assertEquals(reportingStatusEntry1.getReason(), reportingStatusEntriesList.get(1).getReason());
    }

    @Test
    void findByAccountIdOrderBySubmissionDateDesc_pagination() {
        AviationAccount account = createAccount(1L, "account", CompetentAuthorityEnum.ENGLAND, "crcoCode", 1L, AviationAccountStatus.LIVE);

        AviationAccountReportingStatusHistory reportingStatusEntry1 = createReportingStatusHistory(AviationAccountReportingStatus.REQUIRED_TO_REPORT, "reason 1",
                "submitterName 1", "submitterId 1", LocalDateTime.now(), account);
        AviationAccountReportingStatusHistory reportingStatusEntry2 = createReportingStatusHistory(AviationAccountReportingStatus.EXEMPT_COMMERCIAL, "reason 2",
                "submitterName 2", "submitterId 2", LocalDateTime.now().plusSeconds(5L), account);
        AviationAccountReportingStatusHistory reportingStatusEntry3 = createReportingStatusHistory(AviationAccountReportingStatus.EXEMPT_NON_COMMERCIAL, "reason 3",
                "submitterName 3", "submitterId 3", LocalDateTime.now().plusSeconds(10), account);
        flushAndClear();


        final Page<AviationAccountReportingStatusHistory> reportingStatusEntries_first_page =
                repo.findByAccountIdOrderBySubmissionDateDesc(PageRequest.of(0, 2), account.getId());
        assertThat(reportingStatusEntries_first_page.getTotalElements()).isEqualTo(3L);
        assertThat(reportingStatusEntries_first_page.getNumberOfElements()).isEqualTo(2);
        assertThat(reportingStatusEntries_first_page.get().map(AviationAccountReportingStatusHistory::getSubmitterName)).containsOnly(reportingStatusEntry3.getSubmitterName(), reportingStatusEntry2.getSubmitterName());
        assertThat(reportingStatusEntries_first_page.get().map(AviationAccountReportingStatusHistory::getStatus)).containsOnly(reportingStatusEntry3.getStatus(), reportingStatusEntry2.getStatus());
        assertThat(reportingStatusEntries_first_page.get().map(AviationAccountReportingStatusHistory::getReason)).containsOnly(reportingStatusEntry3.getReason(), reportingStatusEntry2.getReason());
        final List<AviationAccountReportingStatusHistory> reportingStatusEntriesList_first = reportingStatusEntries_first_page.get().toList();
        assertEquals(reportingStatusEntry3.getReason(), reportingStatusEntriesList_first.get(0).getReason());
        assertEquals(reportingStatusEntry2.getReason(), reportingStatusEntriesList_first.get(1).getReason());

        final Page<AviationAccountReportingStatusHistory> reportingStatusEntries_second_page =
                repo.findByAccountIdOrderBySubmissionDateDesc(PageRequest.of(1, 2), account.getId());
        assertThat(reportingStatusEntries_second_page.getTotalElements()).isEqualTo(3L);
        assertThat(reportingStatusEntries_second_page.getNumberOfElements()).isEqualTo(1);
        assertThat(reportingStatusEntries_second_page.get().map(AviationAccountReportingStatusHistory::getSubmitterName)).containsOnly(reportingStatusEntry1.getSubmitterName());
        assertThat(reportingStatusEntries_second_page.get().map(AviationAccountReportingStatusHistory::getStatus)).containsOnly(reportingStatusEntry1.getStatus());
        assertThat(reportingStatusEntries_second_page.get().map(AviationAccountReportingStatusHistory::getReason)).containsOnly(reportingStatusEntry1.getReason());
        final List<AviationAccountReportingStatusHistory> reportingStatusEntriesList_second = reportingStatusEntries_second_page.get().toList();
        assertEquals(reportingStatusEntry1.getReason(), reportingStatusEntriesList_second.get(0).getReason());
    }

    @Test
    void findReportingStatusHistoryByAccountIdOrderBySubmissionDateDesc_empty_set_result() {
        AviationAccount account = createAccount(1L, "account", CompetentAuthorityEnum.ENGLAND, "crcoCode", 1L, AviationAccountStatus.LIVE);

        flushAndClear();


        final Page<AviationAccountReportingStatusHistory> reportingStatusEntries =
                repo.findByAccountIdOrderBySubmissionDateDesc(PageRequest.of(0, 2), account.getId());
        assertThat(reportingStatusEntries.getTotalElements()).isZero();

    }

    private AviationAccount createAccount(Long id, String accountName, CompetentAuthorityEnum ca, String crcoCode,
                                          Long verificationBodyId, AviationAccountStatus status) {

        AviationAccount account = AviationAccount.builder()
                .id(id)
                .accountType(AccountType.AVIATION)
                .commencementDate(LocalDate.now())
                .competentAuthority(ca)
                .verificationBodyId(verificationBodyId)
                .status(status)
                .reportingStatus(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
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
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .emitterId("EM" + String.format("%05d", id))
                .crcoCode(crcoCode)
                .build();
        entityManager.persist(account);
        return account;
    }

    private AviationAccountReportingStatusHistory createReportingStatusHistory(AviationAccountReportingStatus status, String reason, String submitterName,
                                                            String submitterId, LocalDateTime submissionDate, AviationAccount account) {

        AviationAccountReportingStatusHistory reportingStatusEntry = AviationAccountReportingStatusHistory.builder()
                .status(status)
                .reason(reason)
                .submitterName(submitterName)
                .submitterId(submitterId)
                .submissionDate(submissionDate)
                .account(account)
                .build();

        entityManager.persist(reportingStatusEntry);
        return reportingStatusEntry;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
