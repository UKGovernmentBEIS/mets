import { RequestActionStore } from '../../request-action.store';
import { VirRequestActionPayload } from '../../request-action.types';

export class VirActionStoreDelegate {
  constructor(private store: RequestActionStore) {}

  get payload(): VirRequestActionPayload | null {
    return this.store.getState().requestActionItem?.payload as VirRequestActionPayload;
  }

  get baseFileAttachmentDownloadUrl(): string {
    return `/aviation/actions/${this.store.getState().requestActionItem.id}/file-download/attachment`;
  }

  get baseFileDocumentDownloadUrl(): string {
    return `/aviation/actions/${this.store.getState().requestActionItem.id}/file-download/document`;
  }
}
