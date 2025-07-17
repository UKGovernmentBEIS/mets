package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.service.RequestAviationAerUkEtsApplyService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.handler.AviationAerUkEtsApplySaveActionHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsApplySaveActionHandlerTest {

    @InjectMocks
    private AviationAerUkEtsApplySaveActionHandler applySaveActionHandler;

    @Mock
    private RequestAviationAerUkEtsApplyService requestAviationAerUkEtsApplyService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        AppUser appUser = AppUser.builder().build();
        AviationAerUkEtsSaveApplicationRequestTaskActionPayload taskActionPayload =
            AviationAerUkEtsSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AVIATION_AER_UKETS_SAVE_APPLICATION_PAYLOAD)
                .aer(AviationAerUkEts.builder()
                    .additionalDocuments(EmpAdditionalDocuments.builder()
                        .exist(false)
                        .build()
                    )
                    .build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        applySaveActionHandler.process(requestTask.getId(), RequestTaskActionType.AVIATION_AER_UKETS_SAVE_APPLICATION,
            appUser, taskActionPayload);

        // Verify
        verify(requestAviationAerUkEtsApplyService, times(1)).applySaveAction(taskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(applySaveActionHandler.getTypes()).containsOnly(RequestTaskActionType.AVIATION_AER_UKETS_SAVE_APPLICATION);
    }
}