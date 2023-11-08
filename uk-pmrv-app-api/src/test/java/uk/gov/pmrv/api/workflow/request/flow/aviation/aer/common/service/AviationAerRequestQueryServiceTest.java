package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AviationAerRequestQueryServiceTest {

    @InjectMocks
    private AviationAerRequestQueryService aerRequestQueryService;

    @Mock
    private RequestRepository requestRepository;

    @Test
    void findEndDateOfApprovedEmpIssuanceByAccountId() {
        Long accountId = 1L;
        LocalDateTime endDate = LocalDateTime.now();

        when(requestRepository.findByAccountIdAndTypeAndStatus(accountId, RequestType.EMP_ISSUANCE_UKETS, RequestStatus.APPROVED))
                .thenReturn(List.of(Request.builder()
                                .id("id")
                                .type(RequestType.EMP_ISSUANCE_UKETS)
                                .endDate(endDate)
                                .status(RequestStatus.APPROVED)
                        .build()));

        final Optional<LocalDateTime> actual = aerRequestQueryService.findEndDateOfApprovedEmpIssuanceByAccountId(accountId);
        assertThat(actual).isPresent();
        assertEquals(endDate, actual.get());
    }

    @Test
    void findEndDateOfApprovedEmpIssuanceByAccountId_no_result() {
        Long accountId = 1L;

        when(requestRepository.findByAccountIdAndTypeAndStatus(accountId, RequestType.EMP_ISSUANCE_UKETS, RequestStatus.APPROVED))
                .thenReturn(List.of());

        final Optional<LocalDateTime> actual = aerRequestQueryService.findEndDateOfApprovedEmpIssuanceByAccountId(accountId);
        assertThat(actual).isEmpty();
    }
}
