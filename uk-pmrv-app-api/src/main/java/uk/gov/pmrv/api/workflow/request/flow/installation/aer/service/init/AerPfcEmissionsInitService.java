package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.CalculationOfPFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCCalculationMethod;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.DurationRange;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationOfPfcEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationPfcParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.OverVoltageSourceStreamEmissionCalculationMethodData;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmissionCalculationMethodData;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.SlopeSourceStreamEmissionCalculationMethodData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AerPfcEmissionsInitService implements AerMonitoringApproachTypeEmissionsInitService {

    @Override
    public AerMonitoringApproachEmissions initialize(Permit permit) {
        CalculationOfPFCMonitoringApproach calculationOfPFCMonitoringApproach =
            (CalculationOfPFCMonitoringApproach) permit.getMonitoringApproaches().getMonitoringApproaches().get(getMonitoringApproachType());
        List<PFCSourceStreamCategoryAppliedTier> sourceStreamCategoryAppliedTiers =
            calculationOfPFCMonitoringApproach.getSourceStreamCategoryAppliedTiers();

        List<PfcSourceStreamEmission> pfcSourceStreamEmissions = new ArrayList<>();

        sourceStreamCategoryAppliedTiers.forEach(sourceStreamCategoryAppliedTier ->
            populatePfcSourceStreamEmissions(pfcSourceStreamEmissions, sourceStreamCategoryAppliedTier));

        return CalculationOfPfcEmissions.builder()
            .type(getMonitoringApproachType())
            .sourceStreamEmissions(pfcSourceStreamEmissions)
            .build();
    }

    private static void populatePfcSourceStreamEmissions(List<PfcSourceStreamEmission> pfcSourceStreamEmissions,
                                                         PFCSourceStreamCategoryAppliedTier sourceStreamCategoryAppliedTier) {
        PfcSourceStreamEmission pfcSourceStreamEmission = PfcSourceStreamEmission.builder()
            .id(UUID.randomUUID().toString())
            .sourceStream(sourceStreamCategoryAppliedTier.getSourceStreamCategory().getSourceStream())
            .emissionSources(sourceStreamCategoryAppliedTier.getSourceStreamCategory().getEmissionSources())
            .parameterMonitoringTier(
                CalculationPfcParameterMonitoringTier.builder()
                    .emissionFactorTier(sourceStreamCategoryAppliedTier.getEmissionFactor().getTier())
                    .activityDataTier(sourceStreamCategoryAppliedTier.getActivityData().getTier())
                    .build()
            )
            .pfcSourceStreamEmissionCalculationMethodData(
                populatePfcSourceStreamEmissionCalculationMethodData(
                    sourceStreamCategoryAppliedTier.getSourceStreamCategory().getCalculationMethod()
                )
            )
            .massBalanceApproachUsed(sourceStreamCategoryAppliedTier.getActivityData().isMassBalanceApproachUsed())
            .durationRange(DurationRange.builder().fullYearCovered(true).build())
            .build();
        pfcSourceStreamEmissions.add(pfcSourceStreamEmission);
    }

    private static PfcSourceStreamEmissionCalculationMethodData populatePfcSourceStreamEmissionCalculationMethodData(PFCCalculationMethod calculationMethod) {
        if (calculationMethod == PFCCalculationMethod.SLOPE) {
            return SlopeSourceStreamEmissionCalculationMethodData.builder()
                .calculationMethod(calculationMethod)
                .build();
        }
        return OverVoltageSourceStreamEmissionCalculationMethodData.builder()
            .calculationMethod(calculationMethod)
            .build();
    }

    @Override
    public MonitoringApproachType getMonitoringApproachType() {
        return MonitoringApproachType.CALCULATION_PFC;
    }
}
