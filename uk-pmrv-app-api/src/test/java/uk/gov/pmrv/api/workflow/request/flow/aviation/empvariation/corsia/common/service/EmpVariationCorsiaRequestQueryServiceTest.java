package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestMetadata;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaRequestQueryServiceTest {

    @InjectMocks
    private EmpVariationCorsiaRequestQueryService empVariationRequestQueryService;

    @Mock
    private RequestRepository requestRepository;

    @Test
    void findApprovedEmpIssuanceVariationRequests() {

        Long accountId = 1L;
        LocalDateTime localDateTime = LocalDateTime.of(2023, 5, 16, 10, 10, 37);
        when(requestRepository.findByAccountIdAndTypeAndStatusOrderByEndDateDesc(accountId, RequestType.EMP_VARIATION_CORSIA, RequestStatus.APPROVED))
                .thenReturn(List.of(createRequest("id1", localDateTime, 10),
                        createRequest("id3", localDateTime.minusMonths(2), 9),
                        createRequest("id4", localDateTime.minusMonths(3), 8)));

        final List<EmpVariationRequestInfo> actual = empVariationRequestQueryService.findApprovedVariationRequests(accountId);
        assertThat(actual).isNotEmpty();
        assertThat(actual).extracting(EmpVariationRequestInfo::getEndDate).isEqualTo(List.of(localDateTime, localDateTime.minusMonths(2), localDateTime.minusMonths(3)));
        assertThat(actual).extracting(empVariationRequestInfo -> empVariationRequestInfo.getMetadata().getEmpConsolidationNumber()).containsOnly(10, 9, 8);
    }

    @Test
    void findApprovedEmpIssuanceVariationRequests_no_results() {

        Long accountId = 1L;
        when(requestRepository.findByAccountIdAndTypeAndStatusOrderByEndDateDesc(accountId, RequestType.EMP_VARIATION_CORSIA, RequestStatus.APPROVED))
                .thenReturn(List.of());

        final List<EmpVariationRequestInfo> actual = empVariationRequestQueryService.findApprovedVariationRequests(accountId);
        assertThat(actual).isEmpty();
    }

    private Request createRequest(String requestId, LocalDateTime endDate, Integer consolidationNumber) {
        return Request.builder()
                .id(requestId)
                .type(RequestType.EMP_VARIATION_CORSIA)
                .endDate(endDate)
                .metadata(EmpVariationRequestMetadata.builder()
                        .empConsolidationNumber(consolidationNumber)
                        .build())
                .build();
    }
}
