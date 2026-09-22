import { TestBed } from '@angular/core/testing';

import { firstValueFrom, of } from 'rxjs';

import { AuthorityManagePermissionDTO, RegulatorAuthoritiesService } from 'pmrv-api';

import { SettingsFeesPermissionService } from './settings-fees-permission.service';

describe('SettingsFeesPermissionService', () => {
  let service: SettingsFeesPermissionService;
  let regulatorAuthoritiesService: Partial<jest.Mocked<RegulatorAuthoritiesService>>;

  function setup(permissions: AuthorityManagePermissionDTO) {
    regulatorAuthoritiesService = {
      getCurrentRegulatorUserPermissionsByCa: jest.fn().mockReturnValue(of(permissions)),
    };

    TestBed.configureTestingModule({
      providers: [{ provide: RegulatorAuthoritiesService, useValue: regulatorAuthoritiesService }],
    });

    service = TestBed.inject(SettingsFeesPermissionService);
  }

  it.each([
    ['NONE', false, false],
    ['VIEW_ONLY', true, false],
    ['EXECUTE', true, true],
  ] as const)('for level %s, canView() is %s and canExecute() is %s', async (level, canView, canExecute) => {
    setup({ permissions: { SETTINGS_PAGE_FEES: level } });

    expect(await firstValueFrom(service.canView())).toEqual(canView);
    expect(await firstValueFrom(service.canExecute())).toEqual(canExecute);
  });

  it('treats a missing SETTINGS_PAGE_FEES key as NONE', async () => {
    setup({ permissions: {} });

    expect(await firstValueFrom(service.canView())).toEqual(false);
    expect(await firstValueFrom(service.canExecute())).toEqual(false);
  });
});
