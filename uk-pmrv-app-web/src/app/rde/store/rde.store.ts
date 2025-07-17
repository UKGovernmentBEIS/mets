import { Injectable } from '@angular/core';

import { map, Observable } from 'rxjs';

import { Store } from '@core/store/store';
import { AVIATION_REQUEST_TYPES } from '@shared/utils/request.utils';

import { initialState, RdeState } from './rde.state';

@Injectable({ providedIn: 'root' })
export class RdeStore extends Store<RdeState> {
  constructor() {
    super(initialState);
  }

  setState(state: RdeState): void {
    super.setState(state);
  }

  get isEditable$(): Observable<boolean> {
    return this.pipe(map((state) => state?.isEditable));
  }

  get isPaymentRequired(): boolean {
    return true;
  }

  get isAssignableAndCapable$(): Observable<boolean> {
    return this.pipe(map((state) => state?.userAssignCapable && state?.assignable));
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const url = this.createBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: this.getState().rdeAttachments[id],
      })) ?? []
    );
  }

  createBaseFileDownloadUrl() {
    return !this.getState().actionId
      ? `${AVIATION_REQUEST_TYPES.includes(this.getState().requestType) ? '/aviation' : ''}/rde/${
          this.getState().requestTaskId
        }/file-download/`
      : `${AVIATION_REQUEST_TYPES.includes(this.getState().requestType) ? '/aviation' : ''}/rde/action/${
          this.getState().actionId
        }/file-download/attachment/`;
  }
}
