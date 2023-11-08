package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirReviewResponse;

@ExtendWith(MockitoExtension.class)
class CalculateAirRespondToRegulatorCommentsExpirationDateServiceTest {

    @InjectMocks
    private CalculateAirRespondToRegulatorCommentsExpirationDateService expirationDateService;

    @Mock
    private RequestService requestService;

    @Test
    void calculateExpirationDate() {
        
        final String requestId = "AIR-2022-1";
        final LocalDate currentDate = LocalDate.now();
        final Date expected = Date.from(currentDate.minusDays(10)
                .atTime(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant());
        final Request request = Request.builder()
                .id(requestId)
                .requestTasks(List.of(
                        RequestTask.builder()
                                .type(RequestTaskType.AIR_RESPOND_TO_REGULATOR_COMMENTS)
                                .payload(AirApplicationRespondToRegulatorCommentsRequestTaskPayload.builder()
                                        .payloadType(RequestTaskPayloadType.AIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                                        .regulatorImprovementResponses(Map.of(
                                                1, RegulatorAirImprovementResponse.builder()
                                                        .improvementRequired(false).build(),
                                                2, RegulatorAirImprovementResponse.builder().improvementRequired(true)
                                                        .improvementDeadline(currentDate).build(),
                                                3, RegulatorAirImprovementResponse.builder().improvementRequired(true)
                                                        .improvementDeadline(currentDate.minusDays(10)).build()
                                        )).build())
                                .build()
                )).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        Date actual = expirationDateService.calculateExpirationDate(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void calculateExpirationDate_no_improvements() {

        final String requestId = "AIR-2022-1";
        final Request request = Request.builder()
                .id(requestId)
                .payload(AirRequestPayload.builder()
                        .regulatorReviewResponse(RegulatorAirReviewResponse.builder()
                                .regulatorImprovementResponses(Map.of(
                                        1, RegulatorAirImprovementResponse.builder().improvementRequired(false).build(),
                                        2, RegulatorAirImprovementResponse.builder().improvementRequired(false).build()
                                ))
                                .build())
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        BusinessException be = assertThrows(BusinessException.class,
                () -> expirationDateService.calculateExpirationDate(requestId));

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    }
}
