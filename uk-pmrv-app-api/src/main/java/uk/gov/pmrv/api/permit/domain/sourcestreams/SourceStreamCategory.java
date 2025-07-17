package uk.gov.pmrv.api.permit.domain.sourcestreams;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum SourceStreamCategory {

    CATEGORY_1 {
        @Override
        public Set<CalculationParameterType> getApplicableCalculationParameterTypes() {
            return Set.of(
                CalculationParameterType.ACTIVITY_DATA,
                CalculationParameterType.NET_CALORIFIC_VALUE,
                CalculationParameterType.EMISSION_FACTOR,
                CalculationParameterType.OXIDATION_FACTOR
            );
        }
    },
    CATEGORY_2 {
        @Override
        public Set<CalculationParameterType> getApplicableCalculationParameterTypes() {
            return Set.of(
                CalculationParameterType.ACTIVITY_DATA,
                CalculationParameterType.NET_CALORIFIC_VALUE,
                CalculationParameterType.CARBON_CONTENT
            );
        }
    },
    CATEGORY_4 {
        @Override
        public Set<CalculationParameterType> getApplicableCalculationParameterTypes() {
            return Set.of(
                CalculationParameterType.ACTIVITY_DATA,
                CalculationParameterType.EMISSION_FACTOR
            );
        }
    },
    CATEGORY_5 {
        @Override
        public Set<CalculationParameterType> getApplicableCalculationParameterTypes() {
            return Set.of(
                CalculationParameterType.ACTIVITY_DATA,
                CalculationParameterType.NET_CALORIFIC_VALUE
            );
        }
    },
    CATEGORY_6 {
        @Override
        public Set<CalculationParameterType> getApplicableCalculationParameterTypes() {
            return Set.of(
                CalculationParameterType.ACTIVITY_DATA,
                CalculationParameterType.EMISSION_FACTOR,
                CalculationParameterType.NET_CALORIFIC_VALUE
            );
        }
    },
    CATEGORY_7 {
        @Override
        public Set<CalculationParameterType> getApplicableCalculationParameterTypes() {
            return Set.of(
                CalculationParameterType.ACTIVITY_DATA,
                CalculationParameterType.EMISSION_FACTOR,
                CalculationParameterType.CONVERSION_FACTOR
            );
        }
    }
    ;

    public abstract Set<CalculationParameterType> getApplicableCalculationParameterTypes();
}
