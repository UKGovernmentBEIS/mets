package uk.gov.pmrv.api.workflow.request.flow.installation.dre.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.Dre;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;

class DreMapperTest {

	private final DreMapper mapper = Mappers.getMapper(DreMapper.class);

    @Test
    void toSubmittedActionPayload() {
    	UUID attachment1 = UUID.randomUUID();
    	DreRequestPayload requestPayload = DreRequestPayload.builder()
    			.dre(Dre.builder()
    					.determinationReason(DreDeterminationReason.builder()
    							.operatorAskedToResubmit(true)
    							.regulatorComments("reg comments")
    							.build())
    					.build())
    			.sectionCompleted(true)
    			.dreAttachments(Map.of(attachment1, "att1.pdf"))
    			.decisionNotification(DecisionNotification.builder()
    					.signatory("signatory")
    					.build())
    			.build();
    	
    	DreApplicationSubmittedRequestActionPayload actionPayload = mapper.toSubmittedActionPayload(requestPayload);
    	
    	assertThat(actionPayload.getPayloadType()).isEqualTo(RequestActionPayloadType.DRE_APPLICATION_SUBMITTED_PAYLOAD);
    	assertThat(actionPayload.getAttachments()).containsAllEntriesOf(requestPayload.getDreAttachments());
    	assertThat(actionPayload.getDecisionNotification()).isEqualTo(requestPayload.getDecisionNotification());
    	assertThat(actionPayload.getDre()).isEqualTo(requestPayload.getDre());
    }
    
    @Test
    void cloneSubmittedRequestActionPayloadIgnoreDeterminationReason() {
    	FileInfoDTO officialNotice = FileInfoDTO.builder().name("off").uuid(UUID.randomUUID().toString()).build();
    	DreApplicationSubmittedRequestActionPayload actionPayload = DreApplicationSubmittedRequestActionPayload.builder()
    			.dre(Dre.builder()
    					.officialNoticeReason("offi_notice")
    					.determinationReason(DreDeterminationReason.builder()
    							.operatorAskedToResubmit(true)
    							.regulatorComments("reg comments")
    							.build())
    					.build())
    			.officialNotice(officialNotice)
    			.build();
    	
    	DreApplicationSubmittedRequestActionPayload cloned = mapper.cloneSubmittedRequestActionPayloadIgnoreDeterminationReason(actionPayload);
    	
    	assertThat(cloned.getDre().getDeterminationReason()).isNull();
    	assertThat(cloned.getDre().getOfficialNoticeReason()).isEqualTo(actionPayload.getDre().getOfficialNoticeReason());
    	assertThat(cloned.getOfficialNotice()).isEqualTo(officialNotice);
    }
    
    @Test
    void toDreApplicationSubmitRequestTaskPayload() {
    	Dre dre = Dre.builder()
				.officialNoticeReason("offi_notice")
				.determinationReason(DreDeterminationReason.builder()
						.operatorAskedToResubmit(true)
						.regulatorComments("reg comments")
						.build())
				.build();
    	
    	UUID attachment1 = UUID.randomUUID();
    	Map<UUID, String> attachments = new HashMap<>();
    	attachments.put(attachment1, "att1");
    	
    	boolean sectionsCompleted = true;
    	
    	DreRequestPayload requestPayload = DreRequestPayload.builder()
    			.dre(dre)
    			.dreAttachments(attachments)
    			.sectionCompleted(sectionsCompleted)
    			.payloadType(RequestPayloadType.DRE_REQUEST_PAYLOAD)
    			.build();
    	
    	DreApplicationSubmitRequestTaskPayload result = mapper.toDreApplicationSubmitRequestTaskPayload(requestPayload, RequestTaskPayloadType.DRE_APPLICATION_SUBMIT_PAYLOAD);
    	
    	assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.DRE_APPLICATION_SUBMIT_PAYLOAD);
    	assertThat(result.getDreAttachments()).containsExactlyEntriesOf(attachments);
    	assertThat(result.getDre()).isEqualTo(dre);
    	assertThat(result.isSectionCompleted()).isEqualTo(sectionsCompleted);
    }
}
