import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { OperatorDetailsFormProvider } from '@aviation/request-task/aer/ukets/tasks/operator-details';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSubtaskSummaryValues, getSummaryHeaderForTaskType } from '@aviation/request-task/util';
import { OperatorDetailsSummaryTemplateComponent } from '@aviation/shared/components/operator-details/operator-details-summary-template/operator-details-summary-template.component';
import { transformFiles } from '@aviation/shared/components/operator-details/utils/operator-details-summary.util';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { OperatorDetailsFlightIdentificationTypePipe } from '@aviation/shared/pipes/operator-details-flight-identification-type.pipe';
import { OperatorDetailsLegalStatusTypePipe } from '@aviation/shared/pipes/operator-details-legal-status-type.pipe';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationOperatorDetails, EmpOperatorDetails } from 'pmrv-api';

interface ViewModel {
  operatorDetails: EmpOperatorDetails & AviationOperatorDetails;
  certificationFiles: { fileName: string; downloadUrl: string }[];
  evidenceFiles: { fileName: string; downloadUrl: string }[];
  pageHeader: string;
}

@Component({
  selector: 'app-operator-details-summary-page',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    OperatorDetailsFlightIdentificationTypePipe,
    OperatorDetailsLegalStatusTypePipe,
    OperatorDetailsSummaryTemplateComponent,
  ],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.pageHeader }}</app-page-heading>

      <app-operator-details-summary-template
        *ngIf="vm.operatorDetails"
        [data]="vm.operatorDetails"
        [certificationFiles]="vm.certificationFiles"
        [evidenceFiles]="vm.evidenceFiles"></app-operator-details-summary-template>
    </ng-container>

    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export default class OperatorDetailsSummaryComponent {
  operatorDetails: EmpOperatorDetails;
  downloadUrl = `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/`;
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([this.store.pipe(requestTaskQuery.selectRequestTaskType)]).pipe(
    map(([type]) => {
      const operatorDetails = getSubtaskSummaryValues(this.form);

      return {
        operatorDetails: {
          ...operatorDetails,
          operatorName: operatorDetails?.operatorName?.operatorName,
          crcoCode: operatorDetails?.operatorName?.crcoCode,
        },
        certificationFiles: transformFiles(
          operatorDetails?.airOperatingCertificate?.certificateFiles ?? [],
          this.downloadUrl,
        ),
        evidenceFiles: transformFiles(operatorDetails?.organisationStructure?.evidenceFiles ?? [], this.downloadUrl),
        pageHeader: getSummaryHeaderForTaskType(type, 'operatorDetails'),
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: OperatorDetailsFormProvider,
    protected readonly store: RequestTaskStore,
    protected readonly destroy$: DestroySubject,
  ) {}
}
