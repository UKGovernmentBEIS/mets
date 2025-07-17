import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { UsersAuthoritiesInfoDTO, VerifierAuthoritiesService } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class VerifiersGuard {
  constructor(private readonly verifierAuthoritiesService: VerifierAuthoritiesService) {}

  resolve(): Observable<UsersAuthoritiesInfoDTO> {
    return this.verifierAuthoritiesService.getVerifierAuthorities();
  }
}
