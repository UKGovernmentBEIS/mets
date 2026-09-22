import { inject, Injectable } from '@angular/core';

import { map, Observable, shareReplay } from 'rxjs';

import { RegulatorAuthoritiesService } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class SettingsFeesPermissionService {
  private readonly regulatorAuthoritiesService = inject(RegulatorAuthoritiesService);

  private readonly permissionLevel$ = this.regulatorAuthoritiesService.getCurrentRegulatorUserPermissionsByCa().pipe(
    map((dto) => dto.permissions?.['SETTINGS_PAGE_FEES'] ?? 'NONE'),
    shareReplay(1),
  );

  canView(): Observable<boolean> {
    return this.permissionLevel$.pipe(map((level) => level !== 'NONE'));
  }

  canExecute(): Observable<boolean> {
    return this.permissionLevel$.pipe(map((level) => level === 'EXECUTE'));
  }
}
