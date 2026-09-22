import { TestBed } from '@angular/core/testing';
import { Route, UrlSegment } from '@angular/router';

import { of, throwError } from 'rxjs';

import { canExecuteSettingsFees, canViewSettingsFees } from './settings-fees-permission.guard';
import { SettingsFeesPermissionService } from './settings-fees-permission.service';

describe('settings-fees-permission guards', () => {
  let settingsFeesPermissionService: Partial<jest.Mocked<SettingsFeesPermissionService>>;

  beforeEach(() => {
    settingsFeesPermissionService = {
      canView: jest.fn().mockReturnValue(of(true)),
      canExecute: jest.fn().mockReturnValue(of(true)),
    };

    TestBed.configureTestingModule({
      providers: [{ provide: SettingsFeesPermissionService, useValue: settingsFeesPermissionService }],
    });
  });

  describe('canViewSettingsFees', () => {
    it('allows the match when the user can view fees', (done) => {
      TestBed.runInInjectionContext(() => {
        const result = canViewSettingsFees()({} as Route, [] as UrlSegment[]);

        (result as any).subscribe((value: boolean) => {
          expect(value).toEqual(true);
          done();
        });
      });
    });

    it('blocks the match when the user cannot view fees', (done) => {
      settingsFeesPermissionService.canView.mockReturnValue(of(false));

      TestBed.runInInjectionContext(() => {
        const result = canViewSettingsFees()({} as Route, [] as UrlSegment[]);

        (result as any).subscribe((value: boolean) => {
          expect(value).toEqual(false);
          done();
        });
      });
    });

    it('blocks the match when the backend call fails', (done) => {
      settingsFeesPermissionService.canView.mockReturnValue(throwError(() => new Error('failed')));

      TestBed.runInInjectionContext(() => {
        const result = canViewSettingsFees()({} as Route, [] as UrlSegment[]);

        (result as any).subscribe((value: boolean) => {
          expect(value).toEqual(false);
          done();
        });
      });
    });
  });

  describe('canExecuteSettingsFees', () => {
    it('allows the match when the user can execute fee actions', (done) => {
      TestBed.runInInjectionContext(() => {
        const result = canExecuteSettingsFees()({} as Route, [] as UrlSegment[]);

        (result as any).subscribe((value: boolean) => {
          expect(value).toEqual(true);
          done();
        });
      });
    });

    it('blocks the match when the user cannot execute fee actions', (done) => {
      settingsFeesPermissionService.canExecute.mockReturnValue(of(false));

      TestBed.runInInjectionContext(() => {
        const result = canExecuteSettingsFees()({} as Route, [] as UrlSegment[]);

        (result as any).subscribe((value: boolean) => {
          expect(value).toEqual(false);
          done();
        });
      });
    });

    it('blocks the match when the backend call fails', (done) => {
      settingsFeesPermissionService.canExecute.mockReturnValue(throwError(() => new Error('failed')));

      TestBed.runInInjectionContext(() => {
        const result = canExecuteSettingsFees()({} as Route, [] as UrlSegment[]);

        (result as any).subscribe((value: boolean) => {
          expect(value).toEqual(false);
          done();
        });
      });
    });
  });
});
