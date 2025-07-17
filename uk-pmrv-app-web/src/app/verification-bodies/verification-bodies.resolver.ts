import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { VerificationBodiesService, VerificationBodyInfoResponseDTO } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class VerificationBodiesResolver {
  constructor(private readonly verificationBodiesService: VerificationBodiesService) {}

  resolve(): Observable<VerificationBodyInfoResponseDTO> {
    return this.verificationBodiesService.getVerificationBodies();
  }
}
