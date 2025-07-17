import { Injectable } from '@angular/core';

import { map, Observable } from 'rxjs';

import { Store } from '@core/store/store';
import { AVIATION_REQUEST_TYPES } from '@shared/utils/request.utils';

import { initialState, RfiState } from './rfi.state';

@Injectable({ providedIn: 'root' })
export class RfiStore extends Store<RfiState> {
  constructor() {
    super(initialState);
  }

  setState(state: RfiState): void {
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
        fileName: this.getState().rfiAttachments[id],
      })) ?? []
    );
  }

  createBaseFileDownloadUrl() {
    return !this.getState().actionId
      ? `${AVIATION_REQUEST_TYPES.includes(this.getState().requestType) ? '/aviation' : ''}/rfi/${
          this.getState().requestTaskId
        }/file-download/`
      : `${AVIATION_REQUEST_TYPES.includes(this.getState().requestType) ? '/aviation' : ''}/rfi/action/${
          this.getState().actionId
        }/file-download/attachment/`;
  }
}
