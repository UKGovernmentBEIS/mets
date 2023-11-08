package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaReviewUploadAttachmentServiceTest {

	@InjectMocks
    private EmpVariationCorsiaReviewUploadAttachmentService service;
	
	@Mock
    private RequestTaskService requestTaskService;
	
	@Test
    void uploadReviewGroupDecisionAttachment() {
        Long requestTaskId = 1L;
        UUID attachmentUuid = UUID.randomUUID();
        String filename = "name";

        EmpVariationCorsiaApplicationReviewRequestTaskPayload taskPayload =
        		EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_REVIEW_PAYLOAD)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia
                		.builder()
                		.abbreviations(EmpAbbreviations
                				.builder()
                				.exist(false)
                				.build())
                		.build())
                .build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).payload(taskPayload).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        assertThat(taskPayload.getReviewAttachments()).isEmpty();

        service.uploadReviewGroupDecisionAttachment(requestTaskId, attachmentUuid.toString(), filename);

        assertThat(taskPayload.getReviewAttachments()).containsExactly(entry(attachmentUuid, filename));
    }
}
