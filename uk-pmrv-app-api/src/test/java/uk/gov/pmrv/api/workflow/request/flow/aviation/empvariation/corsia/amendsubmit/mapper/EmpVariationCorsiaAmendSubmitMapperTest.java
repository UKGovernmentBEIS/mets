package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.FuelMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.ActivitiesDescriptionCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

class EmpVariationCorsiaAmendSubmitMapperTest {

	private final EmpVariationCorsiaAmendSubmitMapper empVariationCorsiaAmendSubmitMapper = Mappers.getMapper(EmpVariationCorsiaAmendSubmitMapper.class);
	
	@Test
    void toEmpVariationCorsiaApplicationReturnedForAmendsRequestActionPayload() {
    	final UUID attachment1 = UUID.randomUUID();
    	final UUID attachment2 = UUID.randomUUID();
    	final UUID attachment3 = UUID.randomUUID();
    	RequestActionPayloadType returnedForAmendsPayloadType = RequestActionPayloadType.EMP_VARIATION_CORSIA_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD;
    	Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = buildReviewGroupDecisions(attachment1);
    	Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> expectedReviewGroupDecisions = Map.of(
            EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpVariationReviewDecision
                .builder()
                .type(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .details(ChangesRequiredDecisionDetails.builder()
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
                .build());
        final EmpVariationCorsiaApplicationReviewRequestTaskPayload taskPayload =
            EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_REVIEW_PAYLOAD)
                .emissionsMonitoringPlan(buildEmp())
                .empVariationDetailsReviewDecision(EmpVariationReviewDecision.builder()
                		.type(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .details(ChangesRequiredDecisionDetails.builder()
                                .requiredChanges(
                                    List.of(
                                        ReviewDecisionRequiredChange.builder()
                                            .reason("reason")
                                            .files(Set.of(attachment2))
                                            .build()
                                    )
                                )
                                .notes("notes")
                                .build()
                        )
                        .build())
                .empAttachments(Map.of(attachment3, "att3"))
                .reviewAttachments(Map.of(attachment1, "att1", attachment2, "att2"))
                .empSectionsCompleted(Map.of("section1", List.of(true, false)))
                .reviewSectionsCompleted(Map.of("section1", true))
                .reviewGroupDecisions(reviewGroupDecisions)
                .build();

        final EmpVariationCorsiaApplicationReturnedForAmendsRequestActionPayload result =
        		empVariationCorsiaAmendSubmitMapper.toEmpVariationCorsiaApplicationReturnedForAmendsRequestActionPayload(taskPayload, returnedForAmendsPayloadType);

        assertThat(result.getPayloadType()).isEqualTo(returnedForAmendsPayloadType);
        assertThat(result.getReviewGroupDecisions()).isEqualTo(expectedReviewGroupDecisions);
        assertThat(result.getReviewAttachments().keySet()).containsExactlyInAnyOrder(attachment1, attachment2);
    }
  
    @Test
    void toEmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload() {

        final UUID attachment1 = UUID.randomUUID();
        RequestTaskPayloadType amendsSubmitPayloadType = RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD;
        String operatorName = "anotherOperatorName";
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("name").email("email").build();
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .serviceContactDetails(serviceContactDetails)
            .build();
        Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = buildReviewGroupDecisions(attachment1);
        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = buildEmp();
        
        EmpVariationReviewDecision empVariationDetailsReviewDecision = EmpVariationReviewDecision.builder()
		        .type(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
		        .details(ChangesRequiredDecisionDetails.builder()
						.notes("notes")
						.requiredChanges(List.of(
								ReviewDecisionRequiredChange.builder().reason("reason1").files(Set.of(attachment1)).build()
								))
						.build())
		        .build();
        
        final EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_VARIATION_CORSIA_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .empAttachments(Map.of(attachment1, "att1"))
            .empSectionsCompleted(Map.of("section1", List.of(true, false)))
            .reviewSectionsCompleted(Map.of("section1", true))
            .reviewGroupDecisions(reviewGroupDecisions)
            .empVariationDetails(EmpVariationCorsiaDetails.builder().reason("reason").build())
            .empVariationDetailsCompleted(Boolean.TRUE)
            .reviewAttachments(Map.of(attachment1, "attachment1"))
            .empVariationDetailsReviewDecision(empVariationDetailsReviewDecision)
            .build();
        Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> expectedReviewGroupDecisions = Map.of(
            EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpVariationReviewDecision
                .builder()
                .type(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .details(ChangesRequiredDecisionDetails.builder()
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
                .build());

        final EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload result =
        		empVariationCorsiaAmendSubmitMapper.toEmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload(
        				requestPayload, aviationAccountInfo, amendsSubmitPayloadType);

        assertThat(result.getPayloadType()).isEqualTo(amendsSubmitPayloadType);
        assertThat(result.getReviewGroupDecisions()).isEqualTo(expectedReviewGroupDecisions);
        assertThat(result.getReviewAttachments()).isEqualTo(Map.of(attachment1, "attachment1"));
        assertThat(result.getEmpVariationDetails()).isEqualTo(requestPayload.getEmpVariationDetails());
        assertThat(result.getServiceContactDetails()).isEqualTo(serviceContactDetails);
        assertThat(result.getEmpAttachments()).isEqualTo(requestPayload.getEmpAttachments());
        assertThat(result.getEmpSectionsCompleted()).isEqualTo(requestPayload.getEmpSectionsCompleted());
        assertThat(result.getReviewSectionsCompleted()).isEqualTo(requestPayload.getReviewSectionsCompleted());
        assertThat(result.getEmpVariationDetailsCompleted()).isEqualTo(requestPayload.getEmpVariationDetailsCompleted());
        assertThat(result.getEmpVariationDetailsReviewDecision()).isEqualTo(EmpVariationReviewDecision.builder()
                .type(empVariationDetailsReviewDecision.getType())
                .details(ChangesRequiredDecisionDetails.builder()
                        .requiredChanges(((ChangesRequiredDecisionDetails) empVariationDetailsReviewDecision.getDetails()).getRequiredChanges()).build())
                .build());
    }
    
    private EmissionsMonitoringPlanCorsia buildEmp() {
		return EmissionsMonitoringPlanCorsia.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .emissionsMonitoringApproach(FuelMonitoringApproach.builder().build())
            .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
            .operatorDetails(EmpCorsiaOperatorDetails.builder()
                .operatorName("empOperatorDetailsOperatorName")
                .flightIdentification(FlightIdentification.builder()
                    .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                    .icaoDesignators("icao designators")
                    .build())
                .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                    .operatorType(OperatorType.COMMERCIAL)
                    .flightTypes(Set.of(FlightType.SCHEDULED))
                    .activityDescription("activity description")
                    .build())
                .build())
            .build();
	}
    
    private Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> buildReviewGroupDecisions(UUID attachment) {
		return Map.of(
            EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpVariationReviewDecision
                .builder()
                .type(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .details(ChangesRequiredDecisionDetails.builder()
                        .requiredChanges(
                            List.of(
                                ReviewDecisionRequiredChange.builder()
                                    .reason("reason")
                                    .files(Set.of(attachment))
                                    .build()
                            )
                        )
                        .notes("notes")
                        .build()
                )
                .build(),
            EmpCorsiaReviewGroup.MONITORING_APPROACH, EmpVariationReviewDecision.builder().type(EmpVariationReviewDecisionType.ACCEPTED).build()
            );
	}
}
