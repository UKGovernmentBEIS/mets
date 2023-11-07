import { TestBed } from '@angular/core/testing';

import { SiteVisitsService } from '@shared/components/review-groups/opinion-statement-group/services/site-visits.service';

describe('SiteVisitsService', () => {
  let siteVisitsService: SiteVisitsService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [SiteVisitsService] });
    siteVisitsService = TestBed.inject(SiteVisitsService);
  });

  it('should be created', () => {
    expect(siteVisitsService).toBeTruthy();
  });

  it('should map correctly visitTypes to paths', () => {
    expect(siteVisitsService.mapVisitTypeToPath('IN_PERSON')).toEqual('in-person-visit');
    expect(siteVisitsService.mapVisitTypeToPath('VIRTUAL')).toEqual('virtual-visit');
    expect(siteVisitsService.mapVisitTypeToPath('NO_VISIT')).toEqual('no-visit');
  });

  it('should map correctly visitTypes and urlPaths to boolean', () => {
    expect(siteVisitsService.siteVisitTypeMatches('IN_PERSON', 'in-person-visit')).toBeTruthy();
    expect(siteVisitsService.siteVisitTypeMatches('IN_PERSON', 'random-endpoint')).toBeFalsy();

    expect(siteVisitsService.siteVisitTypeMatches('VIRTUAL', 'virtual-visit')).toBeTruthy();
    expect(siteVisitsService.siteVisitTypeMatches('VIRTUAL', 'random-endpoint')).toBeFalsy();

    expect(siteVisitsService.siteVisitTypeMatches('NO_VISIT', 'no-visit')).toBeTruthy();
    expect(siteVisitsService.siteVisitTypeMatches('NO_VISIT', 'random-endpoint')).toBeFalsy();

    expect(siteVisitsService.siteVisitTypeMatches(null, 'no-visit')).toBeFalsy();
  });

  it('should map correctly visitTypes to labels', () => {
    expect(siteVisitsService.mapVisitTypeToLabel('IN_PERSON')).toEqual('In person visit');
    expect(siteVisitsService.mapVisitTypeToLabel('VIRTUAL')).toEqual('Virtual visit');
    expect(siteVisitsService.mapVisitTypeToLabel('NO_VISIT')).toEqual('No visit');
  });
});
