package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.EmpEmissionSourcesCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpManagementProceduresCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpProcedureDescription;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;


@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaReviewReturnForAmendsValidatorServiceTest {

	@InjectMocks
    private EmpIssuanceCorsiaReviewReturnForAmendsValidatorService serviceValidator;

    @Test
    void validate() {
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload payload = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder()
        		.emissionsMonitoringPlan(buildEmp())
                .reviewGroupDecisions(Map.of(
                		EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, buildReviewDecision(EmpReviewDecisionType.ACCEPTED, Collections.emptyList()),
                        EmpCorsiaReviewGroup.EMISSION_SOURCES, buildReviewDecision(EmpReviewDecisionType.ACCEPTED, Collections.emptyList()),
                        EmpCorsiaReviewGroup.MANAGEMENT_PROCEDURES, buildReviewDecision(EmpReviewDecisionType.OPERATOR_AMENDS_NEEDED, List.of(
                        ReviewDecisionRequiredChange.builder().reason("no reason").build()))
                ))
                .build();

        // Invoke
        assertDoesNotThrow(() -> serviceValidator.validate(payload));
    }

    @Test
    void validate_no_amends() {
    	EmpIssuanceCorsiaApplicationReviewRequestTaskPayload payload = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder()
        		.emissionsMonitoringPlan(buildEmp())
                .reviewGroupDecisions(Map.of(
                        EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, buildReviewDecision(EmpReviewDecisionType.ACCEPTED, Collections.emptyList()),
                        EmpCorsiaReviewGroup.EMISSION_SOURCES, buildReviewDecision(EmpReviewDecisionType.ACCEPTED, Collections.emptyList()),
                        EmpCorsiaReviewGroup.FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES, buildReviewDecision(EmpReviewDecisionType.ACCEPTED, Collections.emptyList())
                ))
                .build();

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> serviceValidator.validate(payload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_EMP_REVIEW);
    }

    private EmpIssuanceReviewDecision buildReviewDecision(EmpReviewDecisionType type, List<ReviewDecisionRequiredChange> requiredChanges) {
        if (type == EmpReviewDecisionType.ACCEPTED) {
            return EmpIssuanceReviewDecision.builder()
                .type(type)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build();
        } else {
            return EmpIssuanceReviewDecision.builder()
                .type(type)
                .details(ChangesRequiredDecisionDetails.builder().requiredChanges(requiredChanges).build())
                .build();
        }
    }

    private EmissionsMonitoringPlanCorsia buildEmp() {
        return EmissionsMonitoringPlanCorsia.builder()
        		.abbreviations(EmpAbbreviations.builder().exist(false).build())
        		.emissionSources(EmpEmissionSourcesCorsia
        				.builder()
                        .multipleFuelConsumptionMethodsExplanation("explanation")
        				.build())
        		.managementProcedures(EmpManagementProceduresCorsia
        				.builder()
        				.recordKeepingAndDocumentation(buildProcedureForm("documentation"))
        				.build())
                .build();
    }
    
    private EmpProcedureDescription buildProcedureForm(String value) {
        return EmpProcedureDescription.builder()
                .description("description" + value)
                .build();
    }
}
