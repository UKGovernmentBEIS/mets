import { Injectable } from '@angular/core';

import { map, Observable } from 'rxjs';

import {
  AerApplicationAmendsSubmittedRequestActionPayload,
  AerApplicationCompletedRequestActionPayload,
  AerApplicationReturnedForAmendsRequestActionPayload,
  AerApplicationSubmittedRequestActionPayload,
  AerApplicationVerificationSubmittedRequestActionPayload,
  RequestActionDTO,
} from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';

@Injectable({ providedIn: 'root' })
export class AerService {
  constructor(private readonly store: CommonActionsStore) {}

  get requestAction$(): Observable<RequestActionDTO> {
    return this.store.requestAction$;
  }

  getPayload(): Observable<any> {
    return this.store.payload$.pipe(map((payload) => payload));
  }

  getDownloadUrlFiles(files: string[], isVerification = false): { downloadUrl: string; fileName: string }[] {
    const aerAttachments = isVerification ? this.verificationAttachments : this.attachments;

    const actionId = this.store.actionId;
    const url = `/actions/${actionId}/file-download/attachment/`;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: aerAttachments[id],
      })) ?? []
    );
  }

  private get attachments() {
    const payload = this.store.getValue().action.payload;
    switch (payload.payloadType) {
      case 'AER_APPLICATION_COMPLETED_PAYLOAD':
      case 'AER_APPLICATION_SUBMITTED_PAYLOAD':
      case 'AER_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD':
      case 'AER_APPLICATION_AMENDS_SUBMITTED_PAYLOAD':
        return (<
          | AerApplicationSubmittedRequestActionPayload
          | AerApplicationCompletedRequestActionPayload
          | AerApplicationVerificationSubmittedRequestActionPayload
          | AerApplicationAmendsSubmittedRequestActionPayload
        >payload).aerAttachments;
      case 'AER_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD':
        return (<AerApplicationReturnedForAmendsRequestActionPayload>payload).reviewAttachments;
      default:
        throw Error('Unhandled task type: ' + payload.payloadType);
    }
  }

  private get verificationAttachments() {
    return (this.store.getValue().action.payload as AerApplicationCompletedRequestActionPayload)
      ?.verificationAttachments;
  }
}
