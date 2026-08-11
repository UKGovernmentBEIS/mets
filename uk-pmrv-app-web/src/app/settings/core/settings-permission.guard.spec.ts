import { TestBed } from '@angular/core/testing';
import { Route, UrlSegment } from '@angular/router';

import { of, throwError } from 'rxjs';

import { SettingsService } from 'pmrv-api';

import { hasAccessibleSettings } from './settings-permission.guard';

describe('hasAccessibleSettings', () => {
  let settingsService: Partial<jest.Mocked<SettingsService>>;

  beforeEach(() => {
    settingsService = { getAccessibleSections: jest.fn().mockReturnValue(of(['FEES'])) };

    TestBed.configureTestingModule({
      providers: [{ provide: SettingsService, useValue: settingsService }],
    });
  });

  it('requests the accessible sections for the given account type and allows the match when non-empty', (done) => {
    TestBed.runInInjectionContext(() => {
      const result = hasAccessibleSettings('INSTALLATION')({} as Route, [] as UrlSegment[]);

      expect(settingsService.getAccessibleSections).toHaveBeenCalledWith('INSTALLATION');
      (result as any).subscribe((value: boolean) => {
        expect(value).toEqual(true);
        done();
      });
    });
  });

  it('blocks the match when the user has no accessible sections', (done) => {
    settingsService.getAccessibleSections.mockReturnValue(of([]));

    TestBed.runInInjectionContext(() => {
      const result = hasAccessibleSettings('AVIATION')({} as Route, [] as UrlSegment[]);

      expect(settingsService.getAccessibleSections).toHaveBeenCalledWith('AVIATION');
      (result as any).subscribe((value: boolean) => {
        expect(value).toEqual(false);
        done();
      });
    });
  });

  it('blocks the match when the backend call fails', (done) => {
    settingsService.getAccessibleSections.mockReturnValue(throwError(() => new Error('failed')));

    TestBed.runInInjectionContext(() => {
      const result = hasAccessibleSettings('INSTALLATION')({} as Route, [] as UrlSegment[]);

      (result as any).subscribe((value: boolean) => {
        expect(value).toEqual(false);
        done();
      });
    });
  });
});
