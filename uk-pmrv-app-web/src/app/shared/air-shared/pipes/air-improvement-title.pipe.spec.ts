import {
  AirImprovement,
  AirImprovementCalculationCO2,
  AirImprovementCalculationPFC,
  AirImprovementFallback,
  AirImprovementMeasurement,
} from 'pmrv-api';

import { AirImprovementTitlePipe } from './air-improvement-title.pipe';

describe('AirImprovementTitlePipe', () => {
  let pipe: AirImprovementTitlePipe;

  beforeAll(async () => {
    pipe = new AirImprovementTitlePipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform AirImprovementCalculationCO2', () => {
    const airImprovement = {
      type: 'CALCULATION_CO2',
      categoryType: 'MAJOR',
      emissionSources: ['S1: Boiler'],
      sourceStreamReference: 'F1: Acetylene',
      parameter: 'Emission factor',
      tier: 'Tier 1',
    } as AirImprovementCalculationCO2;
    expect(pipe.transform(airImprovement, '1')).toEqual('Item 1: F1: Acetylene: major: emission factor');
  });

  it('should transform AirImprovementCalculationPFC', () => {
    const airImprovement = {
      type: 'CALCULATION_PFC',
      categoryType: 'MAJOR',
      emissionSources: ['S1: Boiler'],
      sourceStreamReference: 'F1: Acetylene',
      emissionPoints: ['EP1: West side chimney'],
      parameter: 'Emission factor',
      tier: 'Tier 1',
    } as AirImprovementCalculationPFC;
    expect(pipe.transform(airImprovement, '2')).toEqual('Item 2: F1: Acetylene: major: emission factor');
  });

  it('should transform AirImprovementMeasurement CO2', () => {
    const airImprovement = {
      type: 'MEASUREMENT_CO2',
      categoryType: 'MAJOR',
      emissionSources: ['S1: Boiler'],
      sourceStreamReferences: ['F1: Acetylene'],
      emissionPoint: 'EP1: West side chimney',
      parameter: 'Applied standard Parameter',
      tier: 'Tier 2',
    } as AirImprovementMeasurement;
    expect(pipe.transform(airImprovement, '3')).toEqual('Item 3: EP1: West side chimney: major');
  });

  it('should transform AirImprovementMeasurement N2O', () => {
    const airImprovement = {
      type: 'MEASUREMENT_N2O',
      categoryType: 'MAJOR',
      emissionSources: ['S1: Boiler'],
      sourceStreamReferences: ['F1: Acetylene'],
      emissionPoint: 'EP1: West side chimney',
      parameter: 'Applied standard Parameter',
      tier: 'Tier 2',
    } as AirImprovementMeasurement;
    expect(pipe.transform(airImprovement, '4')).toEqual('Item 4: EP1: West side chimney: major');
  });

  it('should transform AirImprovementFallback', () => {
    const airImprovement = {
      type: 'FALLBACK',
      categoryType: 'MAJOR',
      emissionSources: ['S1: Boiler'],
      sourceStreamReference: 'F1: Acetylene',
    } as AirImprovementFallback;
    expect(pipe.transform(airImprovement, '5')).toEqual('Item 5: F1: Acetylene: major');
  });

  it('should transform null AirImprovement', () => {
    const airImprovement = {
      type: 'RANDOM',
    } as unknown as AirImprovement;
    expect(pipe.transform(airImprovement, '6')).toEqual('');
  });
});
