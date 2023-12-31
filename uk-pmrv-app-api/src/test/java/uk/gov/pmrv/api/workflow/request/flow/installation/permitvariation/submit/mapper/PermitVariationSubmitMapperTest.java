package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain.PermitVariationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain.PermitVariationApplicationSubmittedRequestActionPayload;

class PermitVariationSubmitMapperTest {

	private final PermitVariationSubmitMapper mapper = Mappers.getMapper(PermitVariationSubmitMapper.class);

    @Test
    void toPermitContainer_from_submit_request_Task_payload() {
    	UUID attachment1 = UUID.randomUUID();
    	PermitVariationApplicationSubmitRequestTaskPayload taskPayload = PermitVariationApplicationSubmitRequestTaskPayload.builder()
    			.payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD)
    			.permitType(PermitType.GHGE)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.installationOperatorDetails(InstallationOperatorDetails.builder().installationName("installationName1").build())
    			.permitAttachments(Map.of(attachment1, "att1"))
    			.permitSectionsCompleted(Map.of("section1", List.of(true, false)))
    			.reviewSectionsCompleted(Map.of("section1", true))
    			.build();
    	
    	InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().installationName("installationName2").build();
    	
    	PermitContainer result = mapper.toPermitContainer(taskPayload, installationOperatorDetails);
    	
    	assertThat(result).isEqualTo(PermitContainer.builder()
    			.permitType(PermitType.GHGE)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.installationOperatorDetails(InstallationOperatorDetails.builder().installationName("installationName2").build())
    			.permitAttachments(Map.of(attachment1, "att1"))
    			.build());
    }
    
    @Test
    void toPermitVariationApplicationSubmittedRequestActionPayload() {
    	UUID attachment1 = UUID.randomUUID();
    	PermitVariationApplicationSubmitRequestTaskPayload taskPayload = PermitVariationApplicationSubmitRequestTaskPayload.builder()
    			.payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD)
    			.permitType(PermitType.GHGE)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.installationOperatorDetails(InstallationOperatorDetails.builder().installationName("installationName1").build())
    			.permitAttachments(Map.of(attachment1, "att1"))
    			.permitSectionsCompleted(Map.of("section1", List.of(true, false)))
    			.reviewSectionsCompleted(Map.of("section2", true))
    			.build();
    	
    	InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().installationName("installationName2").build();
    	
    	PermitVariationApplicationSubmittedRequestActionPayload result = mapper.toPermitVariationApplicationSubmittedRequestActionPayload(taskPayload, installationOperatorDetails);
    	
    	assertThat(result).isEqualTo(PermitVariationApplicationSubmittedRequestActionPayload.builder()
    			.payloadType(RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_SUBMITTED_PAYLOAD)
    			.permitType(PermitType.GHGE)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.installationOperatorDetails(InstallationOperatorDetails.builder().installationName("installationName2").build())
    			.permitAttachments(Map.of(attachment1, "att1"))
    			.permitSectionsCompleted(Map.of("section1", List.of(true, false)))
    			.build());
    }
    
}
