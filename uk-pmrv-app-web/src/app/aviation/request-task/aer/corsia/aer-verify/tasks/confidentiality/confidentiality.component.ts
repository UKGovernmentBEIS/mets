import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { ConfidentialitySummaryTemplateComponent } from '@aviation/shared/components/aer/confidentiality-summary-template/confidentiality-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { AviationAerCorsiaConfidentiality } from 'pmrv-api';

interface ViewModel {
  heading: string;
  data: AviationAerCorsiaConfidentiality;
  totalEmissionsFiles: AttachedFile[];
  aggregatedStatePairDataFiles: AttachedFile[];
}

@Component({
  selector: 'app-confidentiality',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, ConfidentialitySummaryTemplateComponent],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-confidentiality-summary-template
        [confidentialityData]="vm.data"
        [totalEmissionsFiles]="vm.totalEmissionsFiles"
        [aggregatedStatePairDataFiles]="vm.aggregatedStatePairDataFiles"
      ></app-confidentiality-summary-template>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfidentialityComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerVerifyCorsiaQuery.selectAer),
    this.store.pipe(aerVerifyCorsiaQuery.selectAerAttachments),
  ]).pipe(
    map(([aer, aerAttachments]) => {
      return {
        heading: aerHeaderTaskMap['confidentiality'],
        data: aer.confidentiality,
        totalEmissionsFiles:
          aer.confidentiality.totalEmissionsDocuments?.map((id) => ({
            downloadUrl: `${
              (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate).baseFileAttachmentDownloadUrl
            }/${id}`,
            fileName: aerAttachments[id],
          })) ?? [],
        aggregatedStatePairDataFiles:
          aer.confidentiality.aggregatedStatePairDataDocuments?.map((id) => ({
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
