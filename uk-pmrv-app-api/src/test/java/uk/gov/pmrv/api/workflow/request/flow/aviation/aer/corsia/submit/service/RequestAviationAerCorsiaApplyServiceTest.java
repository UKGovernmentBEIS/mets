package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.service.AviationAerCorsiaSubmitRequestTaskSyncAerAttachmentsService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaSaveApplicationRequestTaskActionPayload;

@ExtendWith(MockitoExtension.class)
class RequestAviationAerCorsiaApplyServiceTest {

    @InjectMocks
    private RequestAviationAerCorsiaApplyService service;
    
    @Mock
    private AviationAerCorsiaSubmitRequestTaskSyncAerAttachmentsService syncAerAttachmentsService;

    @Test
    void applySaveAction() {
        AviationAerCorsiaSaveApplicationRequestTaskActionPayload taskActionPayload =
            AviationAerCorsiaSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AVIATION_AER_CORSIA_SAVE_APPLICATION_PAYLOAD)
                .reportingRequired(true)
                .aer(AviationAerCorsia.builder()
                    .operatorDetails(AviationCorsiaOperatorDetails.builder().operatorName("operator name").build())
                    .build())
                .aerSectionsCompleted(Map.of(EmpAdditionalDocuments.class.getName(), List.of(true)))
                .build();

        AviationAerCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload =
            AviationAerCorsiaApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                .aerSectionsCompleted(new HashMap<>())
                .build();

        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .build();

        //invoke
        service.applySaveAction(taskActionPayload, requestTask);

        AviationAerCorsiaApplicationSubmitRequestTaskPayload payloadSaved =
            (AviationAerCorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        AviationAerCorsia aer = payloadSaved.getAer();
        assertNotNull(aer.getOperatorDetails());
        Assertions.assertTrue(payloadSaved.getReportingRequired());
        assertThat(payloadSaved.getAerSectionsCompleted()).containsExactly(
            Map.entry(EmpAdditionalDocuments.class.getName(), List.of(true)));
        assertFalse(payloadSaved.isVerificationPerformed());
        
        verify(syncAerAttachmentsService, times(1)).sync(taskActionPayload.getReportingRequired(), requestTaskPayload);
    }
}