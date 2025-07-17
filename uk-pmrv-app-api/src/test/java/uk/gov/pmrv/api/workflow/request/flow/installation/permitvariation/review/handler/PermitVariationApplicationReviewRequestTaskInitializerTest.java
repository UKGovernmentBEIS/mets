package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2MonitoringApproach;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitVariationApplicationReviewRequestTaskInitializerTest {

	@InjectMocks
    private PermitVariationApplicationReviewRequestTaskInitializer handler;

	@Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

	@Test
	void initializePayload() {
		UUID attachment = UUID.randomUUID();
		Long accountId = 1L;

		InstallationOperatorDetails installationOperatorDetails = createInstallationOperatorDetails();
		PermitContainer permitContainer = createPermitContainer(attachment);
		PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
				.permitType(PermitType.GHGE)
				.permit(permitContainer.getPermit())
				.originalPermitContainer(permitContainer)
				.permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
				.permitVariationDetailsCompleted(Boolean.TRUE)
				.permitSectionsCompleted(Map.of("section1", List.of(true, false)))
    			.reviewSectionsCompleted(Map.of("section2", true))
				.build();
		Request request = Request.builder().accountId(accountId).payload(requestPayload).build();
		when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
			.thenReturn(installationOperatorDetails);

		// invoke
		RequestTaskPayload result = handler.initializePayload(request);
		PermitVariationApplicationReviewRequestTaskPayload variationTaskPayloadResult = (PermitVariationApplicationReviewRequestTaskPayload) result;
		assertThat(variationTaskPayloadResult.getPermit()).isEqualTo(requestPayload.getPermit());
		assertThat(variationTaskPayloadResult.getPermitType()).isEqualTo(requestPayload.getPermitType());
		assertThat(variationTaskPayloadResult.getPermitVariationDetails()).isEqualTo(requestPayload.getPermitVariationDetails());
		assertThat(variationTaskPayloadResult.getPermitVariationDetailsCompleted()).isEqualTo(requestPayload.getPermitVariationDetailsCompleted());
		assertThat(variationTaskPayloadResult.getPermitSectionsCompleted()).isEqualTo(requestPayload.getPermitSectionsCompleted());
		assertThat(variationTaskPayloadResult.getReviewSectionsCompleted()).isEqualTo(requestPayload.getReviewSectionsCompleted());
		assertThat(variationTaskPayloadResult.getInstallationOperatorDetails()).isEqualTo(installationOperatorDetails);
		assertThat(variationTaskPayloadResult.getReviewGroupDecisions().keySet())
		.containsExactlyInAnyOrder(PermitReviewGroup.PERMIT_TYPE,
			PermitReviewGroup.INSTALLATION_DETAILS,
			PermitReviewGroup.FUELS_AND_EQUIPMENT,
			PermitReviewGroup.DEFINE_MONITORING_APPROACHES,
			PermitReviewGroup.UNCERTAINTY_ANALYSIS,
			PermitReviewGroup.MANAGEMENT_PROCEDURES,
			PermitReviewGroup.MONITORING_METHODOLOGY_PLAN,
			PermitReviewGroup.ADDITIONAL_INFORMATION,
			PermitReviewGroup.CONFIDENTIALITY_STATEMENT,
			PermitReviewGroup.INHERENT_CO2);
		variationTaskPayloadResult.getReviewGroupDecisions().values().forEach(group ->
			assertThat(group.getType()).isEqualTo(ReviewDecisionType.ACCEPTED));
		assertThat(variationTaskPayloadResult.getPermitVariationDetailsReviewDecision()).isEqualTo(PermitVariationReviewDecision.builder().type(ReviewDecisionType.ACCEPTED)
            		.details(PermitAcceptedVariationDecisionDetails.builder().build()).build());

		verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
	}

	@Test
	void initializePayloadWithoutOverwritingReviewGroupDecisions() {
		UUID attachment = UUID.randomUUID();
		Long accountId = 1L;

		InstallationOperatorDetails installationOperatorDetails = createInstallationOperatorDetails();
		PermitContainer permitContainer = createPermitContainer(attachment);
		PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
			.permitType(PermitType.GHGE)
			.permit(permitContainer.getPermit())
			.permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
			.permitVariationDetailsCompleted(Boolean.TRUE)
			.permitVariationDetailsReviewDecision(PermitVariationReviewDecision.builder().type(ReviewDecisionType.REJECTED)
            		.details(ReviewDecisionDetails.builder().build()).build())
			.permitSectionsCompleted(Map.of("section1", List.of(true, false)))
			.reviewSectionsCompleted(Map.of("section2", true))
			.reviewGroupDecisions(
				Map.of(
					PermitReviewGroup.MEASUREMENT_N2O,
					PermitVariationReviewDecision.builder()
						.type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
						.details(ReviewDecisionDetails.builder().notes("notes").build())
						.build(),
					PermitReviewGroup.FALLBACK,
					PermitVariationReviewDecision.builder()
						.type(ReviewDecisionType.ACCEPTED)
						.details(ReviewDecisionDetails.builder().notes("notes").build())
						.build()
				)
			)
			.build();
		Request request = Request.builder().accountId(accountId).payload(requestPayload).build();
		when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
			.thenReturn(installationOperatorDetails);

		// invoke
		RequestTaskPayload result = handler.initializePayload(request);
		PermitVariationApplicationReviewRequestTaskPayload variationTaskPayloadResult = (PermitVariationApplicationReviewRequestTaskPayload) result;
		assertThat(variationTaskPayloadResult.getPermit()).isEqualTo(requestPayload.getPermit());
		assertThat(variationTaskPayloadResult.getPermitType()).isEqualTo(requestPayload.getPermitType());
		assertThat(variationTaskPayloadResult.getPermitVariationDetails()).isEqualTo(requestPayload.getPermitVariationDetails());
		assertThat(variationTaskPayloadResult.getPermitVariationDetailsCompleted()).isEqualTo(requestPayload.getPermitVariationDetailsCompleted());
		assertThat(variationTaskPayloadResult.getPermitSectionsCompleted()).isEqualTo(requestPayload.getPermitSectionsCompleted());
		assertThat(variationTaskPayloadResult.getReviewSectionsCompleted()).isEqualTo(requestPayload.getReviewSectionsCompleted());
		assertThat(variationTaskPayloadResult.getInstallationOperatorDetails()).isEqualTo(installationOperatorDetails);
		assertThat(variationTaskPayloadResult.getReviewGroupDecisions().keySet())
			.containsExactlyInAnyOrder(PermitReviewGroup.MEASUREMENT_N2O,
				PermitReviewGroup.FALLBACK);
		assertThat(variationTaskPayloadResult.getReviewGroupDecisions()
			.get(PermitReviewGroup.MEASUREMENT_N2O).getType()).isEqualTo(ReviewDecisionType.OPERATOR_AMENDS_NEEDED);
		assertThat(variationTaskPayloadResult.getReviewGroupDecisions()
			.get(PermitReviewGroup.FALLBACK).getType()).isEqualTo(ReviewDecisionType.ACCEPTED);
		assertThat(variationTaskPayloadResult.getPermitVariationDetailsReviewDecision()).isEqualTo(PermitVariationReviewDecision.builder().type(ReviewDecisionType.REJECTED)
            		.details(ReviewDecisionDetails.builder().build()).build());

		verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
	}

	private static PermitContainer createPermitContainer(UUID attachment) {
		return PermitContainer.builder()
			.permitType(PermitType.GHGE)
			.permit(Permit.builder()
				.abbreviations(Abbreviations.builder().exist(false).build())
				.monitoringApproaches(MonitoringApproaches.builder()
					.monitoringApproaches(Map.of(
						MonitoringApproachType.INHERENT_CO2, InherentCO2MonitoringApproach.builder().inherentReceivingTransferringInstallations(Collections.emptyList()).build()
					))
					.build())
				.build())
			.permitAttachments(Map.of(attachment, "att"))
			.build();
	}

	private static InstallationOperatorDetails createInstallationOperatorDetails() {
		return InstallationOperatorDetails.builder()
			.installationName("Account name")
			.siteName("siteName")
			.installationLocation(LocationOnShoreDTO.builder()
				.type(LocationType.ONSHORE)
				.gridReference("ST330000")
				.address(AddressDTO.builder()
					.line1("line1")
					.city("city")
					.country("GB")
					.postcode("postcode")
					.build())
				.build())
			.operatorType(LegalEntityType.LIMITED_COMPANY)
			.companyReferenceNumber("408812")
			.operatorDetailsAddress(AddressDTO.builder()
				.line1("line1")
				.city("city")
				.country("GR")
				.postcode("postcode")
				.build())
			.build();
	}

}
