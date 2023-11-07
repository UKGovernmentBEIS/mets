import { TestBed } from '@angular/core/testing';

import { AirImprovementResponseService } from './air-improvement-response.service';

describe('AirImprovementResponseService', () => {
  let airImprovementResponseService: AirImprovementResponseService;

  beforeAll(() => {
    TestBed.configureTestingModule({ providers: [AirImprovementResponseService] });
    airImprovementResponseService = TestBed.inject(AirImprovementResponseService);
  });

  it('should be created', () => {
    expect(airImprovementResponseService).toBeTruthy();
  });

  it('should map correctly responseTypes to paths', () => {
    expect(airImprovementResponseService.mapResponseTypeToPath('YES')).toEqual('improvement-positive');
    expect(airImprovementResponseService.mapResponseTypeToPath('NO')).toEqual('improvement-negative');
    expect(airImprovementResponseService.mapResponseTypeToPath('ALREADY_MADE')).toEqual('improvement-existing');
  });

  it('should map correctly visitTypes and urlPaths to boolean', () => {
    expect(airImprovementResponseService.typeMatches('YES', 'improvement-positive')).toBeTruthy();
    expect(airImprovementResponseService.typeMatches('YES', 'random-endpoint')).toBeFalsy();

    expect(airImprovementResponseService.typeMatches('NO', 'improvement-negative')).toBeTruthy();
    expect(airImprovementResponseService.typeMatches('NO', 'random-endpoint')).toBeFalsy();

    expect(airImprovementResponseService.typeMatches('ALREADY_MADE', 'improvement-existing')).toBeTruthy();
    expect(airImprovementResponseService.typeMatches('ALREADY_MADE', 'random-endpoint')).toBeFalsy();

    expect(airImprovementResponseService.typeMatches(null, 'improvement-positive')).toBeFalsy();
  });
});
