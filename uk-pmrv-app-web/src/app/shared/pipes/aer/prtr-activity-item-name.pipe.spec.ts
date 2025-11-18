import { PrtrActivityItemNamePipe } from '@shared/pipes/aer/prtr-activity-item-name.pipe';

describe('PrtrActivityItemNamePipe', () => {
  let pipe: PrtrActivityItemNamePipe;

  beforeEach(async () => {
    pipe = new PrtrActivityItemNamePipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('_1_A_MINERAL_OIL_GAS_REFINERIES')).toEqual('1.(a) Mineral oil and gas refineries');
    expect(pipe.transform('_1_B_INSTALLATIONS_FOR_GASIFICATION_LIGUEFACTION')).toEqual(
      '1.(b) Installations for gasification and liquefaction',
    );
    expect(pipe.transform('_1_C_THERMAL_POWER_STATIONS_OTHER_COMBUSTION_INSTALLATIONS')).toEqual(
      '1.(c) Thermal power stations and other combustion installations',
    );
    expect(pipe.transform('_1_D_COKE_OVENS')).toEqual('1.(d) Coke Ovens');
    expect(pipe.transform('_1_E_COAL_ROLLING_MILLS')).toEqual('1.(e) Coal rolling mills');
    expect(pipe.transform('_1_F_INSTALLATIONS_MANUFACTURE_OF_COAL_PRODUCTS_SOLID_SMOKELESS_FUEL')).toEqual(
      '1.(f) Installations for the manufacture of coal products and solid smokeless fuel',
    );
    expect(pipe.transform('_2_A_METAL_ORE_ROASTING_OR_SINTERING_INSTALLATIONS')).toEqual(
      '2.(a) Metal ore (including sulfide ore) roasting or sintering installations',
    );
    expect(pipe.transform('_2_B_INSTALLATIONS_FOR_PRODUCTION_OF_PIG_IRON_OR_STEEL')).toEqual(
      '2.(b) Installations for the production of pig iron or steel (primary or secondary melting) including continuous casting',
    );
    expect(pipe.transform('_2_C_1_HOT_ROLLING_MILLS')).toEqual(
      '2.(c).(i) Installations for the processing of ferrous metals: hot-rolling mills',
    );
    expect(pipe.transform('_2_C_2_SMITHERIES_WITH_HAMMERS')).toEqual(
      '2.(c).(ii) Installations for the processing of ferrous metals: smitheries with hammers',
    );
    expect(pipe.transform('_2_C_3_APPLICATION_OF_PROTECTIVE_FUSED_METAL_COATS')).toEqual(
      '2.(c).(iii) Installations for the processing of ferrous metals: application of protective fused metal coats',
    );
    expect(pipe.transform('_2_D_FERROUS_METAL_FOUNDRIES_WITH_PRODUCTION_CAPACITY_OF_20_TONNES_PER_DAY')).toEqual(
      '2.(d) Ferrous metal foundries with a production capacity of 20 tonnes per day',
    );
    expect(pipe.transform('_2_E_1_PRODUCTION_OF_NON_FERROUS_CRUDE_METALS')).toEqual(
      '2.(e).(i) Installations: for the production of non-ferrous crude metals from ore, concentrates or secondary raw materials by metallurgical, chemical, or electrolytic processes',
    );
    expect(pipe.transform('_2_E_2_SMELTING_OF_NON_FERROUS_METALS')).toEqual(
      '2.(e).(ii) Installations: for the smelting, including the alloying, of non-ferrous metals, including recovered products (refining, foundry casting and so on)',
    );
    expect(pipe.transform('_2_F_INSTALLATIONS_FOR_SURFACE_TREATMENT_OF_METALS_AND_PLASTIC_MATERIALS')).toEqual(
      '2.(f) Installations for surface treatment of metals and plastic materials using an electrolytic or chemical process',
    );
    expect(pipe.transform('_3_A_UNDERGROUND_MINING_AND_RELATED_OPERATIONS')).toEqual(
      '3.(a) Underground mining and related operations',
    );
    expect(pipe.transform('_3_B_OPENCAST_MINING_AND_QUARRYING')).toEqual('3.(b) Opencast mining and quarrying');
    expect(pipe.transform('_3_C_1_CEMENT_CLINKER_IN_ROTARY_KILNS')).toEqual(
      '3.(c).(i) Installations for the production of: cement clinker in rotary kilns',
    );
    expect(pipe.transform('_3_C_2_LIME_IN_ROTARY_KILNS')).toEqual(
      '3.(c).(ii) Installations for the production of: lime in rotary kilns',
    );
    expect(pipe.transform('_3_C_3_CEMENT_CLINKER_OR_LIME_IN_OTHER_FURNACES')).toEqual(
      '3.(c).(iii) Installations for the production of: cement clinker or lime in other furnaces',
    );
    expect(
      pipe.transform('_3_D_INSTALLATIONS_FOR_THE_PRODUCTION_OF_ASBESTOS_AND_MANUFACTURE_OF_ASBESTOS_BASED_PRODUCTS'),
    ).toEqual('3.(d) Installations for the production of asbestos and the manufacture of asbestos-based products');
    expect(pipe.transform('_3_E_INSTALLATIONS_FOR_THE_MANUFACTURE_OF_GLASS_INCLUDING_GLASS_FIBRE')).toEqual(
      '3.(e) Installations for the manufacture of glass, including glass fibre',
    );
    expect(pipe.transform('_3_F_INSTALLATIONS_FOR_MELTING_MINERAL_SUBSTANCES')).toEqual(
      '3.(f) Installations for melting mineral substances, including the production of mineral fibres',
    );
    expect(pipe.transform('_3_G_INSTALLATIONS_FOR_THE_MANUFACTURE_OF_CERAMIC_PRODUCTS')).toEqual(
      '3.(g) Installations for the manufacture of ceramic products by firing, in particular roofing tiles, bricks, refractory bricks, tiles, stoneware or porcelain',
    );
    expect(pipe.transform('_4_A_1_SIMPLE_HYDROCARBONS')).toEqual(
      '4.(a).(i) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: simple hydrocarbons',
    );
    expect(pipe.transform('_4_A_2_OXYGEN_CONTAINING_HYDROCARBONS')).toEqual(
      '4.(a).(ii) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: oxygen-containing hydrocarbons',
    );
    expect(pipe.transform('_4_A_3_SULFUROUS_HYDROCARBONS')).toEqual(
      '4.(a).(iii)  Chemical installations for the production on an industrial scale of basic organic chemicals, such as: sulfurous hydrocarbons',
    );
    expect(pipe.transform('_4_A_4_NITROGENOUS_HYDROCARBONS')).toEqual(
      '4.(a).(iv) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: nitrogenous hydrocarbons',
    );
    expect(pipe.transform('_4_A_5_PHOSPHOROUS_HYDROCARBONS')).toEqual(
      '4.(a).(v) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: phosphorus-containing hydrocarbons',
    );
    expect(pipe.transform('_4_A_6_HALOGENIC_HYDROCARBONS')).toEqual(
      '4.(a).(vi) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: halogenic hydrocarbons',
    );
    expect(pipe.transform('_4_A_7_ORGANOMETALLIC_COMPOUNDS')).toEqual(
      '4.(a).(vii) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: organometallic compounds',
    );
    expect(pipe.transform('_4_A_8_BASIC_PLASTIC_MATERIALS')).toEqual(
      '4.(a).(viii) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: basic plastic materials',
    );
    expect(pipe.transform('_4_A_9_SYNTHETIC_RUBBERS')).toEqual(
      '4.(a).(ix) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: synthetic rubbers',
    );
    expect(pipe.transform('_4_A_10_DYES_AND_PIGMENTS')).toEqual(
      '4.(a).(x) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: dyes and pigments',
    );
    expect(pipe.transform('_4_A_11_SURFACE_ACTIVE_AGENTS_AND_SURFACTANS')).toEqual(
      '4.(a).(xi) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: surface-active agents and surfactants',
    );
    expect(pipe.transform('_4_B_1_GASES')).toEqual(
      '4.(b).(i) Chemical installations for the production on an industrial scale of basic inorganic chemicals, such as: gases',
    );
    expect(pipe.transform('_4_B_2_ACIDS')).toEqual(
      '4.(b).(ii) Chemical installations for the production on an industrial scale of basic inorganic chemicals, such as: acids',
    );
    expect(pipe.transform('_4_B_3_BASES')).toEqual(
      '4.(b).(iii) Chemical installations for the production on an industrial scale of basic inorganic chemicals, such as: bases',
    );
    expect(pipe.transform('_4_B_4_SALTS')).toEqual(
      '4.(b).(iv) Chemical installations for the production on an industrial scale of basic inorganic chemicals, such as: salts',
    );
    expect(pipe.transform('_4_B_5_NON_METALS_METAL_OXIDES')).toEqual(
      '4.(b).(v) Chemical installations for the production on an industrial scale of basic inorganic chemicals, such as: non-metals, metal oxides or other inorganic compounds',
    );
    expect(pipe.transform('_4_C_CHEMICAL_INSTALLATIONS_FOR_PRODUCTION_ON_INDUSTRIAL_SCALE_OF_PHOSPHOROUS')).toEqual(
      '4.(c) Chemical installations for the production on an industrial scale of phosphorous-, nitrogen- or potassium-based fertilisers',
    );
    expect(
      pipe.transform('_4_D_CHEMICAL_INSTALLATIONS_FOR_PRODUCTION_ON_INDUSTRIAL_SCALE_OF_BASIC_PLANT_HEALTH'),
    ).toEqual(
      '4.(d) Chemical installations for the production on an industrial scale of basic plant health products and of biocides',
    );
    expect(pipe.transform('_4_E_INSTALLATIONS_USING_CHEMICAL_OR_BIOLOGICAL_PROCESS')).toEqual(
      '4.(e) Installations using a chemical or biological process for the production on an industrial scale of basic pharmaceutical products',
    );
    expect(pipe.transform('_4_F_INSTALLATIONS_FOR_PRODUCTION_ON_INDUSTRIAL_SCALE_OF_EXPLOSIVES')).toEqual(
      '4.(f) Installations for the production on an industrial scale of explosives and pyrotechnic products',
    );
    expect(pipe.transform('_5_A_INSTALLATIONS_FOR_THE_RECOVERY_OR_DISPOSAL_OF_HAZARDOUS_WASTE')).toEqual(
      '5.(a) Installations for the recovery or disposal of hazardous waste',
    );
    expect(pipe.transform('_5_B_INSTALLATIONS_FOR_THE_INCINERATION_OF_NON_HAZARDOUS_WASTE')).toEqual(
      '5.(b) Installations for the incineration of non-hazardous waste',
    );
    expect(pipe.transform('_5_C_INSTALLATIONS_FOR_THE_DISPOSAL_OF_NON_HAZARDOUS_WASTE')).toEqual(
      '5.(c) Installations for the disposal of non-hazardous waste',
    );
    expect(pipe.transform('_5_D_LANDFILLS')).toEqual('5.(d) Landfills');
    expect(pipe.transform('_5_E_INSTALLATIONS_FOR_DISPOSAL_OR_RECYCLING_OF_ANIMAL_CARCASSES')).toEqual(
      '5.(e) Installations for the disposal or recycling of animal carcasses and animal waste',
    );
    expect(pipe.transform('_5_F_URBAN_WASTE_WATER_TREATMENT_PLANTS')).toEqual(
      '5.(f) Urban waste-water treatment plants',
    );
    expect(pipe.transform('_5_G_INDEPENDENTLY_OPERATED_INDUSTRIAL_WASTE_WATER_TREATMENT')).toEqual(
      '5.(g) Independently operated industrial waste-water treatment plants which serve one or more activities of this annex',
    );
    expect(pipe.transform('_6_A_INDUSTRIAL_PLANTS_FOR_PRODUCTION_OF_PULP_FROM_WOOD_OR_OTHER_FIBROUS')).toEqual(
      '6.(a) Industrial plants for the production of pulp from timber or similar fibrous materials',
    );
    expect(pipe.transform('_6_B_INDUSTRIAL_PLANTS_FOR_PRODUCTION_OF_PAPER_AND_BOARD')).toEqual(
      '6.(b) Industrial plants for the production of paper and board and other primary wood products (such as chipboard, fibreboard and plywood)',
    );
    expect(pipe.transform('_6_C_INDUSTRIAL_PLANTS_FOR_PRESERVATION_OF_WOOD')).toEqual(
      '6.(c) Industrial plants for the preservation of wood and wood products with chemicals',
    );
    expect(pipe.transform('_7_A_1_WITH_40000_PLACES_FOR_POULTRY')).toEqual(
      '7.(a).(i) Installations for the intensive rearing of poultry or pigs: with 40,000 places for poultry',
    );
    expect(pipe.transform('_7_A_2_WITH_2000_PLACES_FOR_PRODUCTION_PIGS')).toEqual(
      '7.(a).(ii) Installations for the intensive rearing of poultry or pigs: with 2,000 places for production pigs (over 30 kg)',
    );
    expect(pipe.transform('_7_A_3_WITH_750_PLACES_FOR_SOWS')).toEqual(
      '7.(a).(iii) Installations for the intensive rearing of poultry or pigs: with 750 places for sows',
    );
    expect(pipe.transform('_7_B_INTENSIVE_AQUACULTURE')).toEqual('7.(b) Intensive aquaculture');
    expect(pipe.transform('_8_A_SLAGHTERHOUSES')).toEqual('8.(a) Slaughterhouses');
    expect(pipe.transform('_8_B_1_ANIMAL_RAW_MATERIALS')).toEqual(
      '8.(b).(i) Treatment and processing intended for the production of food and beverage products from: animal raw materials (other than milk)',
    );
    expect(pipe.transform('_8_B_2_VEGETABLE_RAW_MATERIALS')).toEqual(
      '8.(b).(ii) Treatment and processing intended for the production of food and beverage products from: vegetable raw materials',
    );
    expect(pipe.transform('_8_C_TREATMENT_AND_PROCESSING_OF_MILK')).toEqual('8.(c) Treatment and processing of milk');
    expect(pipe.transform('_9_A_PLANTS_FOR_THE_PRETREATMENT')).toEqual(
      '9.(a) Plants for the pre-treatment (operations such as washing, bleaching, mercerisation) or dyeing of fibres or textiles',
    );
    expect(pipe.transform('_9_B_PLANTS_FOR_THE_TANNING')).toEqual('9.(b) Plants for the tanning of hides and skins');
    expect(pipe.transform('_9_C_INSTALLATIONS_FOR_THE_SURFACE_TREATMENT')).toEqual(
      '9.(c) Installations for the surface treatment of substances, objects or products using organic solvents, in particular for dressing, printing, coating, degreasing, waterproofing, sizing, painting, cleaning, or impregnating',
    );
    expect(pipe.transform('_9_D_INSTALLATIONS_FOR_THE_PRODUCTION_OF_CARBON')).toEqual(
      '9.(d) Installations for the production of carbon (hard-burnt coal) or electro-graphite by means of incineration or graphitisation',
    );
    expect(pipe.transform('_9_E_INSTALLATIONS_FOR_THE_BUILDING_AND_PAINTING_OR_REMOVAL_OF_PAINT')).toEqual(
      '9.(e) Installations for the building of and painting or removal of paint from ships',
    );

    //OLD CODES
    expect(pipe.transform('_1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION')).toEqual(
      '1.A.1.a Public Electricity and Heat Production',
    );
    expect(pipe.transform('_1_A_1_B_PETROLEUM_REFINING')).toEqual('1.A.1.b Petroleum refining');
    expect(pipe.transform('_1_A_1_C_MANUFACTURE_OF_SOLID_FUELS_AND_OTHER_ENERGY_INDUSTRIES')).toEqual(
      '1.A.1.c Manufacture of Solid Fuels and Other Energy Industries',
    );

    expect(pipe.transform('_1_A_2_A_IRON_AND_STEEL')).toEqual('1.A.2.a Iron and Steel');
    expect(pipe.transform('_1_A_2_B_NON_FERROUS_METALS')).toEqual('1.A.2.b Non-ferrous Metals');
    expect(pipe.transform('_1_A_2_C_CHEMICALS')).toEqual('1.A.2.c Chemicals');
    expect(pipe.transform('_1_A_2_D_PULP_PAPER_AND_PRINT')).toEqual('1.A.2.d Pulp, Paper and Print');
    expect(pipe.transform('_1_A_2_E_FOOD_PROCESSING_BEVERAGES_AND_TOBACCO')).toEqual(
      '1.A.2.e Food Processing, Beverages and Tobacco',
    );
    expect(pipe.transform('_1_A_2_F_NON_METALLIC_MINERALS')).toEqual('1.A.2.f Non-metallic minerals');
    expect(pipe.transform('_1_A_2_GVII_MOBILE_COMBUSTION_IN_MANUFACTURING_INDUSTRIES_AND_CONSTRUCTION')).toEqual(
      '1.A.2.gvii Mobile combustion in manufacturing industries and construction',
    );
    expect(pipe.transform('_1_A_2_GVIII_STATIONARY_COMBUSTION_IN_MANUFACTURING_AND_CONSTRUCTION')).toEqual(
      '1.A.2.gviii Stationary combustion in manufacturing and construction: Other',
    );

    expect(pipe.transform('_1_A_3_AI_INTERNATIONAL_AVIATION')).toEqual('1.A.3.ai International Aviation');
    expect(pipe.transform('_1_A_3_AII_CIVIL_AVIATION')).toEqual('1.A.3.aii Civil Aviation');
    expect(pipe.transform('_1_A_3_B_ROAD_TRANSPORTATION')).toEqual('1.A.3.b Road Transportation');
    expect(pipe.transform('_1_A_3_C_RAILWAYS')).toEqual('1.A.3.c Railways');
    expect(pipe.transform('_1_A_3_DI_INTERNATIONAL_NAVIGATION')).toEqual('1.A.3.di International Navigation');
    expect(pipe.transform('_1_A_3_DII_NATIONAL_NAVIGATION')).toEqual('1.A.3.dii National Navigation');
    expect(pipe.transform('_1_A_3_E_OTHER')).toEqual('1.A.3.e Other');

    expect(pipe.transform('_1_A_4_A_COMMERCIAL_INSTITUTIONAL_COMBUSTION')).toEqual(
      '1.A.4.a Commercial / Institutional Combustion',
    );
    expect(pipe.transform('_1_A_4_B_RESIDENTIAL')).toEqual('1.A.4.b Residential');
    expect(pipe.transform('_1_A_4_C_AGRICULTURE_FORESTRY_FISHING')).toEqual('1.A.4.c Agriculture / Forestry / Fishing');

    expect(pipe.transform('_1_A_5_A_OTHER_STATIONARY_INCLUDING_MILITARY')).toEqual(
      '1.A.5.a Other, Stationary (including Military)',
    );
    expect(pipe.transform('_1_A_5_B_OTHER_MOBILE_INCLUDING_MILITARY')).toEqual(
      '1.A.5.b Other, Mobile (including military)',
    );

    expect(pipe.transform('_1_B_1_A_COAL_MINING_AND_HANDLING')).toEqual('1.B.1.a Coal Mining and Handling');
    expect(pipe.transform('_1_B_1_B_SOLID_FUEL_TRANSFORMATION')).toEqual('1.B.1.b Solid fuel transformation');
    expect(pipe.transform('_1_B_1_C_OTHER')).toEqual('1.B.1.c Other');

    expect(pipe.transform('_1_B_2_A_OIL')).toEqual('1.B.2.a Oil');
    expect(pipe.transform('_1_B_2_B_NATURAL_GAS')).toEqual('1.B.2.b Natural gas');
    expect(pipe.transform('_1_B_2_C_VENTING_AND_FLARING')).toEqual('1.B.2.c Venting and flaring');

    expect(pipe.transform('_2_A_1_CEMENT_PRODUCTION')).toEqual('2.A.1 Cement Production');
    expect(pipe.transform('_2_A_2_LIME_PRODUCTION')).toEqual('2.A.2 Lime Production');
    expect(pipe.transform('_2_A_3_GLASS_PRODUCTION')).toEqual('2.A.3 Glass Production');
    expect(pipe.transform('_2_A_4_OTHER_PROCESS_USES_OF_CARBONATES')).toEqual('2.A.4 Other Process uses of Carbonates');

    expect(pipe.transform('_2_B_1_AMMONIA_PRODUCTION')).toEqual('2.B.1 Ammonia Production');
    expect(pipe.transform('_2_B_2_NITRIC_ACID_PRODUCTION')).toEqual('2.B.2 Nitric Acid Production');
    expect(pipe.transform('_2_B_3_ADIPIC_ACID_PRODUCTION')).toEqual('2.B.3 Adipic Acid Production');
    expect(pipe.transform('_2_B_4_CAPROLACTAM_GLYOXAL_AND_GLYOXYLIC_ACID_PRODUCTION')).toEqual(
      '2.B.4 Caprolactam, Glyoxal and Glyoxylic Acid Production',
    );
    expect(pipe.transform('_2_B_5_CARBIDE_PRODUCTION')).toEqual('2.B.5 Carbide production');
    expect(pipe.transform('_2_B_6_TITANIUM_DIOXIDE_PRODUCTION')).toEqual('2.B.6 Titanium Dioxide Production');
    expect(pipe.transform('_2_B_7_SODA_ASH_PRODUCTION')).toEqual('2.B.7 Soda Ash Production');
    expect(pipe.transform('_2_B_8_PETROCHEMICAL_AND_CARBON_BLACK_PRODUCTION')).toEqual(
      '2.B.8 Petrochemical and Carbon Black Production',
    );
    expect(pipe.transform('_2_B_9_FLUOROCHEMICAL_PRODUCTION')).toEqual('2.B.9 Fluorochemical Production');
    expect(pipe.transform('_2_B_10_OTHER')).toEqual('2.B.10 Other');

    expect(pipe.transform('_2_C_1_IRON_AND_STEEL_PRODUCTION')).toEqual('2.C.1 Iron and Steel production');
    expect(pipe.transform('_2_C_2_FERROALLOYS_PRODUCTION')).toEqual('2.C.2 Ferroalloys Production');
    expect(pipe.transform('_2_C_3_ALUMINIUM_PRODUCTION')).toEqual('2.C.3 Aluminium Production');
    expect(pipe.transform('_2_C_4_MAGNESIUM_PRODUCTION')).toEqual('2.C.4 Magnesium Production');
    expect(pipe.transform('_2_C_5_LEAD_PRODUCTION')).toEqual('2.C.5 Lead Production');
    expect(pipe.transform('_2_C_6_ZINC_PRODUCTION')).toEqual('2.C.6 Zinc Production');
    expect(pipe.transform('_2_C_7_OTHER_METAL_PRODUCTION')).toEqual('2.C.7 Other Metal Production');

    expect(pipe.transform('_2_D_1_LUBRICANT_USE')).toEqual('2.D.1 Lubricant Use');
    expect(pipe.transform('_2_D_2_PARAFFIN_WAX_USE')).toEqual('2.D.2 Paraffin Wax Use');
    expect(pipe.transform('_2_D_3_OTHER')).toEqual('2.D.3 Other');

    expect(pipe.transform('_2_E_1_INTEGRATED_CIRCUIT_OR_SEMICONDUCTOR')).toEqual(
      '2.E.1 Integrated Circuit or Semiconductor',
    );
    expect(pipe.transform('_2_E_2_TFT_FLAT_PANEL_DISPLAY')).toEqual('2.E.2 TFT Flat Panel Display');
    expect(pipe.transform('_2_E_3_PHOTOVOLTAICS')).toEqual('2.E.3 Photovoltaics');
    expect(pipe.transform('_2_E_4_HEAT_TRANSFER_FLUID')).toEqual('2.E.4 Heat Transfer Fluid');
    expect(pipe.transform('_2_E_5_OTHER')).toEqual('2.E.5 Other');

    expect(pipe.transform('_2_F_1_REFRIGERATION_AND_AIR_CONDITIONING_EQUIPMENT')).toEqual(
      '2.F.1 Refrigeration and Air Conditioning Equipment',
    );
    expect(pipe.transform('_2_F_2_FOAM_BLOWING_AGENTS')).toEqual('2.F.2 Foam Blowing Agents');
    expect(pipe.transform('_2_F_3_FIRE_EXTINGUISHERS')).toEqual('2.F.3 Fire Extinguishers');
    expect(pipe.transform('_2_F_4_AEROSOLS')).toEqual('2.F.4 Aerosols');
    expect(pipe.transform('_2_F_5_SOLVENTS')).toEqual('2.F.5 Solvents');
    expect(pipe.transform('_2_F_6_OTHER')).toEqual('2.F.6 Other');

    expect(pipe.transform('_2_G_1_ELECTRICAL_EQUIPMENT')).toEqual('2.G.1 Electrical Equipment');
    expect(pipe.transform('_2_G_2_SF6_AND_PFCS_FROM_OTHER_PRODUCT_USE')).toEqual(
      '2.G.2 SF6 and PFCs from Other Product Use',
    );
    expect(pipe.transform('_2_G_3_N2O_FROM_PRODUCT_USES')).toEqual('2.G.3 N2O from Product Uses');
    expect(pipe.transform('_2_G_4_OTHER')).toEqual('2.G.4 Other');

    expect(pipe.transform('_2_H_OTHER')).toEqual('2.H Other');
    expect(pipe.transform(undefined)).toEqual('');
  });
});
