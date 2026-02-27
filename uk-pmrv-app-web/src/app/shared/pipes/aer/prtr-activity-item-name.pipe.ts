import { Pipe, PipeTransform } from '@angular/core';

import { PollutantRegisterActivities, PRTRCodes } from 'pmrv-api';

@Pipe({
  name: 'prtrActivityItemName',
  standalone: false,
})
export class PrtrActivityItemNamePipe implements PipeTransform {
  transform(activityItem?: PRTRCodes['codes'][number] | PollutantRegisterActivities['activities'][number]): string {
    switch (activityItem) {
      case '_1_A_MINERAL_OIL_GAS_REFINERIES':
        return '1.(a) Mineral oil and gas refineries';
      case '_1_B_INSTALLATIONS_FOR_GASIFICATION_LIGUEFACTION':
        return '1.(b) Installations for gasification and liquefaction';
      case '_1_C_THERMAL_POWER_STATIONS_OTHER_COMBUSTION_INSTALLATIONS':
        return '1.(c) Thermal power stations and other combustion installations';
      case '_1_D_COKE_OVENS':
        return '1.(d) Coke Ovens';
      case '_1_E_COAL_ROLLING_MILLS':
        return '1.(e) Coal rolling mills';
      case '_1_F_INSTALLATIONS_MANUFACTURE_OF_COAL_PRODUCTS_SOLID_SMOKELESS_FUEL':
        return '1.(f) Installations for the manufacture of coal products and solid smokeless fuel';
      case '_2_A_METAL_ORE_ROASTING_OR_SINTERING_INSTALLATIONS':
        return '2.(a) Metal ore (including sulfide ore) roasting or sintering installations';
      case '_2_B_INSTALLATIONS_FOR_PRODUCTION_OF_PIG_IRON_OR_STEEL':
        return '2.(b) Installations for the production of pig iron or steel (primary or secondary melting) including continuous casting';
      case '_2_C_1_HOT_ROLLING_MILLS':
        return '2.(c).(i) Installations for the processing of ferrous metals: hot-rolling mills';
      case '_2_C_2_SMITHERIES_WITH_HAMMERS':
        return '2.(c).(ii) Installations for the processing of ferrous metals: smitheries with hammers';
      case '_2_C_3_APPLICATION_OF_PROTECTIVE_FUSED_METAL_COATS':
        return '2.(c).(iii) Installations for the processing of ferrous metals: application of protective fused metal coats';
      case '_2_D_FERROUS_METAL_FOUNDRIES_WITH_PRODUCTION_CAPACITY_OF_20_TONNES_PER_DAY':
        return '2.(d) Ferrous metal foundries with a production capacity of 20 tonnes per day';
      case '_2_E_1_PRODUCTION_OF_NON_FERROUS_CRUDE_METALS':
        return '2.(e).(i) Installations: for the production of non-ferrous crude metals from ore, concentrates or secondary raw materials by metallurgical, chemical, or electrolytic processes';
      case '_2_E_2_SMELTING_OF_NON_FERROUS_METALS':
        return '2.(e).(ii) Installations: for the smelting, including the alloying, of non-ferrous metals, including recovered products (refining, foundry casting and so on)';
      case '_2_F_INSTALLATIONS_FOR_SURFACE_TREATMENT_OF_METALS_AND_PLASTIC_MATERIALS':
        return '2.(f) Installations for surface treatment of metals and plastic materials using an electrolytic or chemical process';
      case '_3_A_UNDERGROUND_MINING_AND_RELATED_OPERATIONS':
        return '3.(a) Underground mining and related operations';
      case '_3_B_OPENCAST_MINING_AND_QUARRYING':
        return '3.(b) Opencast mining and quarrying';
      case '_3_C_1_CEMENT_CLINKER_IN_ROTARY_KILNS':
        return '3.(c).(i) Installations for the production of: cement clinker in rotary kilns';
      case '_3_C_2_LIME_IN_ROTARY_KILNS':
        return '3.(c).(ii) Installations for the production of: lime in rotary kilns';
      case '_3_C_3_CEMENT_CLINKER_OR_LIME_IN_OTHER_FURNACES':
        return '3.(c).(iii) Installations for the production of: cement clinker or lime in other furnaces';
      case '_3_D_INSTALLATIONS_FOR_THE_PRODUCTION_OF_ASBESTOS_AND_MANUFACTURE_OF_ASBESTOS_BASED_PRODUCTS':
        return '3.(d) Installations for the production of asbestos and the manufacture of asbestos-based products';
      case '_3_E_INSTALLATIONS_FOR_THE_MANUFACTURE_OF_GLASS_INCLUDING_GLASS_FIBRE':
        return '3.(e) Installations for the manufacture of glass, including glass fibre';
      case '_3_F_INSTALLATIONS_FOR_MELTING_MINERAL_SUBSTANCES':
        return '3.(f) Installations for melting mineral substances, including the production of mineral fibres';
      case '_3_G_INSTALLATIONS_FOR_THE_MANUFACTURE_OF_CERAMIC_PRODUCTS':
        return '3.(g) Installations for the manufacture of ceramic products by firing, in particular roofing tiles, bricks, refractory bricks, tiles, stoneware or porcelain';
      case '_4_A_1_SIMPLE_HYDROCARBONS':
        return '4.(a).(i) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: simple hydrocarbons';
      case '_4_A_2_OXYGEN_CONTAINING_HYDROCARBONS':
        return '4.(a).(ii) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: oxygen-containing hydrocarbons';
      case '_4_A_3_SULFUROUS_HYDROCARBONS':
        return '4.(a).(iii)  Chemical installations for the production on an industrial scale of basic organic chemicals, such as: sulfurous hydrocarbons';
      case '_4_A_4_NITROGENOUS_HYDROCARBONS':
        return '4.(a).(iv) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: nitrogenous hydrocarbons';
      case '_4_A_5_PHOSPHOROUS_HYDROCARBONS':
        return '4.(a).(v) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: phosphorus-containing hydrocarbons';
      case '_4_A_6_HALOGENIC_HYDROCARBONS':
        return '4.(a).(vi) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: halogenic hydrocarbons';
      case '_4_A_7_ORGANOMETALLIC_COMPOUNDS':
        return '4.(a).(vii) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: organometallic compounds';
      case '_4_A_8_BASIC_PLASTIC_MATERIALS':
        return '4.(a).(viii) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: basic plastic materials';
      case '_4_A_9_SYNTHETIC_RUBBERS':
        return '4.(a).(ix) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: synthetic rubbers';
      case '_4_A_10_DYES_AND_PIGMENTS':
        return '4.(a).(x) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: dyes and pigments';
      case '_4_A_11_SURFACE_ACTIVE_AGENTS_AND_SURFACTANS':
        return '4.(a).(xi) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: surface-active agents and surfactants';
      case '_4_B_1_GASES':
        return '4.(b).(i) Chemical installations for the production on an industrial scale of basic inorganic chemicals, such as: gases';
      case '_4_B_2_ACIDS':
        return '4.(b).(ii) Chemical installations for the production on an industrial scale of basic inorganic chemicals, such as: acids';
      case '_4_B_3_BASES':
        return '4.(b).(iii) Chemical installations for the production on an industrial scale of basic inorganic chemicals, such as: bases';
      case '_4_B_4_SALTS':
        return '4.(b).(iv) Chemical installations for the production on an industrial scale of basic inorganic chemicals, such as: salts';
      case '_4_B_5_NON_METALS_METAL_OXIDES':
        return '4.(b).(v) Chemical installations for the production on an industrial scale of basic inorganic chemicals, such as: non-metals, metal oxides or other inorganic compounds';
      case '_4_C_CHEMICAL_INSTALLATIONS_FOR_PRODUCTION_ON_INDUSTRIAL_SCALE_OF_PHOSPHOROUS':
        return '4.(c) Chemical installations for the production on an industrial scale of phosphorous-, nitrogen- or potassium-based fertilisers';
      case '_4_D_CHEMICAL_INSTALLATIONS_FOR_PRODUCTION_ON_INDUSTRIAL_SCALE_OF_BASIC_PLANT_HEALTH':
        return '4.(d) Chemical installations for the production on an industrial scale of basic plant health products and of biocides';
      case '_4_E_INSTALLATIONS_USING_CHEMICAL_OR_BIOLOGICAL_PROCESS':
        return '4.(e) Installations using a chemical or biological process for the production on an industrial scale of basic pharmaceutical products';
      case '_4_F_INSTALLATIONS_FOR_PRODUCTION_ON_INDUSTRIAL_SCALE_OF_EXPLOSIVES':
        return '4.(f) Installations for the production on an industrial scale of explosives and pyrotechnic products';
      case '_5_A_INSTALLATIONS_FOR_THE_RECOVERY_OR_DISPOSAL_OF_HAZARDOUS_WASTE':
        return '5.(a) Installations for the recovery or disposal of hazardous waste';
      case '_5_B_INSTALLATIONS_FOR_THE_INCINERATION_OF_NON_HAZARDOUS_WASTE':
        return '5.(b) Installations for the incineration of non-hazardous waste';
      case '_5_C_INSTALLATIONS_FOR_THE_DISPOSAL_OF_NON_HAZARDOUS_WASTE':
        return '5.(c) Installations for the disposal of non-hazardous waste';
      case '_5_D_LANDFILLS':
        return '5.(d) Landfills';
      case '_5_E_INSTALLATIONS_FOR_DISPOSAL_OR_RECYCLING_OF_ANIMAL_CARCASSES':
        return '5.(e) Installations for the disposal or recycling of animal carcasses and animal waste';
      case '_5_F_URBAN_WASTE_WATER_TREATMENT_PLANTS':
        return '5.(f) Urban waste-water treatment plants';
      case '_5_G_INDEPENDENTLY_OPERATED_INDUSTRIAL_WASTE_WATER_TREATMENT':
        return '5.(g) Independently operated industrial waste-water treatment plants which serve one or more activities of this annex';
      case '_6_A_INDUSTRIAL_PLANTS_FOR_PRODUCTION_OF_PULP_FROM_WOOD_OR_OTHER_FIBROUS':
        return '6.(a) Industrial plants for the production of pulp from timber or similar fibrous materials';
      case '_6_B_INDUSTRIAL_PLANTS_FOR_PRODUCTION_OF_PAPER_AND_BOARD':
        return '6.(b) Industrial plants for the production of paper and board and other primary wood products (such as chipboard, fibreboard and plywood)';
      case '_6_C_INDUSTRIAL_PLANTS_FOR_PRESERVATION_OF_WOOD':
        return '6.(c) Industrial plants for the preservation of wood and wood products with chemicals';
      case '_7_A_1_WITH_40000_PLACES_FOR_POULTRY':
        return '7.(a).(i) Installations for the intensive rearing of poultry or pigs: with 40,000 places for poultry';
      case '_7_A_2_WITH_2000_PLACES_FOR_PRODUCTION_PIGS':
        return '7.(a).(ii) Installations for the intensive rearing of poultry or pigs: with 2,000 places for production pigs (over 30 kg)';
      case '_7_A_3_WITH_750_PLACES_FOR_SOWS':
        return '7.(a).(iii) Installations for the intensive rearing of poultry or pigs: with 750 places for sows';
      case '_7_B_INTENSIVE_AQUACULTURE':
        return '7.(b) Intensive aquaculture';
      case '_8_A_SLAGHTERHOUSES':
        return '8.(a) Slaughterhouses';
      case '_8_B_1_ANIMAL_RAW_MATERIALS':
        return '8.(b).(i) Treatment and processing intended for the production of food and beverage products from: animal raw materials (other than milk)';
      case '_8_B_2_VEGETABLE_RAW_MATERIALS':
        return '8.(b).(ii) Treatment and processing intended for the production of food and beverage products from: vegetable raw materials';
      case '_8_C_TREATMENT_AND_PROCESSING_OF_MILK':
        return '8.(c) Treatment and processing of milk';
      case '_9_A_PLANTS_FOR_THE_PRETREATMENT':
        return '9.(a) Plants for the pre-treatment (operations such as washing, bleaching, mercerisation) or dyeing of fibres or textiles';
      case '_9_B_PLANTS_FOR_THE_TANNING':
        return '9.(b) Plants for the tanning of hides and skins';
      case '_9_C_INSTALLATIONS_FOR_THE_SURFACE_TREATMENT':
        return '9.(c) Installations for the surface treatment of substances, objects or products using organic solvents, in particular for dressing, printing, coating, degreasing, waterproofing, sizing, painting, cleaning, or impregnating';
      case '_9_D_INSTALLATIONS_FOR_THE_PRODUCTION_OF_CARBON':
        return '9.(d) Installations for the production of carbon (hard-burnt coal) or electro-graphite by means of incineration or graphitisation';
      case '_9_E_INSTALLATIONS_FOR_THE_BUILDING_AND_PAINTING_OR_REMOVAL_OF_PAINT':
        return '9.(e) Installations for the building of and painting or removal of paint from ships';

      //OLD CODES
      case '_1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION':
        return '1.A.1.a Public Electricity and Heat Production';
      case '_1_A_1_B_PETROLEUM_REFINING':
        return '1.A.1.b Petroleum refining';
      case '_1_A_1_C_MANUFACTURE_OF_SOLID_FUELS_AND_OTHER_ENERGY_INDUSTRIES':
        return '1.A.1.c Manufacture of Solid Fuels and Other Energy Industries';

      case '_1_A_2_A_IRON_AND_STEEL':
        return '1.A.2.a Iron and Steel';
      case '_1_A_2_B_NON_FERROUS_METALS':
        return '1.A.2.b Non-ferrous Metals';
      case '_1_A_2_C_CHEMICALS':
        return '1.A.2.c Chemicals';
      case '_1_A_2_D_PULP_PAPER_AND_PRINT':
        return '1.A.2.d Pulp, Paper and Print';
      case '_1_A_2_E_FOOD_PROCESSING_BEVERAGES_AND_TOBACCO':
        return '1.A.2.e Food Processing, Beverages and Tobacco';
      case '_1_A_2_F_NON_METALLIC_MINERALS':
        return '1.A.2.f Non-metallic minerals';
      case '_1_A_2_GVII_MOBILE_COMBUSTION_IN_MANUFACTURING_INDUSTRIES_AND_CONSTRUCTION':
        return '1.A.2.gvii Mobile combustion in manufacturing industries and construction';
      case '_1_A_2_GVIII_STATIONARY_COMBUSTION_IN_MANUFACTURING_AND_CONSTRUCTION':
        return '1.A.2.gviii Stationary combustion in manufacturing and construction: Other';

      case '_1_A_3_AI_INTERNATIONAL_AVIATION':
        return '1.A.3.ai International Aviation';
      case '_1_A_3_AII_CIVIL_AVIATION':
        return '1.A.3.aii Civil Aviation';
      case '_1_A_3_B_ROAD_TRANSPORTATION':
        return '1.A.3.b Road Transportation';
      case '_1_A_3_C_RAILWAYS':
        return '1.A.3.c Railways';
      case '_1_A_3_DI_INTERNATIONAL_NAVIGATION':
        return '1.A.3.di International Navigation';
      case '_1_A_3_DII_NATIONAL_NAVIGATION':
        return '1.A.3.dii National Navigation';
      case '_1_A_3_E_OTHER':
        return '1.A.3.e Other';

      case '_1_A_4_A_COMMERCIAL_INSTITUTIONAL_COMBUSTION':
        return '1.A.4.a Commercial / Institutional Combustion';
      case '_1_A_4_B_RESIDENTIAL':
        return '1.A.4.b Residential';
      case '_1_A_4_C_AGRICULTURE_FORESTRY_FISHING':
        return '1.A.4.c Agriculture / Forestry / Fishing';

      case '_1_A_5_A_OTHER_STATIONARY_INCLUDING_MILITARY':
        return '1.A.5.a Other, Stationary (including Military)';
      case '_1_A_5_B_OTHER_MOBILE_INCLUDING_MILITARY':
        return '1.A.5.b Other, Mobile (including military)';

      case '_1_B_1_A_COAL_MINING_AND_HANDLING':
        return '1.B.1.a Coal Mining and Handling';
      case '_1_B_1_B_SOLID_FUEL_TRANSFORMATION':
        return '1.B.1.b Solid fuel transformation';
      case '_1_B_1_C_OTHER':
        return '1.B.1.c Other';

      case '_1_B_2_A_OIL':
        return '1.B.2.a Oil';
      case '_1_B_2_B_NATURAL_GAS':
        return '1.B.2.b Natural gas';
      case '_1_B_2_C_VENTING_AND_FLARING':
        return '1.B.2.c Venting and flaring';

      case '_2_A_1_CEMENT_PRODUCTION':
        return '2.A.1 Cement Production';
      case '_2_A_2_LIME_PRODUCTION':
        return '2.A.2 Lime Production';
      case '_2_A_3_GLASS_PRODUCTION':
        return '2.A.3 Glass Production';
      case '_2_A_4_OTHER_PROCESS_USES_OF_CARBONATES':
        return '2.A.4 Other Process uses of Carbonates';

      case '_2_B_1_AMMONIA_PRODUCTION':
        return '2.B.1 Ammonia Production';
      case '_2_B_2_NITRIC_ACID_PRODUCTION':
        return '2.B.2 Nitric Acid Production';
      case '_2_B_3_ADIPIC_ACID_PRODUCTION':
        return '2.B.3 Adipic Acid Production';
      case '_2_B_4_CAPROLACTAM_GLYOXAL_AND_GLYOXYLIC_ACID_PRODUCTION':
        return '2.B.4 Caprolactam, Glyoxal and Glyoxylic Acid Production';
      case '_2_B_5_CARBIDE_PRODUCTION':
        return '2.B.5 Carbide production';
      case '_2_B_6_TITANIUM_DIOXIDE_PRODUCTION':
        return '2.B.6 Titanium Dioxide Production';
      case '_2_B_7_SODA_ASH_PRODUCTION':
        return '2.B.7 Soda Ash Production';
      case '_2_B_8_PETROCHEMICAL_AND_CARBON_BLACK_PRODUCTION':
        return '2.B.8 Petrochemical and Carbon Black Production';
      case '_2_B_9_FLUOROCHEMICAL_PRODUCTION':
        return '2.B.9 Fluorochemical Production';
      case '_2_B_10_OTHER':
        return '2.B.10 Other';

      case '_2_C_1_IRON_AND_STEEL_PRODUCTION':
        return '2.C.1 Iron and Steel production';
      case '_2_C_2_FERROALLOYS_PRODUCTION':
        return '2.C.2 Ferroalloys Production';
      case '_2_C_3_ALUMINIUM_PRODUCTION':
        return '2.C.3 Aluminium Production';
      case '_2_C_4_MAGNESIUM_PRODUCTION':
        return '2.C.4 Magnesium Production';
      case '_2_C_5_LEAD_PRODUCTION':
        return '2.C.5 Lead Production';
      case '_2_C_6_ZINC_PRODUCTION':
        return '2.C.6 Zinc Production';
      case '_2_C_7_OTHER_METAL_PRODUCTION':
        return '2.C.7 Other Metal Production';

      case '_2_D_1_LUBRICANT_USE':
        return '2.D.1 Lubricant Use';
      case '_2_D_2_PARAFFIN_WAX_USE':
        return '2.D.2 Paraffin Wax Use';
      case '_2_D_3_OTHER':
        return '2.D.3 Other';

      case '_2_E_1_INTEGRATED_CIRCUIT_OR_SEMICONDUCTOR':
        return '2.E.1 Integrated Circuit or Semiconductor';
      case '_2_E_2_TFT_FLAT_PANEL_DISPLAY':
        return '2.E.2 TFT Flat Panel Display';
      case '_2_E_3_PHOTOVOLTAICS':
        return '2.E.3 Photovoltaics';
      case '_2_E_4_HEAT_TRANSFER_FLUID':
        return '2.E.4 Heat Transfer Fluid';
      case '_2_E_5_OTHER':
        return '2.E.5 Other';

      case '_2_F_1_REFRIGERATION_AND_AIR_CONDITIONING_EQUIPMENT':
        return '2.F.1 Refrigeration and Air Conditioning Equipment';
      case '_2_F_2_FOAM_BLOWING_AGENTS':
        return '2.F.2 Foam Blowing Agents';
      case '_2_F_3_FIRE_EXTINGUISHERS':
        return '2.F.3 Fire Extinguishers';
      case '_2_F_4_AEROSOLS':
        return '2.F.4 Aerosols';
      case '_2_F_5_SOLVENTS':
        return '2.F.5 Solvents';
      case '_2_F_6_OTHER':
        return '2.F.6 Other';

      case '_2_G_1_ELECTRICAL_EQUIPMENT':
        return '2.G.1 Electrical Equipment';
      case '_2_G_2_SF6_AND_PFCS_FROM_OTHER_PRODUCT_USE':
        return '2.G.2 SF6 and PFCs from Other Product Use';
      case '_2_G_3_N2O_FROM_PRODUCT_USES':
        return '2.G.3 N2O from Product Uses';
      case '_2_G_4_OTHER':
        return '2.G.4 Other';

      case '_2_H_OTHER':
        return '2.H Other';
      default:
        return '';
    }
  }
}
