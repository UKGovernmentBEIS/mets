package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
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
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperationScope;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.applicationtimeframe.EmpApplicationTimeframeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.FuelMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsreductionclaim.EmpEmissionsReductionClaim;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.ActivitiesDescription;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

class EmpVariationUkEtsAmendSubmitMapperTest {

	private final EmpVariationUkEtsAmendSubmitMapper empVariationUkEtsAmendSubmitMapper = Mappers.getMapper(EmpVariationUkEtsAmendSubmitMapper.class);
	
	@Test
    void toEmpVariationUkEtsApplicationReturnedForAmendsRequestActionPayload() {
    	final UUID attachment1 = UUID.randomUUID();
    	final UUID attachment2 = UUID.randomUUID();
    	final UUID attachment3 = UUID.randomUUID();
    	RequestActionPayloadType returnedForAmendsPayloadType = RequestActionPayloadType.EMP_VARIATION_UKETS_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD;
    	Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = buildReviewGroupDecisions(attachment1);
    	Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> expectedReviewGroupDecisions = Map.of(
                EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpVariationReviewDecision
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
        final EmpVariationUkEtsApplicationReviewRequestTaskPayload taskPayload =
        		EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_REVIEW_PAYLOAD)
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

        final EmpVariationUkEtsApplicationReturnedForAmendsRequestActionPayload result =
        		empVariationUkEtsAmendSubmitMapper.toEmpVariationUkEtsApplicationReturnedForAmendsRequestActionPayload(taskPayload, returnedForAmendsPayloadType);

        assertThat(result.getPayloadType()).isEqualTo(returnedForAmendsPayloadType);
        assertThat(result.getReviewGroupDecisions()).isEqualTo(expectedReviewGroupDecisions);
        assertThat(result.getReviewAttachments().keySet()).containsExactlyInAnyOrder(attachment1, attachment2);
    }
  
    @Test
    void toEmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload() {

        final UUID attachment1 = UUID.randomUUID();
        RequestTaskPayloadType amendsSubmitPayloadType = RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD;
        String operatorName = "anotherOperatorName";
        String crcoCode = "anotherCode";
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("name").email("email").build();
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .crcoCode(crcoCode)
            .serviceContactDetails(serviceContactDetails)
            .build();
        Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = buildReviewGroupDecisions(attachment1);
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = buildEmp();
        
        EmpVariationReviewDecision empVariationDetailsReviewDecision = EmpVariationReviewDecision.builder()
		        .type(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
		        .details(ChangesRequiredDecisionDetails.builder()
						.notes("notes")
						.requiredChanges(List.of(
								ReviewDecisionRequiredChange.builder().reason("reason1").files(Set.of(attachment1)).build()
								))
						.build())
		        .build();
        
        final EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_VARIATION_UKETS_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .empAttachments(Map.of(attachment1, "att1"))
            .empSectionsCompleted(Map.of("section1", List.of(true, false)))
            .reviewSectionsCompleted(Map.of("section1", true))
            .reviewGroupDecisions(reviewGroupDecisions)
            .empVariationDetails(EmpVariationUkEtsDetails.builder().reason("reason").build())
            .empVariationDetailsCompleted(Boolean.TRUE)
            .reviewAttachments(Map.of(attachment1, "attachment1"))
            .empVariationDetailsReviewDecision(empVariationDetailsReviewDecision)
            .build();
        Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> expectedReviewGroupDecisions = Map.of(
                EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpVariationReviewDecision
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

        final EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload result =
        		empVariationUkEtsAmendSubmitMapper.toEmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload(
        				requestPayload, aviationAccountInfo, amendsSubmitPayloadType);

        assertThat(result.getPayloadType()).isEqualTo(amendsSubmitPayloadType);
        assertThat(result.getEmissionsMonitoringPlan().getOperatorDetails().getCrcoCode())
        	.isEqualTo(aviationAccountInfo.getCrcoCode());
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
    
    private EmissionsMonitoringPlanUkEts buildEmp() {
		return EmissionsMonitoringPlanUkEts.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .applicationTimeframeInfo(EmpApplicationTimeframeInfo.builder().dateOfStart(LocalDate.now()).submittedOnTime(Boolean.TRUE).build())
            .emissionsMonitoringApproach(FuelMonitoringApproach.builder().build())
            .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
            .emissionsReductionClaim(EmpEmissionsReductionClaim.builder().exist(false).build())
            .operatorDetails(EmpOperatorDetails.builder()
                .operatorName("empOperatorDetailsOperatorName")
                .crcoCode("empOperatorDetailsCrcoCode")
                .flightIdentification(FlightIdentification.builder()
                    .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                    .icaoDesignators("icao designators")
                    .build())
                .activitiesDescription(ActivitiesDescription.builder()
                    .operatorType(OperatorType.COMMERCIAL)
                    .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                    .flightTypes(Set.of(FlightType.SCHEDULED))
                    .activityDescription("activity description")
                    .build())
                .build())
            .build();
	}
    
    private Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> buildReviewGroupDecisions(UUID attachment) {
		return Map.of(
                EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpVariationReviewDecision
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
                EmpUkEtsReviewGroup.MONITORING_APPROACH, EmpVariationReviewDecision.builder().type(EmpVariationReviewDecisionType.ACCEPTED).build()
            );
	}
}
