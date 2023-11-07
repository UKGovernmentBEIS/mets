import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCommonQuery } from '@aviation/request-action/aer/shared/aer-common.selector';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { ReportingObligationSummaryTemplateComponent } from '@aviation/shared/components/aer/reporting-obligation-summary-template/reporting-obligation-summary-template.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerReportingObligationDetails, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  reportingData: { reportingRequired: boolean; reportingObligationDetails?: AviationAerReportingObligationDetails };
  files: { downloadUrl: string; fileName: string }[];
  year: number;
}

@Component({
  selector: 'app-reporting-obligation',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, ReportingObligationSummaryTemplateComponent],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-reporting-obligation-summary-template
        [reportingData]="vm.reportingData"
        [year]="vm.year"
        [files]="vm.files"
      ></app-reporting-obligation-summary-template>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReportingObligationComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCommonQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: 'Reporting status',
      reportingData: {
        reportingRequired: payload.reportingRequired,
        reportingObligationDetails: payload.reportingObligationDetails,
      },
      files:
        payload.reportingObligationDetails?.supportingDocuments?.map((uuid) => {
          const file = payload.aerAttachments[uuid];
          return {
            fileName: file,
            downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          };
        }) ?? [],
      year: payload.reportingYear,
    })),
  );
}
