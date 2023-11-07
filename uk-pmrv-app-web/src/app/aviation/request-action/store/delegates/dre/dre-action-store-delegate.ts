import { RequestActionStore } from '../../request-action.store';

export class DreActionStoreDelegate {
  constructor(private store: RequestActionStore) {}

  get baseFileAttachmentDownloadUrl(): string {
    return `/aviation/actions/${this.store.getState().requestActionItem.id}/file-download/attachment`;
  }

  get baseFileDocumentDownloadUrl(): string {
    return `/aviation/actions/${this.store.getState().requestActionItem.id}/file-download/document`;
  }
}
