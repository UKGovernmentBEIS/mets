import { TestBed } from '@angular/core/testing';

import { firstValueFrom, of } from 'rxjs';

import { SettingsService } from 'pmrv-api';

import { SettingsFeesPermissionService } from './settings-fees-permission.service';
import { SettingsVisibilityService } from './settings-visibility.service';

describe('SettingsVisibilityService', () => {
  let service: SettingsVisibilityService;
  let settingsService: Partial<jest.Mocked<SettingsService>>;
  let settingsFeesPermissionService: Partial<jest.Mocked<SettingsFeesPermissionService>>;

  function setup(sections: string[], canViewFees: boolean) {
    settingsService = { getAccessibleSections: jest.fn().mockReturnValue(of(sections)) };
    settingsFeesPermissionService = { canView: jest.fn().mockReturnValue(of(canViewFees)) };

    TestBed.configureTestingModule({
      providers: [
        { provide: SettingsService, useValue: settingsService },
        { provide: SettingsFeesPermissionService, useValue: settingsFeesPermissionService },
      ],
    });

    service = TestBed.inject(SettingsVisibilityService);
  }

  it('is false when there are no accessible sections', async () => {
    setup([], false);

    expect(await firstValueFrom(service.hasAnyAccessibleSection('INSTALLATION'))).toEqual(false);
  });

  it('is false when FEES is accessible but the user cannot view fees', async () => {
    setup(['FEES'], false);

    expect(await firstValueFrom(service.hasAnyAccessibleSection('INSTALLATION'))).toEqual(false);
    expect(settingsFeesPermissionService.canView).toHaveBeenCalled();
  });

  it('is true when FEES is accessible and the user can view fees', async () => {
    setup(['FEES'], true);

    expect(await firstValueFrom(service.hasAnyAccessibleSection('INSTALLATION'))).toEqual(true);
  });

  it('does not check fees permission when FEES is not an accessible section', async () => {
    setup(['EMISSION_FACTORS'], false);

    expect(await firstValueFrom(service.hasAnyAccessibleSection('INSTALLATION'))).toEqual(true);
    expect(settingsFeesPermissionService.canView).not.toHaveBeenCalled();
  });

  it('is true when GLOBAL_WARMING_POTENTIALS is accessible', async () => {
    setup(['GLOBAL_WARMING_POTENTIALS'], false);

    expect(await firstValueFrom(service.hasAnyAccessibleSection('AVIATION'))).toEqual(true);
  });

  it('requests the accessible sections for the given account type', async () => {
    setup(['FEES'], true);

    await firstValueFrom(service.hasAnyAccessibleSection('AVIATION'));

    expect(settingsService.getAccessibleSections).toHaveBeenCalledWith('AVIATION');
  });
});
