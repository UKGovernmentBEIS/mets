package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.RequestEmpUkEtsReviewService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceUkEtsReviewSaveActionHandlerTest {

    @InjectMocks
    private EmpIssuanceUkEtsReviewSaveActionHandler empIssuanceUkEtsReviewSaveActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestEmpUkEtsReviewService requestEmpUkEtsReviewService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        EmpIssuanceUkEtsSaveApplicationReviewRequestTaskActionPayload requestTaskActionPayload =
            EmpIssuanceUkEtsSaveApplicationReviewRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMP_ISSUANCE_UKETS_SAVE_APPLICATION_REVIEW_PAYLOAD)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().build())
                .build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        AppUser appUser = AppUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        //invoke
        empIssuanceUkEtsReviewSaveActionHandler.process(requestTask.getId(),
            RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_APPLICATION_REVIEW,
            appUser,
            requestTaskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestEmpUkEtsReviewService, times(1)).applySaveAction(requestTaskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(empIssuanceUkEtsReviewSaveActionHandler.getTypes())
            .containsOnly(RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_APPLICATION_REVIEW);
    }
}