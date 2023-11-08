package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsSubmitUploadAttachmentServiceTest {

	@InjectMocks
    private EmpVariationUkEtsSubmitUploadAttachmentService service;
	
	@Mock
    private RequestTaskService requestTaskService;
	
	@Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        UUID attachmentUuid = UUID.randomUUID();
        String filename = "filename";
        
        EmpVariationUkEtsApplicationSubmitRequestTaskPayload taskPayload = 
        		EmpVariationUkEtsApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_SUBMIT_PAYLOAD)
                        .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts
        						.builder()
        						.abbreviations(EmpAbbreviations.builder().exist(false).build())
        						.build())
                        .build();
        
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).payload(taskPayload).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        
        assertThat(taskPayload.getEmpAttachments()).isEmpty();
        
        service.uploadAttachment(requestTaskId, attachmentUuid.toString(), filename);
        
        assertThat(taskPayload.getEmpAttachments()).containsExactly(entry(attachmentUuid, filename));
    }
	
}
