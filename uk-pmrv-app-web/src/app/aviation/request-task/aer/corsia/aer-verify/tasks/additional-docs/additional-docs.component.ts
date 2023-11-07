import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { EmpAdditionalDocuments } from 'pmrv-api';

interface ViewModel {
  heading: string;
  data: EmpAdditionalDocuments;
  files: AttachedFile[];
}

@Component({
  selector: 'app-additional-docs',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-documents-summary-template [data]="vm.data" [files]="vm.files"></app-documents-summary-template>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalDocsComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerVerifyCorsiaQuery.selectAer),
    this.store.pipe(aerVerifyCorsiaQuery.selectAerAttachments),
  ]).pipe(
    map(([aer, aerAttachments]) => {
      return {
        heading: aerHeaderTaskMap['additionalDocuments'],
        data: aer.additionalDocuments,
        files:
          aer.additionalDocuments.documents?.map((id) => ({
            downloadUrl: `${
              (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate).baseFileAttachmentDownloadUrl
            }/${id}`,
            fileName: aerAttachments[id],
          })) ?? [],
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
