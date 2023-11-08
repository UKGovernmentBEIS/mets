package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PermitVariationAmendSubmitMapperTest {

    private final PermitVariationAmendSubmitMapper mapper = Mappers.getMapper(PermitVariationAmendSubmitMapper.class);

    @Test
    void toPermitVariationAmendsSubmitRequestTaskPayload() {

        final UUID attachment1 = UUID.randomUUID();

        Map<PermitReviewGroup, PermitVariationReviewDecision> reviewGroupDecisions = Map.of(
            PermitReviewGroup.MEASUREMENT_N2O,
            PermitVariationReviewDecision.builder()
                .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .details(
                    ChangesRequiredDecisionDetails.builder()
                        .requiredChanges(
                            List.of(
                                ReviewDecisionRequiredChange.builder()
                                    .reason("reason")
                                    .files(Set.of(attachment1))
                                    .build()
                            )
                        )
                        .notes("notes")
                        .build()
                )
                .build()
        );
        
        final PermitVariationReviewDecision permitVariationDetailsReviewDecision = PermitVariationReviewDecision.builder()
			.type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
			.details(ChangesRequiredDecisionDetails.builder()
					.notes("notes")
					.requiredChanges(List.of(
							ReviewDecisionRequiredChange.builder().reason("reason1").build()
							))
					.build())
			.build();
        
        final PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_VARIATION_REQUEST_PAYLOAD)
            .permitType(PermitType.GHGE)
            .permit(Permit.builder()
                .abbreviations(Abbreviations.builder().exist(true).build())
                .build())
            .permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
            .permitVariationDetailsCompleted(Boolean.TRUE)
            .permitSectionsCompleted(Map.of("section1", List.of(true, false)))
            .permitAttachments(Map.of(attachment1, "att1"))
            .reviewSectionsCompleted(Map.of("section2", true))
            .reviewGroupDecisions(reviewGroupDecisions)
            .reviewAttachments(Map.of(attachment1, "attachment1"))
            .permitVariationDetailsReviewDecision(permitVariationDetailsReviewDecision)
            .build();
        Map<PermitReviewGroup, PermitVariationReviewDecision> expectedReviewGroupDecisions = Map.of(
            PermitReviewGroup.MEASUREMENT_N2O,
            PermitVariationReviewDecision.builder()
                .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .details(
                    ChangesRequiredDecisionDetails.builder()
                        .requiredChanges(
                            List.of(
                                ReviewDecisionRequiredChange.builder()
                                    .reason("reason")
                                    .files(Set.of(attachment1))
                                    .build()
                            )
                        )
                        .build()
                )
                .build()
        );

        final PermitVariationApplicationAmendsSubmitRequestTaskPayload result =
            mapper.toPermitVariationAmendsSubmitRequestTaskPayload(requestPayload);

        assertThat(result).isEqualTo(PermitVariationApplicationAmendsSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
            .reviewGroupDecisions(expectedReviewGroupDecisions)
            .reviewAttachments(Map.of(attachment1, "attachment1"))
            .permitVariationDetails(requestPayload.getPermitVariationDetails())
            .permitAttachments(requestPayload.getPermitAttachments())
            .permitSectionsCompleted(requestPayload.getPermitSectionsCompleted())
            .permit(requestPayload.getPermit())
            .permitType(requestPayload.getPermitType())
            .reviewSectionsCompleted(requestPayload.getReviewSectionsCompleted())
            .permitVariationDetailsCompleted(requestPayload.getPermitVariationDetailsCompleted())
            .permitVariationDetailsReviewDecision(PermitVariationReviewDecision.builder()
                    .type(permitVariationDetailsReviewDecision.getType())
                    .details(ChangesRequiredDecisionDetails.builder()
                            .requiredChanges(((ChangesRequiredDecisionDetails) permitVariationDetailsReviewDecision.getDetails()).getRequiredChanges()).build())
                    .build())
            .build());
    }
    
    @Test
    void toPermitVariationApplicationReturnedForAmendsRequestActionPayload() {

        final UUID attachment1 = UUID.randomUUID();
        Map<PermitReviewGroup, PermitVariationReviewDecision> reviewGroupDecisions = Map.of(
            PermitReviewGroup.MEASUREMENT_N2O,
            PermitVariationReviewDecision.builder()
                .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .details(
                    ChangesRequiredDecisionDetails.builder()
                        .requiredChanges(
                            List.of(
                                ReviewDecisionRequiredChange.builder()
                                    .reason("reason")
                                    .files(Collections.emptySet())
                                    .build()
                            )
                        )
                        .notes("notes")
                        .build()
                )
                .build()
        );
        Map<PermitReviewGroup, PermitVariationReviewDecision> expectedReviewGroupDecisions = Map.of(
            PermitReviewGroup.MEASUREMENT_N2O,
            PermitVariationReviewDecision.builder()
                .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .details(
                    ChangesRequiredDecisionDetails.builder()
                        .requiredChanges(
                            List.of(
                                ReviewDecisionRequiredChange.builder()
                                    .reason("reason")
                                    .files(Collections.emptySet())
                                    .build()
                            )
                        )
                        .build()
                )
                .build()
        );
        final PermitVariationApplicationReviewRequestTaskPayload taskPayload =
            PermitVariationApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                .permitType(PermitType.GHGE)
                .permit(Permit.builder()
                    .abbreviations(Abbreviations.builder().exist(true).build())
                    .build())
                .installationOperatorDetails(
                    InstallationOperatorDetails.builder().installationName("installationName1").build())
                .permitAttachments(Map.of(attachment1, "att1"))
                .permitSectionsCompleted(Map.of("section1", List.of(true, false)))
                .reviewSectionsCompleted(Map.of("section1", true))
                .reviewGroupDecisions(reviewGroupDecisions)
                .build();

        final PermitVariationApplicationReturnedForAmendsRequestActionPayload result =
            mapper.toPermitVariationApplicationReturnedForAmendsRequestActionPayload(taskPayload);

        assertThat(result).isEqualTo(PermitVariationApplicationReturnedForAmendsRequestActionPayload.builder()
            .payloadType(RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD)
                .reviewGroupDecisions(expectedReviewGroupDecisions)
                .reviewAttachments(Collections.emptyMap())
            .build());
    }
}
