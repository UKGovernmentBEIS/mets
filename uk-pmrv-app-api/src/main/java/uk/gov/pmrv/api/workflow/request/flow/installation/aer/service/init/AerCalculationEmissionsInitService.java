package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.BiomassPercentages;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.DurationRange;

@Service
public class AerCalculationEmissionsInitService implements AerMonitoringApproachTypeEmissionsInitService {

    @Override
    public AerMonitoringApproachEmissions initialize(Permit permit) {
        CalculationOfCO2MonitoringApproach calculationMonitoringApproach =
            (CalculationOfCO2MonitoringApproach) permit.getMonitoringApproaches().getMonitoringApproaches().get(getMonitoringApproachType());

        List<CalculationSourceStreamCategoryAppliedTier> calculationSourceStreamCategoryAppliedTiers =
            calculationMonitoringApproach.getSourceStreamCategoryAppliedTiers();

        List<SourceStream> permitSourceStreams = permit.getSourceStreams().getSourceStreams();

        List<CalculationSourceStreamEmission> calculationSourceStreamEmissions = new ArrayList<>();

        calculationSourceStreamCategoryAppliedTiers.forEach(sourceStreamCategoryAppliedTier -> {
            CalculationSourceStreamEmission calcSourceStreamEmission =
                buildCalculationSourceStreamEmission(sourceStreamCategoryAppliedTier, permitSourceStreams);
            calculationSourceStreamEmissions.add(calcSourceStreamEmission);
        });

        return CalculationOfCO2Emissions.builder().type(getMonitoringApproachType())
            .hasTransfer(calculationMonitoringApproach.isHasTransfer())
            .sourceStreamEmissions(calculationSourceStreamEmissions)
            .build();
    }

    private CalculationSourceStreamEmission buildCalculationSourceStreamEmission(
        CalculationSourceStreamCategoryAppliedTier sourceStreamCategoryAppliedTier,
        List<SourceStream> sourceStreams) {
        CalculationSourceStreamCategory sourceStreamCategory =
            sourceStreamCategoryAppliedTier.getSourceStreamCategory();
        String sourceStreamId = sourceStreamCategory.getSourceStream();
        boolean existsBiomassInPermit = sourceStreamCategoryAppliedTier.getBiomassFraction().isExist();
        TransferCO2 transfer = sourceStreamCategoryAppliedTier.getSourceStreamCategory().getTransfer();

        CalculationSourceStreamEmission calcSourceStreamEmission = CalculationSourceStreamEmission.builder()
            .id(UUID.randomUUID().toString())
            .sourceStream(sourceStreamId)
            .emissionSources(sourceStreamCategory.getEmissionSources())
            .durationRange(DurationRange.builder().fullYearCovered(true).build())
            .biomassPercentages(BiomassPercentages.builder().contains(existsBiomassInPermit).build())
            .transfer(transfer)
            .build();

        Optional<SourceStream> sourceStream = sourceStreams.stream()
            .filter(ss -> sourceStreamId.equals(ss.getId()))
            .findFirst();

        List<CalculationParameterMonitoringTier> parameterMonitoringTiers = new ArrayList<>();

        sourceStream.map(SourceStream::getType).ifPresent(sourceStreamType ->
            parameterMonitoringTiers.addAll(getParameterMonitoringTiersBySourceStreamType(sourceStreamType,
                sourceStreamCategoryAppliedTier)
            )
        );

        //if biomass fraction exists in permit, add tier to parameter monitoring tiers list
        if (existsBiomassInPermit) {
            parameterMonitoringTiers
                .add(CalculationParameterType.BIOMASS_FRACTION.getParameterMonitoringTier(sourceStreamCategoryAppliedTier));
        }

        calcSourceStreamEmission.setParameterMonitoringTiers(parameterMonitoringTiers);
        return calcSourceStreamEmission;
    }

    private List<CalculationParameterMonitoringTier> getParameterMonitoringTiersBySourceStreamType(SourceStreamType sourceStreamType,
                                                                                                   CalculationSourceStreamCategoryAppliedTier sourceStreamCategoryAppliedTier) {
        List<CalculationParameterMonitoringTier> parameterMonitoringTiers = new ArrayList<>();
        SourceStreamCategory sourceStreamCategory = sourceStreamType.getCategory();

        sourceStreamCategory.getApplicableCalculationParameterTypes().forEach(
            parameterType -> parameterMonitoringTiers.add(parameterType.getParameterMonitoringTier(sourceStreamCategoryAppliedTier))
        );

        return parameterMonitoringTiers;
    }

    @Override
    public MonitoringApproachType getMonitoringApproachType() {
        return MonitoringApproachType.CALCULATION_CO2;
    }
}
