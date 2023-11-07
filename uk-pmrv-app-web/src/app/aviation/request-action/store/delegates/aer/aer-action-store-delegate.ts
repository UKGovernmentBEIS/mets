import { RequestActionStore } from '../../request-action.store';
import { AerRequestActionPayload } from '../../request-action.types';

export class AerActionStoreDelegate {
  constructor(private store: RequestActionStore) {}

  get payload(): AerRequestActionPayload | null {
    return this.store.getState().requestActionItem?.payload as AerRequestActionPayload;
  }

  get baseFileAttachmentDownloadUrl(): string {
    return `/aviation/actions/${this.store.getState().requestActionItem.id}/file-download/attachment`;
  }

  get baseFileDocumentDownloadUrl(): string {
    return `/aviation/actions/${this.store.getState().requestActionItem.id}/file-download/document`;
  }
}
