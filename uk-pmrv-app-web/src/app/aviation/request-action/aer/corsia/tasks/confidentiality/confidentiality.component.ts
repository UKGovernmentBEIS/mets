import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { ConfidentialitySummaryTemplateComponent } from '@aviation/shared/components/aer/confidentiality-summary-template/confidentiality-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { AviationAerCorsiaConfidentiality, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  data: AviationAerCorsiaConfidentiality;
  totalEmissionsFiles: AttachedFile[];
  aggregatedStatePairDataFiles: AttachedFile[];
}

@Component({
  selector: 'app-confidentiality',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, ConfidentialitySummaryTemplateComponent],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-confidentiality-summary-template
        [confidentialityData]="vm.data"
        [totalEmissionsFiles]="vm.totalEmissionsFiles"
        [aggregatedStatePairDataFiles]="vm.aggregatedStatePairDataFiles"
      ></app-confidentiality-summary-template>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ConfidentialityComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectAer),
    this.store.pipe(aerCorsiaQuery.selectAerAttachments),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([aer, attachments, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['confidentiality'],
      data: aer.confidentiality,
      totalEmissionsFiles:
        aer.confidentiality.totalEmissionsDocuments?.map((uuid) => {
          const file = attachments[uuid];
          return {
            fileName: file,
            downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          };
        }) ?? [],
      aggregatedStatePairDataFiles:
        aer.confidentiality.aggregatedStatePairDataDocuments?.map((uuid) => {
          const file = attachments[uuid];
          return {
            fileName: file,
            downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          };
        }) ?? [],
    })),
  );
}
