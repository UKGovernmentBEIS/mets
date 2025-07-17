import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { RegulatorAuthoritiesService, RegulatorUsersAuthoritiesInfoDTO } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class RegulatorsGuard {
  constructor(private readonly regulatorAuthoritiesService: RegulatorAuthoritiesService) {}

  resolve(): Observable<RegulatorUsersAuthoritiesInfoDTO> {
    return this.regulatorAuthoritiesService.getCaRegulators();
  }
}
