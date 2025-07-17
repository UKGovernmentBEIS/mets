package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service.RequestEmpCorsiaReviewService;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaReviewSaveActionHandlerTest {

    @InjectMocks
    private EmpIssuanceCorsiaReviewSaveActionHandler empIssuanceCorsiaReviewSaveActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestEmpCorsiaReviewService requestEmpCorsiaReviewService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        EmpIssuanceCorsiaSaveApplicationReviewRequestTaskActionPayload requestTaskActionPayload =
            EmpIssuanceCorsiaSaveApplicationReviewRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_REVIEW_PAYLOAD)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder().build())
                .build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        AppUser appUser = AppUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        //invoke
        empIssuanceCorsiaReviewSaveActionHandler.process(requestTask.getId(),
            RequestTaskActionType.EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_REVIEW,
            appUser,
            requestTaskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestEmpCorsiaReviewService, times(1)).applySaveAction(requestTaskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(empIssuanceCorsiaReviewSaveActionHandler.getTypes())
            .containsOnly(RequestTaskActionType.EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_REVIEW);
    }
}