package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class PermitVariationApplicationPeerReviewRegulatorLedRequestTasknitializerTest {
	
	@InjectMocks
    private PermitVariationApplicationPeerReviewRegulatorLedRequestTasknitializer cut;
	
	@Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
	
	@Test
	void initializePayload() {
		
		final UUID attachment = UUID.randomUUID();
		final Long accountId = 1L;

		final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
        		.installationName("Account name")
				.siteName("siteName")
        		.build();
		final PermitContainer permitContainer = PermitContainer.builder()
				.permitType(PermitType.GHGE)
				.permit(Permit.builder()
						.abbreviations(Abbreviations.builder().exist(false).build())
						.build())
				.permitAttachments(Map.of(attachment, "att"))
				.build();
		final PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
				.permitType(PermitType.GHGE)
				.permit(permitContainer.getPermit())
				.permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
				.permitVariationDetailsCompleted(Boolean.TRUE)
				.permitSectionsCompleted(Map.of("section1", List.of(true, false)))
    			.reviewSectionsCompleted(Map.of("section2", true))
				.build();
		
		final Request request = Request.builder().accountId(accountId).payload(requestPayload).build();
		when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
			.thenReturn(installationOperatorDetails);
		
		// invoke
		RequestTaskPayload result = cut.initializePayload(request);
		PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload variationTaskPayloadResult = (PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload) result;
		assertThat(variationTaskPayloadResult.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_PEER_REVIEW_REGULATOR_LED_PAYLOAD);
		assertThat(variationTaskPayloadResult.getPermit()).isEqualTo(requestPayload.getPermit());
		assertThat(variationTaskPayloadResult.getPermitType()).isEqualTo(requestPayload.getPermitType());
		assertThat(variationTaskPayloadResult.getPermitVariationDetails()).isEqualTo(requestPayload.getPermitVariationDetails());
		assertThat(variationTaskPayloadResult.getPermitVariationDetailsCompleted()).isEqualTo(requestPayload.getPermitVariationDetailsCompleted());
		assertThat(variationTaskPayloadResult.getPermitSectionsCompleted()).isEqualTo(requestPayload.getPermitSectionsCompleted());
		assertThat(variationTaskPayloadResult.getReviewSectionsCompleted()).isEqualTo(requestPayload.getReviewSectionsCompleted());
		assertThat(variationTaskPayloadResult.getInstallationOperatorDetails()).isEqualTo(installationOperatorDetails);
		
		verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
	}

}
