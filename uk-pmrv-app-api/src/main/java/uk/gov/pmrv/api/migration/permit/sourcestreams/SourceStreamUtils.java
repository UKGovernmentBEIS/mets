package uk.gov.pmrv.api.migration.permit.sourcestreams;


import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.ACETYLENE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.ANTHRACITE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.BIODIESELS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.BIOGASOLINE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.BIOMASS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.BITUMEN;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.BLAST_FURNACE_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.BLENDED_FUEL_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.CARBON_MONOXIDE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.CHARCOAL;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.CLINICAL_WASTE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.COAL;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.COAL_TAR;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.COKE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.COKE_OVEN_COKE_LIGNITE_COKE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.COKE_OVEN_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.COKING_COAL;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.COLLIERY_METHANE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.COMMERCIAL_INDUSTRIAL_WASTE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.CRUDE_OIL;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.ETHANE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.FUEL_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.FUEL_OIL;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.GAS_COKE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.GAS_DIESEL_OIL;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.GAS_OIL;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.GAS_WORKS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.HAZARDOUS_WASTE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.HIGH_PRESSURE_FLARE_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.IMPORT_FUEL_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.INDUSTRIAL_WASTES;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.KEROSENE_OTHER_THAN_JET_KEROSENE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.LANDFILL_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.LIGNITE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.LIQUEFIED_PETROLEUM_GASES;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.LOW_LOW_PRESSURE_FLARE_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.LOW_PRESSURE_FLARE_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.LUBRICANTS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.MATERIAL;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.MEDIUM_PRESSURE_FLARE_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.METHANE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.MOTOR_GASOLINE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.MSW;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.NAPHTHA;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.NATURAL_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.NATURAL_GAS_LIQUIDS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.NON_BIOMASS_PACKAGING_WASTE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.OIL_SHALE_AND_TAR_SANDS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.OPG;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.ORIMULSION;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.OTHER;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.OTHER_BIOGAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.OTHER_BITUMINOUS_COAL;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.OTHER_LIQUID_BIOFUELS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.OTHER_PETROLEUM_PRODUCTS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.OTHER_PRIMARY_SOLID_BIOMASS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.OXYGEN_STEEL_FURNACE_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.PARAFFIN_WAXES;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.PATENT_FUEL;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.PEAT;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.PETROL;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.PETROLEUM_COKE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.PILOT_AND_PURGE_FLARE_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.PILOT_FLARE_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.PROPANE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.REFINERY_FEEDSTOCKS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.REFINERY_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.RESIDUAL_FUEL_OIL;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.SCRAP_TYRES;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.SHALE_OIL;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.SLUDGE_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.SOUR_GAS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.SOUR_GAS_FLARE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.SDF;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.SSF;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.SRF;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.SUB_BITUMINOUS_COAL;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.WASTE_OILS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.WASTE_SOLVENTS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.WASTE_TYRES;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.WHITE_SPIRIT_SBP;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription.WOOD_WOOD_WASTE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.AMMONIA_FUEL_AS_PROCESS_INPUT;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.BULK_ORGANIC_CHEMICALS_MASS_BALANCE_METHODOLOGY;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.CARBON_BLACK_MASS_BALANCE_METHODOLOGY;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.CEMENT_CLINKER_CKD;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.CEMENT_CLINKER_CLINKER_OUTPUT_METHOD_B;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.CEMENT_CLINKER_KILN_INPUT_BASED_METHOD_A;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.CEMENT_CLINKER_NON_CARBONATE_CARBON;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.CERAMICS_ALKALI_OXIDE_METHOD_B;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.CERAMICS_CARBON_INPUTS_METHOD_A;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.CERAMICS_SCRUBBING;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.COKE_CARBONATE_INPUT_METHOD_A;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.COKE_FUEL_AS_PROCESS_INPUT;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.COKE_MASS_BALANCE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.COKE_OXIDE_OUTPUT_METHOD_B;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.COMBUSTION_COMMERCIAL_STANDARD_FUELS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.COMBUSTION_FLARES;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.COMBUSTION_GAS_PROCESSING_TERMINALS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.COMBUSTION_OTHER_GASEOUS_LIQUID_FUELS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.COMBUSTION_SCRUBBING_CARBONATE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.COMBUSTION_SCRUBBING_GYPSUM;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.COMBUSTION_SCRUBBING_UREA;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.COMBUSTION_SOLID_FUELS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.GLASS_AND_MINERAL_WOOL_CARBONATES_INPUT;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.HYDROGEN_AND_SYNTHESIS_GAS_FUEL_AS_PROCESS_INPUT;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.HYDROGEN_AND_SYNTHESIS_GAS_MASS_BALANCE_METHODOLOGY;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.IRON_STEEL_CARBONATE_INPUT;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.IRON_STEEL_FUEL_AS_PROCESS_INPUT;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.IRON_STEEL_MASS_BALANCE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.LIME_DOLOMITE_MAGNESITE_ALKALI_EARTH_OXIDE_METHOD_B;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.LIME_DOLOMITE_MAGNESITE_CARBONATES_METHOD_A;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.LIME_DOLOMITE_MAGNESITE_KILN_DUST_METHOD_B;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.METAL_ORE_CARBONATE_INPUT;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.METAL_ORE_MASS_BALANCE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.NON_FERROUS_SEC_ALUMINIUM_MASS_BALANCE_METHODOLOGY;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.NON_FERROUS_SEC_ALUMINIUM_PROCESS_EMISSIONS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.PRIMARY_ALUMINIUM_MASS_BALANCE_METHODOLOGY;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.PRIMARY_ALUMINIUM_PFC;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.PULP_PAPER_MAKE_UP_CHEMICALS;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.REFINERIES_CATALYTIC_CRACKER_REGENERATION;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.REFINERIES_HYDROGEN_PRODUCTION;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.REFINERIES_MASS_BALANCE;
import static uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType.SODA_ASH_SODIUM_BICARBONATE_MASS_BALANCE_METHODOLOGY;

public class SourceStreamUtils {

    public static final Map<String, SourceStreamType> etsTypeMap = new HashMap<>();
    public static final Map<String, SourceStreamDescription> etsDescriptionMap = new HashMap<>();
    
    static {
        etsTypeMap.put("Ammonia: Fuel as process input", AMMONIA_FUEL_AS_PROCESS_INPUT);
        etsTypeMap.put("Bulk organic chemicals: Mass balance methodology", BULK_ORGANIC_CHEMICALS_MASS_BALANCE_METHODOLOGY);
        etsTypeMap.put("Carbon black: Mass balance methodology", CARBON_BLACK_MASS_BALANCE_METHODOLOGY);
        etsTypeMap.put("Cement clinker: CKD", CEMENT_CLINKER_CKD);
        etsTypeMap.put("Cement clinker: Clinker output (Method B)", CEMENT_CLINKER_CLINKER_OUTPUT_METHOD_B);
        etsTypeMap.put("Cement clinker: Kiln input based (Method A)", CEMENT_CLINKER_KILN_INPUT_BASED_METHOD_A);
        etsTypeMap.put("Cement clinker: Non-carbonate carbon", CEMENT_CLINKER_NON_CARBONATE_CARBON);
        etsTypeMap.put("Ceramics: Alkali oxide (Method B)", CERAMICS_ALKALI_OXIDE_METHOD_B);
        etsTypeMap.put("Ceramics: Carbon inputs (Method A)", CERAMICS_CARBON_INPUTS_METHOD_A);
        etsTypeMap.put("Ceramics: Scrubbing", CERAMICS_SCRUBBING);
        etsTypeMap.put("Coke: Carbonate input (Method A)", COKE_CARBONATE_INPUT_METHOD_A);
        etsTypeMap.put("Coke: Fuel as process input", COKE_FUEL_AS_PROCESS_INPUT);
        etsTypeMap.put("Coke: Mass balance", COKE_MASS_BALANCE);
        etsTypeMap.put("Coke: Oxide output (Method B)", COKE_OXIDE_OUTPUT_METHOD_B);
        etsTypeMap.put("Combustion: Commercial standard fuels", COMBUSTION_COMMERCIAL_STANDARD_FUELS);
        etsTypeMap.put("Combustion: Flares", COMBUSTION_FLARES);
        etsTypeMap.put("Combustion: Gas Processing Terminals", COMBUSTION_GAS_PROCESSING_TERMINALS);
        etsTypeMap.put("Combustion: Other gaseous & liquid fuels", COMBUSTION_OTHER_GASEOUS_LIQUID_FUELS);
        etsTypeMap.put("Combustion:-Other gaseous or liquid fuels", COMBUSTION_OTHER_GASEOUS_LIQUID_FUELS);
        etsTypeMap.put("Combustion: Scrubbing (carbonate)", COMBUSTION_SCRUBBING_CARBONATE);
        etsTypeMap.put("Combustion: Scrubbing (gypsum)", COMBUSTION_SCRUBBING_GYPSUM);
        etsTypeMap.put("Combustion: Solid fuels", COMBUSTION_SOLID_FUELS);
        etsTypeMap.put("Glass and mineral wool: Carbonates (input)", GLASS_AND_MINERAL_WOOL_CARBONATES_INPUT);
        etsTypeMap.put("Hydrogen and synthesis gas: Fuel as process input", HYDROGEN_AND_SYNTHESIS_GAS_FUEL_AS_PROCESS_INPUT);
        etsTypeMap.put("Hydrogen and synthesis gas: Mass balance methodology", HYDROGEN_AND_SYNTHESIS_GAS_MASS_BALANCE_METHODOLOGY);
        etsTypeMap.put("Iron & steel: Carbonate input", IRON_STEEL_CARBONATE_INPUT);
        etsTypeMap.put("Iron & steel: Fuel as process input", IRON_STEEL_FUEL_AS_PROCESS_INPUT);
        etsTypeMap.put("Iron & steel: Mass balance", IRON_STEEL_MASS_BALANCE);
        etsTypeMap.put("Lime / dolomite / magnesite: Alkali earth oxide (Method B)", LIME_DOLOMITE_MAGNESITE_ALKALI_EARTH_OXIDE_METHOD_B);
        etsTypeMap.put("Lime / dolomite / magnesite: Carbonates (Method A)", LIME_DOLOMITE_MAGNESITE_CARBONATES_METHOD_A);
        etsTypeMap.put("Lime / dolomite / magnesite: Kiln dust (Method B)", LIME_DOLOMITE_MAGNESITE_KILN_DUST_METHOD_B);
        etsTypeMap.put("Metal ore: Carbonate input", METAL_ORE_CARBONATE_INPUT);
        etsTypeMap.put("Metal ore: Mass balance", METAL_ORE_MASS_BALANCE);
        etsTypeMap.put("(Non) ferrous,  sec. aluminium: Mass balance methodology", NON_FERROUS_SEC_ALUMINIUM_MASS_BALANCE_METHODOLOGY);
        etsTypeMap.put("(Non) ferrous, sec. aluminium: Process emissions", NON_FERROUS_SEC_ALUMINIUM_PROCESS_EMISSIONS);
        etsTypeMap.put("Other", SourceStreamType.OTHER);
        etsTypeMap.put("Primary aluminium: Mass balance methodology", PRIMARY_ALUMINIUM_MASS_BALANCE_METHODOLOGY);
        etsTypeMap.put("Pulp & paper: Make up chemicals", PULP_PAPER_MAKE_UP_CHEMICALS);
        etsTypeMap.put("Refineries: Catalytic cracker regeneration", REFINERIES_CATALYTIC_CRACKER_REGENERATION);
        etsTypeMap.put("Refineries: Hydrogen production", REFINERIES_HYDROGEN_PRODUCTION);
        etsTypeMap.put("Refineries: Mass balance", REFINERIES_MASS_BALANCE);
        etsTypeMap.put("Soda ash / sodium bicarbonate: Mass balance methodology", SODA_ASH_SODIUM_BICARBONATE_MASS_BALANCE_METHODOLOGY);
        etsTypeMap.put("Combustion: Scrubbing (urea)", COMBUSTION_SCRUBBING_UREA);
        etsTypeMap.put("Primary aluminium: PFC emissions (overvoltage method)", PRIMARY_ALUMINIUM_PFC);
        etsTypeMap.put("Primary aluminium: PFC emissions (slope method)", PRIMARY_ALUMINIUM_PFC);



        etsDescriptionMap.put("Acetylene", ACETYLENE);
        etsDescriptionMap.put("Anthracite", ANTHRACITE);
        etsDescriptionMap.put("Biodiesels", BIODIESELS);
        etsDescriptionMap.put("Biogasoline", BIOGASOLINE);
        etsDescriptionMap.put("Biomass", BIOMASS);
        etsDescriptionMap.put("Bitumen", BITUMEN);
        etsDescriptionMap.put("Blast Furnace Gas", BLAST_FURNACE_GAS);
        etsDescriptionMap.put("Blended Fuel Gas", BLENDED_FUEL_GAS);
        etsDescriptionMap.put("Carbon Monoxide", CARBON_MONOXIDE);
        etsDescriptionMap.put("Charcoal", CHARCOAL);
        etsDescriptionMap.put("Clinical Waste", CLINICAL_WASTE);
        etsDescriptionMap.put("Coal", COAL);
        etsDescriptionMap.put("Coal Tar", COAL_TAR);
        etsDescriptionMap.put("Coke", COKE);
        etsDescriptionMap.put("Coke Oven Coke & Lignite Coke", COKE_OVEN_COKE_LIGNITE_COKE);
        etsDescriptionMap.put("Coke Oven Gas", COKE_OVEN_GAS);
        etsDescriptionMap.put("Coking Coal", COKING_COAL);
        etsDescriptionMap.put("Colliery Methane", COLLIERY_METHANE);
        etsDescriptionMap.put("Commercial and Industrial Waste", COMMERCIAL_INDUSTRIAL_WASTE);
        etsDescriptionMap.put("Crude Oil", CRUDE_OIL);
        etsDescriptionMap.put("Ethane", ETHANE);
        etsDescriptionMap.put("Fuel Gas", FUEL_GAS);
        etsDescriptionMap.put("Fuel Oil", FUEL_OIL);
        etsDescriptionMap.put("Gas Coke", GAS_COKE);
        etsDescriptionMap.put("Gas/Diesel Oil", GAS_DIESEL_OIL);
        etsDescriptionMap.put("Gas/Oil", GAS_OIL);
        etsDescriptionMap.put("Gas Works", GAS_WORKS);
        etsDescriptionMap.put("Hazardous Waste", HAZARDOUS_WASTE);
        etsDescriptionMap.put("High Pressure Flare Gas", HIGH_PRESSURE_FLARE_GAS);
        etsDescriptionMap.put("Import Fuel Gas", IMPORT_FUEL_GAS);
        etsDescriptionMap.put("Industrial Wastes", INDUSTRIAL_WASTES);
        etsDescriptionMap.put("Kerosene (other than jet kerosene)", KEROSENE_OTHER_THAN_JET_KEROSENE);
        etsDescriptionMap.put("Landfill Gas", LANDFILL_GAS);
        etsDescriptionMap.put("Lignite", LIGNITE);
        etsDescriptionMap.put("Liquefied Petroleum Gases", LIQUEFIED_PETROLEUM_GASES);
        etsDescriptionMap.put("Low Low Pressure Flare Gas", LOW_LOW_PRESSURE_FLARE_GAS);
        etsDescriptionMap.put("Low Pressure Flare Gas", LOW_PRESSURE_FLARE_GAS);
        etsDescriptionMap.put("Lubricants", LUBRICANTS);
        etsDescriptionMap.put("Material", MATERIAL);
        etsDescriptionMap.put("Medium Pressure Flare Gas", MEDIUM_PRESSURE_FLARE_GAS);
        etsDescriptionMap.put("Methane", METHANE);
        etsDescriptionMap.put("Motor Gasoline", MOTOR_GASOLINE);
        etsDescriptionMap.put("MSW", MSW);
        etsDescriptionMap.put("Naphtha", NAPHTHA);
        etsDescriptionMap.put("Natural Gas", NATURAL_GAS);
        etsDescriptionMap.put("Natural Gas Liquids", NATURAL_GAS_LIQUIDS);
        etsDescriptionMap.put("Non-biomass Packaging Waste", NON_BIOMASS_PACKAGING_WASTE);
        etsDescriptionMap.put("Oil Shale and Tar Sands", OIL_SHALE_AND_TAR_SANDS);
        etsDescriptionMap.put("OPG", OPG);
        etsDescriptionMap.put("Orimulsion", ORIMULSION);
        etsDescriptionMap.put("Other", OTHER);
        etsDescriptionMap.put("Other Biogas", OTHER_BIOGAS);
        etsDescriptionMap.put("Other Bituminous Coal", OTHER_BITUMINOUS_COAL);
        etsDescriptionMap.put("Other Liquid Biofuels", OTHER_LIQUID_BIOFUELS);
        etsDescriptionMap.put("Other Petroleum Products", OTHER_PETROLEUM_PRODUCTS);
        etsDescriptionMap.put("Other Primary Solid Biomass", OTHER_PRIMARY_SOLID_BIOMASS);
        etsDescriptionMap.put("Oxygen Steel Furnace Gas", OXYGEN_STEEL_FURNACE_GAS);
        etsDescriptionMap.put("Paraffin Waxes", PARAFFIN_WAXES);
        etsDescriptionMap.put("Patent Fuel", PATENT_FUEL);
        etsDescriptionMap.put("Peat", PEAT);
        etsDescriptionMap.put("Petrol", PETROL);
        etsDescriptionMap.put("Petroleum Coke", PETROLEUM_COKE);
        etsDescriptionMap.put("Pilot and Purge Flare Gas", PILOT_AND_PURGE_FLARE_GAS);
        etsDescriptionMap.put("Pilot Flare Gas", PILOT_FLARE_GAS);
        etsDescriptionMap.put("Propane", PROPANE);
        etsDescriptionMap.put("Refinery Feedstocks", REFINERY_FEEDSTOCKS);
        etsDescriptionMap.put("Refinery Gas", REFINERY_GAS);
        etsDescriptionMap.put("Residual Fuel Oil", RESIDUAL_FUEL_OIL);
        etsDescriptionMap.put("Scrap Tyres", SCRAP_TYRES);
        etsDescriptionMap.put("Shale Oil", SHALE_OIL);
        etsDescriptionMap.put("Sludge Gas", SLUDGE_GAS);
        etsDescriptionMap.put("Sour Gas", SOUR_GAS);
        etsDescriptionMap.put("Sour Gas Flare", SOUR_GAS_FLARE);
        etsDescriptionMap.put("SDF", SDF);
        etsDescriptionMap.put("SSF", SSF);
        etsDescriptionMap.put("SRF", SRF);
        etsDescriptionMap.put("Sub-Bituminous Coal", SUB_BITUMINOUS_COAL);
        etsDescriptionMap.put("Waste Oils", WASTE_OILS);
        etsDescriptionMap.put("Waste Solvents", WASTE_SOLVENTS);
        etsDescriptionMap.put("Waste Tyres", WASTE_TYRES);
        etsDescriptionMap.put("White Spirit & SBP", WHITE_SPIRIT_SBP);
        etsDescriptionMap.put("Wood/Wood Waste", WOOD_WOOD_WASTE);
    }
    
    public static SourceStreamType getPmrvType(String etsType) {
        return etsTypeMap.get(etsType);
    }
    
    public static SourceStreamDescription getPmrvDescription(String etsDescription) {
        return etsDescriptionMap.get(etsDescription);
    }
}
