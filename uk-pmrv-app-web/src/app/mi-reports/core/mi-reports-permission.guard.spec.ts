import { TestBed } from '@angular/core/testing';
import { Route, UrlSegment } from '@angular/router';

import { of } from 'rxjs';

import { MiReportsUserDefinedService } from 'pmrv-api';

import { canManageCustomReports } from './mi-reports-permission.guard';

describe('canManageCustomReports', () => {
  let miReportsUserDefinedService: Partial<jest.Mocked<MiReportsUserDefinedService>>;

  beforeEach(() => {
    miReportsUserDefinedService = { hasManageCustomReportsAccess: jest.fn().mockReturnValue(of(true)) };

    TestBed.configureTestingModule({
      providers: [{ provide: MiReportsUserDefinedService, useValue: miReportsUserDefinedService }],
    });
  });

  it('should check whether the current user can manage custom reports and allow the match when true', (done) => {
    TestBed.runInInjectionContext(() => {
      const result = canManageCustomReports()({} as Route, [] as UrlSegment[]);

      expect(miReportsUserDefinedService.hasManageCustomReportsAccess).toHaveBeenCalled();
      (result as any).subscribe((value: boolean) => {
        expect(value).toEqual(true);
        done();
      });
    });
  });

  it('should block the match when the user lacks execute permission', (done) => {
    miReportsUserDefinedService.hasManageCustomReportsAccess.mockReturnValue(of(false));

    TestBed.runInInjectionContext(() => {
      const result = canManageCustomReports()({} as Route, [] as UrlSegment[]);

      (result as any).subscribe((value: boolean) => {
        expect(value).toEqual(false);
        done();
      });
    });
  });
});
