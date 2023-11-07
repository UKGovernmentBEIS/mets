import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { OperatorDetailsSummaryTemplateComponent } from '@aviation/shared/components/operator-details/operator-details-summary-template/operator-details-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { AviationCorsiaOperatorDetails, LimitedCompanyOrganisation, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  data: AviationCorsiaOperatorDetails;
  certificationFiles: AttachedFile[];
  evidenceFiles: AttachedFile[];
}

@Component({
  selector: 'app-operator-details',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, OperatorDetailsSummaryTemplateComponent],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-operator-details-summary-template
        [data]="vm.data"
        [certificationFiles]="vm.certificationFiles"
        [evidenceFiles]="vm.evidenceFiles"
        [isCorsia]="true"
      ></app-operator-details-summary-template>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OperatorDetailsComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectAer),
    this.store.pipe(aerCorsiaQuery.selectAerAttachments),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([aer, attachments, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['operatorDetails'],
      data: aer.operatorDetails,
      certificationFiles:
        aer.operatorDetails?.airOperatingCertificate?.certificateFiles?.map((uuid) => {
          const file = attachments[uuid];
          return {
            fileName: file,
            downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          };
        }) ?? [],
      evidenceFiles:
        (aer.operatorDetails?.organisationStructure as LimitedCompanyOrganisation)?.evidenceFiles?.map((uuid) => {
          const file = attachments[uuid];
          return {
            fileName: file,
            downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          };
        }) ?? [],
    })),
  );
}
