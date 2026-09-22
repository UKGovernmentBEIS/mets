import { inject, Injectable } from '@angular/core';

import { combineLatest, map, Observable, of, switchMap } from 'rxjs';

import { AccountType } from '@core/store';

import { SettingsService } from 'pmrv-api';

import { SettingsFeesPermissionService } from './settings-fees-permission.service';

@Injectable({ providedIn: 'root' })
export class SettingsVisibilityService {
  private readonly settingsService = inject(SettingsService);
  private readonly settingsFeesPermissionService = inject(SettingsFeesPermissionService);

  hasAnyAccessibleSection(accountType: AccountType): Observable<boolean> {
    return this.settingsService.getAccessibleSections(accountType).pipe(
      switchMap((sections) =>
        combineLatest([
          sections.includes('FEES') ? this.settingsFeesPermissionService.canView() : of(false),
          of(sections.includes('EMISSION_FACTORS')),
          of(sections.includes('GLOBAL_WARMING_POTENTIALS')),
        ]),
      ),
      map((flags) => flags.some(Boolean)),
    );
  }
}
