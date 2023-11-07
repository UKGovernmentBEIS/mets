import { Injectable } from '@angular/core';

import { Observable, of } from 'rxjs';

import { LegalEntitiesService } from 'pmrv-api';

@Injectable()
export class LegalEntitiesServiceStub implements Partial<LegalEntitiesService> {
  isExistingLegalEntityName(name: string): Observable<boolean>;
  isExistingLegalEntityName(
    name: string,
    observe: 'response',
    reportProgress?: boolean,
    options?: {
      httpHeaderAccept?: '*/*';
    },
  ): never;
  isExistingLegalEntityName(
    name: string,
    observe: 'events',
    reportProgress?: boolean,
    options?: {
      httpHeaderAccept?: '*/*';
    },
  ): never;
  isExistingLegalEntityName(
    name: string,
    observe: 'body',
    reportProgress?: boolean,
    options?: {
      httpHeaderAccept?: '*/*';
    },
  ): never;
  isExistingLegalEntityName(name: string): Observable<boolean> {
    if (name === 'Mock Entity') {
      return of(true);
    } else {
      return of(false);
    }
  }
}
