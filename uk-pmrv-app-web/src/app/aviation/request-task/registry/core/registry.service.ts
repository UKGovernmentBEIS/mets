import { Injectable, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { Observable } from 'rxjs';

import { CommonActionsStore } from '@actions/store/common-actions.store';

import { EmpIssuanceRegistryIntegrationRequestActionPayload, RequestActionDTO } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class RegistryActionService {
  constructor(private readonly store: CommonActionsStore) {}

  get payload$(): Observable<EmpIssuanceRegistryIntegrationRequestActionPayload> {
    return this.store.payload$;
  }

  get payload(): Signal<EmpIssuanceRegistryIntegrationRequestActionPayload> {
    return toSignal(this.payload$);
  }

  get requestAction$(): Observable<RequestActionDTO> {
    return this.store.requestAction$;
  }

  get requestAction(): Signal<RequestActionDTO> {
    return toSignal(this.requestAction$);
  }

  get requestActionType$(): Observable<RequestActionDTO['type']> {
    return this.store.requestActionType$;
  }

  get requestActionType(): Signal<RequestActionDTO['type']> {
    return toSignal(this.requestActionType$);
  }
}
