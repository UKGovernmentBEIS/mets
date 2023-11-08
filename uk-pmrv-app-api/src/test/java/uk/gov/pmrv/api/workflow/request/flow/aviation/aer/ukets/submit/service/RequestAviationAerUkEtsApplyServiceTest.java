package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.service;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.service.RequestAviationAerUkEtsApplyService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RequestAviationAerUkEtsApplyServiceTest {

    private final RequestAviationAerUkEtsApplyService service = new RequestAviationAerUkEtsApplyService();

    @Test
    void applySaveAction() {
        AviationAerUkEtsSaveApplicationRequestTaskActionPayload taskActionPayload =
            AviationAerUkEtsSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AVIATION_AER_UKETS_SAVE_APPLICATION_PAYLOAD)
                .reportingRequired(true)
                .aer(AviationAerUkEts.builder()
                    .additionalDocuments(EmpAdditionalDocuments.builder()
                        .exist(false)
                        .build()
                    )
                    .build())
                .aerSectionsCompleted(Map.of(EmpAdditionalDocuments.class.getName(), List.of(true)))
                .build();

        AviationAerUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload = AviationAerUkEtsApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD)
            .aerSectionsCompleted(new HashMap<>())
            .build();

        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .build();

        //invoke
        service.applySaveAction(taskActionPayload, requestTask);

        AviationAerUkEtsApplicationSubmitRequestTaskPayload payloadSaved =
            (AviationAerUkEtsApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        AviationAerUkEts aer = payloadSaved.getAer();
        assertNotNull(aer.getAdditionalDocuments());
        assertFalse(aer.getAdditionalDocuments().isExist());
        assertTrue(payloadSaved.getReportingRequired());
        assertThat(payloadSaved.getAerSectionsCompleted()).containsExactly(Map.entry(EmpAdditionalDocuments.class.getName(), List.of(true)));
        assertFalse(payloadSaved.isVerificationPerformed());
    }
}