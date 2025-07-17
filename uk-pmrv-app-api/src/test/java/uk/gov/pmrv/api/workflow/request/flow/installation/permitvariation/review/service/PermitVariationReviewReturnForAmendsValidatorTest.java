package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.common.MeasuredEmissionsSamplingFrequency;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.AppliedStandard;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.Laboratory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissionsTier;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class PermitVariationReviewReturnForAmendsValidatorTest {

    @InjectMocks
    private PermitVariationReviewReturnForAmendsValidator validator;

    @Test
    void validate() {
        PermitVariationApplicationReviewRequestTaskPayload payload = PermitVariationApplicationReviewRequestTaskPayload.builder()
                .permit(buildPermit())
                .reviewGroupDecisions(Map.of(
                        PermitReviewGroup.CONFIDENTIALITY_STATEMENT, buildReviewDecision(ReviewDecisionType.ACCEPTED, Collections.emptyList()),
                        PermitReviewGroup.FUELS_AND_EQUIPMENT, buildReviewDecision(ReviewDecisionType.REJECTED, Collections.emptyList()),
                        PermitReviewGroup.INSTALLATION_DETAILS, buildReviewDecision(ReviewDecisionType.OPERATOR_AMENDS_NEEDED, List.of(
                        ReviewDecisionRequiredChange.builder().reason("change").build()))
                ))
                .build();

        // Invoke
        assertDoesNotThrow(() -> validator.validate(payload));
    }
    
    @Test
    void validate_variation_details_amend() {
        PermitVariationApplicationReviewRequestTaskPayload payload = PermitVariationApplicationReviewRequestTaskPayload.builder()
                .permit(buildPermit())
                .permitVariationDetailsReviewDecision(PermitVariationReviewDecision.builder()
					.type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
					.details(ChangesRequiredDecisionDetails.builder()
							.notes("notes")
							.requiredChanges(List.of(
									ReviewDecisionRequiredChange.builder().reason("reason1").build()
									))
							.build())
					.build())
                .build();

        // Invoke
        assertDoesNotThrow(() -> validator.validate(payload));
    }

    @Test
    void validate_no_amends() {
        PermitVariationApplicationReviewRequestTaskPayload payload = PermitVariationApplicationReviewRequestTaskPayload.builder()
                .permit(buildPermit())
                .reviewGroupDecisions(Map.of(
                        PermitReviewGroup.CONFIDENTIALITY_STATEMENT, buildReviewDecision(ReviewDecisionType.ACCEPTED, Collections.emptyList()),
                        PermitReviewGroup.FUELS_AND_EQUIPMENT, buildReviewDecision(ReviewDecisionType.ACCEPTED, Collections.emptyList()),
                        PermitReviewGroup.INSTALLATION_DETAILS, buildReviewDecision(ReviewDecisionType.ACCEPTED, Collections.emptyList()),
                        PermitReviewGroup.MANAGEMENT_PROCEDURES, buildReviewDecision(ReviewDecisionType.ACCEPTED, Collections.emptyList()),
                        PermitReviewGroup.MONITORING_METHODOLOGY_PLAN, buildReviewDecision(ReviewDecisionType.ACCEPTED, Collections.emptyList()),
                        PermitReviewGroup.ADDITIONAL_INFORMATION, buildReviewDecision(ReviewDecisionType.ACCEPTED, Collections.emptyList()),
                        PermitReviewGroup.DEFINE_MONITORING_APPROACHES, buildReviewDecision(ReviewDecisionType.ACCEPTED, Collections.emptyList()),
                        PermitReviewGroup.UNCERTAINTY_ANALYSIS, buildReviewDecision(ReviewDecisionType.ACCEPTED, Collections.emptyList()),
                        PermitReviewGroup.MEASUREMENT_CO2, buildReviewDecision(ReviewDecisionType.ACCEPTED, Collections.emptyList())
                ))
                .build();

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(payload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_PERMIT_VARIATION_REVIEW);
    }

    private PermitVariationReviewDecision buildReviewDecision(ReviewDecisionType type, List<ReviewDecisionRequiredChange> requiredChanges) {
        if (type == ReviewDecisionType.ACCEPTED) {
            return PermitVariationReviewDecision.builder()
                .type(type)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build();
        } else if (type == ReviewDecisionType.REJECTED) {
            return PermitVariationReviewDecision.builder()
                .type(type)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build();
        } else {
            return PermitVariationReviewDecision.builder()
                .type(type)
                .details(ChangesRequiredDecisionDetails.builder().requiredChanges(requiredChanges).build())
                .build();
        }
    }

    private Permit buildPermit() {
        return Permit.builder()
                .monitoringApproaches(buildMonitoringApproaches())
                .build();
    }

    private MonitoringApproaches buildMonitoringApproaches() {
        EnumMap<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches = new EnumMap<>(MonitoringApproachType.class);

        monitoringApproaches.put(MonitoringApproachType.MEASUREMENT_CO2, buildMeasurementMonitoringApproach());

        return MonitoringApproaches.builder().monitoringApproaches(monitoringApproaches).build();
    }

    private MeasurementOfCO2MonitoringApproach buildMeasurementMonitoringApproach() {
        return MeasurementOfCO2MonitoringApproach.builder()
                .type(MonitoringApproachType.MEASUREMENT_CO2)
                .approachDescription("measurementApproachDescription")
                .emissionDetermination(buildProcedureForm("emissionDetermination"))
                .referencePeriodDetermination(buildProcedureForm("referencePeriodDetermination"))
                .gasFlowCalculation(ProcedureOptionalForm.builder().exist(false).build())
                .biomassEmissions(ProcedureOptionalForm.builder().exist(false).build())
                .corroboratingCalculations(buildProcedureForm("corroboratingCalculations"))
                .emissionPointCategoryAppliedTiers(List.of(MeasurementOfCO2EmissionPointCategoryAppliedTier.builder()
                        .emissionPointCategory(MeasurementOfCO2EmissionPointCategory.builder()
                                .emissionPoint(UUID.randomUUID().toString())
                                .sourceStreams(Set.of(UUID.randomUUID().toString()))
                                .emissionSources(Set.of(UUID.randomUUID().toString()))
                                .annualEmittedCO2Tonnes(BigDecimal.valueOf(26000.123))
                                .categoryType(CategoryType.MAJOR).build())
                        .measuredEmissions(buildMeasMeasuredEmissions())
                        .appliedStandard(buildAppliedStandard())
                        .build()))
                .build();
    }

    private ProcedureForm buildProcedureForm(String value) {
        return ProcedureForm.builder()
                .procedureDescription("procedureDescription" + value)
                .procedureDocumentName("procedureDocumentName" + value)
                .procedureReference("procedureReference" + value)
                .diagramReference("diagramReference" + value)
                .responsibleDepartmentOrRole("responsibleDepartmentOrRole" + value)
                .locationOfRecords("locationOfRecords" + value)
                .itSystemUsed("itSystemUsed" + value)
                .appliedStandards("appliedStandards" + value)
                .build();
    }

    private MeasurementOfCO2MeasuredEmissions buildMeasMeasuredEmissions() {
        return MeasurementOfCO2MeasuredEmissions.builder()
                .measurementDevicesOrMethods(Set.of(UUID.randomUUID().toString()))
                .samplingFrequency(MeasuredEmissionsSamplingFrequency.ANNUALLY)
                .tier(MeasurementOfCO2MeasuredEmissionsTier.TIER_4)
                .highestRequiredTier(HighestRequiredTier.builder().build())
                .build();
    }

    private AppliedStandard buildAppliedStandard() {
        return AppliedStandard.builder()
                .parameter("parameter")
                .appliedStandard("applied standard")
                .deviationFromAppliedStandardExist(true)
                .deviationFromAppliedStandardDetails("deviation details")
                .laboratory(Laboratory.builder().laboratoryName("lab").laboratoryAccredited(true).build())
                .build();
    }
}
