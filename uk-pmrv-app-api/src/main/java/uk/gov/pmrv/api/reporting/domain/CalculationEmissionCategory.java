package uk.gov.pmrv.api.reporting.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum CalculationEmissionCategory {

    COMBUSTION {
        @Override
        public Set<SourceStreamType> getSourceStreamTypes() {
            return Set.of(
                    SourceStreamType.COMBUSTION_COMMERCIAL_STANDARD_FUELS,
                    SourceStreamType.COMBUSTION_OTHER_GASEOUS_LIQUID_FUELS,
                    SourceStreamType.COMBUSTION_SOLID_FUELS,
                    SourceStreamType.COMBUSTION_FLARES,
                    SourceStreamType.OTHER,
                    SourceStreamType.COMBUSTION_GAS_PROCESSING_TERMINALS,
                    SourceStreamType.COKE_FUEL_AS_PROCESS_INPUT,
                    SourceStreamType.IRON_STEEL_FUEL_AS_PROCESS_INPUT,
                    SourceStreamType.AMMONIA_FUEL_AS_PROCESS_INPUT,
                    SourceStreamType.HYDROGEN_AND_SYNTHESIS_GAS_FUEL_AS_PROCESS_INPUT
            );
        }
    },

    PROCESS {
        @Override
        public Set<SourceStreamType> getSourceStreamTypes() {
            return Set.of(
                    SourceStreamType.COMBUSTION_SCRUBBING_CARBONATE,
                    SourceStreamType.COMBUSTION_SCRUBBING_GYPSUM,
                    SourceStreamType.REFINERIES_HYDROGEN_PRODUCTION,
                    SourceStreamType.CEMENT_CLINKER_CKD,
                    SourceStreamType.CERAMICS_SCRUBBING,
                    SourceStreamType.COKE_CARBONATE_INPUT_METHOD_A,
                    SourceStreamType.COKE_OXIDE_OUTPUT_METHOD_B,
                    SourceStreamType.METAL_ORE_CARBONATE_INPUT,
                    SourceStreamType.IRON_STEEL_CARBONATE_INPUT,
                    SourceStreamType.CEMENT_CLINKER_KILN_INPUT_BASED_METHOD_A,
                    SourceStreamType.CEMENT_CLINKER_CLINKER_OUTPUT_METHOD_B,
                    SourceStreamType.CEMENT_CLINKER_NON_CARBONATE_CARBON,
                    SourceStreamType.LIME_DOLOMITE_MAGNESITE_CARBONATES_METHOD_A,
                    SourceStreamType.LIME_DOLOMITE_MAGNESITE_KILN_DUST_METHOD_B,
                    SourceStreamType.LIME_DOLOMITE_MAGNESITE_ALKALI_EARTH_OXIDE_METHOD_B,
                    SourceStreamType.GLASS_AND_MINERAL_WOOL_CARBONATES_INPUT,
                    SourceStreamType.CERAMICS_CARBON_INPUTS_METHOD_A,
                    SourceStreamType.CERAMICS_ALKALI_OXIDE_METHOD_B,
                    SourceStreamType.PULP_PAPER_MAKE_UP_CHEMICALS,
                    SourceStreamType.NON_FERROUS_SEC_ALUMINIUM_PROCESS_EMISSIONS,
                    SourceStreamType.COMBUSTION_SCRUBBING_UREA
            );
        }
    },

    MASS_BALANCE {
        @Override
        public Set<SourceStreamType> getSourceStreamTypes() {
            return Set.of(
                    SourceStreamType.REFINERIES_MASS_BALANCE,
                    SourceStreamType.COKE_MASS_BALANCE,
                    SourceStreamType.METAL_ORE_MASS_BALANCE,
                    SourceStreamType.IRON_STEEL_MASS_BALANCE,
                    SourceStreamType.CARBON_BLACK_MASS_BALANCE_METHODOLOGY,
                    SourceStreamType.HYDROGEN_AND_SYNTHESIS_GAS_MASS_BALANCE_METHODOLOGY,
                    SourceStreamType.BULK_ORGANIC_CHEMICALS_MASS_BALANCE_METHODOLOGY,
                    SourceStreamType.NON_FERROUS_SEC_ALUMINIUM_MASS_BALANCE_METHODOLOGY,
                    SourceStreamType.SODA_ASH_SODIUM_BICARBONATE_MASS_BALANCE_METHODOLOGY,
                    SourceStreamType.PRIMARY_ALUMINIUM_MASS_BALANCE_METHODOLOGY,
                    SourceStreamType.REFINERIES_CATALYTIC_CRACKER_REGENERATION
            );
        }
    };

    public abstract Set<SourceStreamType> getSourceStreamTypes();
}
