package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationBiomassFractionMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationCarbonContentMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationConversionFactorMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationEmissionFactorMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationNetCalorificValueMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOxidationFactorMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterMonitoringTier;

@Getter
@AllArgsConstructor
public enum CalculationParameterType {
    ACTIVITY_DATA {
        @Override
        public CalculationParameterMonitoringTier getParameterMonitoringTier(CalculationSourceStreamCategoryAppliedTier calculationSourceStream) {
            return CalculationActivityDataMonitoringTier.builder()
                .type(this)
                .tier(calculationSourceStream.getActivityData().getTier())
                .build();
        }
    },
    NET_CALORIFIC_VALUE {
        @Override
        public CalculationParameterMonitoringTier getParameterMonitoringTier(CalculationSourceStreamCategoryAppliedTier calculationSourceStream) {
            return CalculationNetCalorificValueMonitoringTier.builder()
                .type(this)
                .tier(calculationSourceStream.getNetCalorificValue().getTier())
                .build();
        }
    },
    EMISSION_FACTOR {
        @Override
        public CalculationParameterMonitoringTier getParameterMonitoringTier(CalculationSourceStreamCategoryAppliedTier calculationSourceStream) {
            return CalculationEmissionFactorMonitoringTier.builder()
                .type(this)
                .tier(calculationSourceStream.getEmissionFactor().getTier())
                .build();
        }
    },
    OXIDATION_FACTOR {
        @Override
        public CalculationParameterMonitoringTier getParameterMonitoringTier(CalculationSourceStreamCategoryAppliedTier calculationSourceStream) {
            return CalculationOxidationFactorMonitoringTier.builder()
                .type(this)
                .tier(calculationSourceStream.getOxidationFactor().getTier())
                .build();
        }
    },
    CARBON_CONTENT {
        @Override
        public CalculationParameterMonitoringTier getParameterMonitoringTier(CalculationSourceStreamCategoryAppliedTier calculationSourceStream) {
            return CalculationCarbonContentMonitoringTier.builder()
                .type(this)
                .tier(calculationSourceStream.getCarbonContent().getTier())
                .build();
        }
    },
    CONVERSION_FACTOR{
        @Override
        public CalculationParameterMonitoringTier getParameterMonitoringTier(CalculationSourceStreamCategoryAppliedTier calculationSourceStream) {
            return CalculationConversionFactorMonitoringTier.builder()
                .type(this)
                .tier(calculationSourceStream.getConversionFactor().getTier())
                .build();
        }
    },
    BIOMASS_FRACTION{
        @Override
        public CalculationParameterMonitoringTier getParameterMonitoringTier(CalculationSourceStreamCategoryAppliedTier calculationSourceStream) {
            return CalculationBiomassFractionMonitoringTier.builder()
                .type(this)
                .tier(calculationSourceStream.getBiomassFraction().getTier())
                .build();
        }
    }
    ;

    public abstract CalculationParameterMonitoringTier getParameterMonitoringTier(CalculationSourceStreamCategoryAppliedTier calculationSourceStream);
}
