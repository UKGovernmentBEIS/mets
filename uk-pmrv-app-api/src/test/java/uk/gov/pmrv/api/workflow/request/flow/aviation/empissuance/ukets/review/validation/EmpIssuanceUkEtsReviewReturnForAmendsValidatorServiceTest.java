package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.validation;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.EmpEmissionSources;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpManagementProcedures;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;


@ExtendWith(MockitoExtension.class)
class EmpIssuanceUkEtsReviewReturnForAmendsValidatorServiceTest {

	@InjectMocks
    private EmpIssuanceUkEtsReviewReturnForAmendsValidatorService serviceValidator;

    @Test
    void validate() {
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload payload = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
        		.emissionsMonitoringPlan(buildEmp())
                .reviewGroupDecisions(Map.of(
                		EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, buildReviewDecision(EmpReviewDecisionType.ACCEPTED, Collections.emptyList()),
                		EmpUkEtsReviewGroup.EMISSION_SOURCES, buildReviewDecision(EmpReviewDecisionType.ACCEPTED, Collections.emptyList()),
                		EmpUkEtsReviewGroup.MANAGEMENT_PROCEDURES, buildReviewDecision(EmpReviewDecisionType.OPERATOR_AMENDS_NEEDED, List.of(
                        ReviewDecisionRequiredChange.builder().reason("no reason").build()))
                ))
                .build();

        // Invoke
        assertDoesNotThrow(() -> serviceValidator.validate(payload));
    }

    @Test
    void validate_no_amends() {
    	EmpIssuanceUkEtsApplicationReviewRequestTaskPayload payload = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
        		.emissionsMonitoringPlan(buildEmp())
                .reviewGroupDecisions(Map.of(
                		EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, buildReviewDecision(EmpReviewDecisionType.ACCEPTED, Collections.emptyList()),
                		EmpUkEtsReviewGroup.EMISSIONS_REDUCTION_CLAIM, buildReviewDecision(EmpReviewDecisionType.ACCEPTED, Collections.emptyList()),
                		EmpUkEtsReviewGroup.FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES, buildReviewDecision(EmpReviewDecisionType.ACCEPTED, Collections.emptyList())
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

    private EmissionsMonitoringPlanUkEts buildEmp() {
        return EmissionsMonitoringPlanUkEts.builder()
        		.abbreviations(EmpAbbreviations.builder().exist(false).build())
        		.emissionSources(EmpEmissionSources
        				.builder()
        				.additionalAircraftMonitoringApproach(buildProcedureForm("aircraft"))
        				.build())
        		.managementProcedures(EmpManagementProcedures
        				.builder()
        				.recordKeepingAndDocumentation(buildProcedureForm("documentation"))
        				.build())
                .build();
    }
    
    private EmpProcedureForm buildProcedureForm(String value) {
        return EmpProcedureForm.builder()
                .procedureDescription("procedureDescription" + value)
                .procedureDocumentName("procedureDocumentName" + value)
                .procedureReference("procedureReference" + value)
                .responsibleDepartmentOrRole("responsibleDepartmentOrRole" + value)
                .locationOfRecords("locationOfRecords" + value)
                .itSystemUsed("itSystemUsed" + value)
                .build();
    }
}
