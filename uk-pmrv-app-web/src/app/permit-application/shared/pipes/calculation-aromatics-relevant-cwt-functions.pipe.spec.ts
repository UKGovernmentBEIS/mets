import { CalculationAromaticsRelevantCWTFunctionsPipe } from './calculation-aromatics-relevant-cwt-functions.pipe';
import { CWTFunctionData, CWTFunctionKey } from './calculation-aromatics-relevant-cwt-functions.pipe';

describe('CalculationAromaticsRelevantCWTFunctionsPipe', () => {
  let pipe: CalculationAromaticsRelevantCWTFunctionsPipe;

  beforeEach(() => {
    pipe = new CalculationAromaticsRelevantCWTFunctionsPipe();
  });

  it('should return an empty array when input is null', () => {
    expect(pipe.transform(null)).toEqual([]);
  });

  it('should return an empty array when input is undefined', () => {
    expect(pipe.transform(undefined)).toEqual([]);
  });

  it('should return an empty array when input is an empty array', () => {
    expect(pipe.transform([])).toEqual([]);
  });

  it('should transform valid CWTFunctionKeys correctly', () => {
    const funcs: CWTFunctionKey[] = ['NAPHTHA_GASOLINE_HYDROTREATER', 'AROMATIC_SOLVENT_EXTRACTION', 'TDP_TDA'];

    const expectedResult: CWTFunctionData[] = [
      {
        key: 'NAPHTHA_GASOLINE_HYDROTREATER',
        description: 'Naphtha/Gasoline Hydrotreater',
        basis: 'F',
        cwtFactor: 1.1,
      },
      {
        key: 'AROMATIC_SOLVENT_EXTRACTION',
        description: 'Aromatic Solvent Extraction',
        basis: 'F',
        cwtFactor: 5.25,
      },
      {
        key: 'TDP_TDA',
        description: 'TDP/ TDA',
        basis: 'F',
        cwtFactor: 1.85,
      },
    ];

    expect(pipe.transform(funcs)).toEqual(expectedResult);
  });

  it('should ignore invalid CWTFunctionKeys', () => {
    const funcs: CWTFunctionKey[] = ['NAPHTHA_GASOLINE_HYDROTREATER', 'INVALID_KEY' as CWTFunctionKey];

    const expectedResult: CWTFunctionData[] = [
      {
        key: 'NAPHTHA_GASOLINE_HYDROTREATER',
        description: 'Naphtha/Gasoline Hydrotreater',
        basis: 'F',
        cwtFactor: 1.1,
      },
    ];

    expect(pipe.transform(funcs)).toEqual(expectedResult);
  });

  it('should return an empty array when all inputs are invalid', () => {
    const funcs: CWTFunctionKey[] = ['INVALID_KEY_1', 'INVALID_KEY_2'] as any;
    expect(pipe.transform(funcs)).toEqual([]);
  });

  it('should handle a mix of valid and invalid CWTFunctionKeys', () => {
    const funcs: any = ['CYCLOHEXANE_PRODUCTION', 'INVALID_KEY', 'PARAXYLENE_PRODUCTION'];

    const expectedResult: CWTFunctionData[] = [
      {
        key: 'CYCLOHEXANE_PRODUCTION',
        description: 'Cyclohexane production',
        basis: 'P',
        cwtFactor: 3.0,
      },
      {
        key: 'PARAXYLENE_PRODUCTION',
        description: 'Paraxylene production',
        basis: 'P',
        cwtFactor: 6.4,
      },
    ];

    expect(pipe.transform(funcs)).toEqual(expectedResult);
  });

  it('should correctly transform all valid CWTFunctionKeys', () => {
    const funcs: CWTFunctionKey[] = [
      'NAPHTHA_GASOLINE_HYDROTREATER',
      'AROMATIC_SOLVENT_EXTRACTION',
      'TDP_TDA',
      'HYDRODEALKYLATION',
      'XYLENE_ISOMERISATION',
      'PARAXYLENE_PRODUCTION',
      'CYCLOHEXANE_PRODUCTION',
      'CUMENE_PRODUCTION',
    ];

    const expectedResult: CWTFunctionData[] = [
      {
        key: 'NAPHTHA_GASOLINE_HYDROTREATER',
        description: 'Naphtha/Gasoline Hydrotreater',
        basis: 'F',
        cwtFactor: 1.1,
      },
      {
        key: 'AROMATIC_SOLVENT_EXTRACTION',
        description: 'Aromatic Solvent Extraction',
        basis: 'F',
        cwtFactor: 5.25,
      },
      {
        key: 'TDP_TDA',
        description: 'TDP/ TDA',
        basis: 'F',
        cwtFactor: 1.85,
      },
      {
        key: 'HYDRODEALKYLATION',
        description: 'Hydrodealkylation',
        basis: 'F',
        cwtFactor: 2.45,
      },
      {
        key: 'XYLENE_ISOMERISATION',
        description: 'Xylene Isomerisation',
        basis: 'F',
        cwtFactor: 1.85,
      },
      {
        key: 'PARAXYLENE_PRODUCTION',
        description: 'Paraxylene production',
        basis: 'P',
        cwtFactor: 6.4,
      },
      {
        key: 'CYCLOHEXANE_PRODUCTION',
        description: 'Cyclohexane production',
        basis: 'P',
        cwtFactor: 3.0,
      },
      {
        key: 'CUMENE_PRODUCTION',
        description: 'Cumene production',
        basis: 'P',
        cwtFactor: 5.0,
      },
    ];

    expect(pipe.transform(funcs)).toEqual(expectedResult);
  });
});
