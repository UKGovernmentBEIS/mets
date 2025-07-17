import {
  CalculationRefineryProductsRelevantCWTFunctionsPipe,
  CWTFunctionKey,
} from './calculation-refinery-products-relevant-cwt-functions.pipe';

describe('CalculationRefineryProductsRelevantCWTFunctionsPipe', () => {
  let pipe: CalculationRefineryProductsRelevantCWTFunctionsPipe;

  beforeEach(() => {
    pipe = new CalculationRefineryProductsRelevantCWTFunctionsPipe();
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform an array of event keys to an array of EventData objects', () => {
    const funcs = ['VACUUM_DISTILLATION', 'VISBREAKING'] as CWTFunctionKey[];
    const result = pipe.transform(funcs);
    expect(result).toEqual([
      {
        key: 'VACUUM_DISTILLATION',
        description: 'Vacuum Distillation',
        basis: 'F',
        cwtFactor: 0.85,
      },
      {
        key: 'VISBREAKING',
        description: 'Visbreaking',
        basis: 'F',
        cwtFactor: 1.4,
      },
    ]);
  });

  it('should handle empty arrays by returning an empty array', () => {
    const funcs: CWTFunctionKey[] = [];
    const result = pipe.transform(funcs);
    expect(result).toEqual([]);
  });

  it('should filter out invalid event keys', () => {
    const funcs = ['VACUUM_DISTILLATION', 'INVALID_KEY'] as any;
    const result = pipe.transform(funcs);
    expect(result).toEqual([
      {
        key: 'VACUUM_DISTILLATION',
        description: 'Vacuum Distillation',
        basis: 'F',
        cwtFactor: 0.85,
      },
    ]);
  });

  it('should handle null and undefined inputs gracefully', () => {
    expect(pipe.transform(null as any)).toEqual([]);
    expect(pipe.transform(undefined as any)).toEqual([]);
  });

  it('should be case-sensitive and only match exact keys', () => {
    const funcs = ['vacuum_distillation', 'VISBREAKING'] as any;
    const result = pipe.transform(funcs);
    expect(result).toEqual([
      {
        key: 'VISBREAKING',
        description: 'Visbreaking',
        basis: 'F',
        cwtFactor: 1.4,
      },
    ]);
  });

  it('should transform all valid event keys correctly', () => {
    const allCWTFunctionKeys: CWTFunctionKey[] = [
      'ATMOSPHERIC_CRUDE_DISTILLATION',
      'VACUUM_DISTILLATION',
      'SOLVENT_DEASPHALTING',
      'VISBREAKING',
      'THERMAL_CRACKING',
      'DELAYED_COKING',
      'FLUID_COKING',
      'FLEXICOKING',
      'COKE_CALCINING',
      'FLUID_CATALYTIC_CRACKING',
      'OTHER_CATALYTIC_CRACKING',
      'DISTILLATE_GASOIL_HYDROCRACKING',
      'RESIDUAL_HYDROCRACKING',
      'NAPHTHA_GASOLINE_HYDROTREATING',
      'KEROSENE_DIESEL_HYDROTREATING',
      'RESIDUAL_HYDROTREATING',
      'VGO_HYDROTREATING',
      'HYDROGEN_PRODUCTION',
      'CATALYTIC_REFORMING',
      'ALKYLATION',
      'C4_ISOMERISATION',
      'C5_C6_ISOMERISATION',
      'OXYGENATE_PRODUCTION',
      'PROPYLENE_PRODUCTION',
      'ASPHALT_MANUFACTURE',
      'POLYMER_MODIFIED_ASPHALT_BLENDING',
      'SULPHUR_RECOVERY',
      'AROMATIC_SOLVENT_EXTRACTION',
      'HYDRODEALKYLATION',
      'TDP_TDA',
      'CYCLOHEXANE_PRODUCTION',
      'XYLENE_ISOMERISATION',
      'PARAXYLENE_PRODUCTION',
      'METAXYLENE_PRODUCTION',
      'PHTHALIC_ANHYDRIDE_PRODUCTION',
      'MALEIC_ANHYDRIDE_PRODUCTION',
      'ETHYLBENZENE_PRODUCTION',
      'CUMENE_PRODUCTION',
      'PHENOL_PRODUCTION',
      'LUBE_SOLVENT_EXTRACTION',
      'LUBE_SOLVENT_DEWAXING',
      'CATALYTIC_WAX_ISOMERISATION',
      'LUBE_HYDROCRACKER',
      'WAX_DEOILING',
      'LUBE_WAX_HYDROTREATING',
      'SOLVENT_HYDROTREATING',
      'SOLVENT_FRACTIONATION',
      'MOL_SIEVE_C10_PLUS_PARAFFINS',
      'PARTIAL_OXIDATION_RESIDUAL_FEEDS_POX_FUEL',
      'PARTIAL_OXIDATION_RESIDUAL_FEEDS_POX_HYDROGEN_METHANOL',
      'METHANOL_FROM_SYNGAS',
      'AIR_SEPARATION',
      'FRACTIONATION_PURCHASED_NGL',
      'FLUE_GAS_TREATMENT',
      'TREATMENT_COMPRESSION_FUEL_GAS_SALES',
      'SEAWATER_DESALINATION',
    ];

    const result = pipe.transform(allCWTFunctionKeys);

    const expectedResults = allCWTFunctionKeys.map((func) => ({
      key: func,
      description: pipe['calculationRefineryProductsRelevantCWTFunctions'][func].description,
      basis: pipe['calculationRefineryProductsRelevantCWTFunctions'][func].basis,
      cwtFactor: pipe['calculationRefineryProductsRelevantCWTFunctions'][func].cwtFactor,
    }));

    expect(result).toEqual(expectedResults);
  });
});
