import { RequestActionStore } from '../../request-action.store';
import { EmpRequestActionPayload } from '../../request-action.types';

export class EmpActionStoreDelegate {
  constructor(private store: RequestActionStore) {}

  get payload(): EmpRequestActionPayload | null {
    return this.store.getState().requestActionItem?.payload as EmpRequestActionPayload;
  }

  get baseFileAttachmentDownloadUrl(): string {
    return `/aviation/actions/${this.store.getState().requestActionItem.id}/file-download/attachment`;
  }

  get baseFileDocumentDownloadUrl(): string {
    return `/aviation/actions/${this.store.getState().requestActionItem.id}/file-download/document`;
  }
}
