import { RequestActionStore } from '../../request-action.store';
import { AerCorsiaRequestActionPayload } from '../../request-action.types';

export class AerCorsiaActionStoreDelegate {
  constructor(private store: RequestActionStore) {}

  get payload(): AerCorsiaRequestActionPayload | null {
    return this.store.getState().requestActionItem?.payload as AerCorsiaRequestActionPayload;
  }

  get baseFileAttachmentDownloadUrl(): string {
    return `/aviation/actions/${this.store.getState().requestActionItem.id}/file-download/attachment`;
  }

  get baseFileDocumentDownloadUrl(): string {
    return `/aviation/actions/${this.store.getState().requestActionItem.id}/file-download/document`;
  }
}
