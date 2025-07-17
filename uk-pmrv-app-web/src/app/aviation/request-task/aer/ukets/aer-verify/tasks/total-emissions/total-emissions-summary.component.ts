import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { RouterLink, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { TotalEmissionsSchemeYearHeaderComponent } from '@aviation/request-task/aer/ukets/tasks/total-emissions/shared/total-emissions-scheme-year-header';
import { TotalEmissionsStandardFuelsTableComponent } from '@aviation/request-task/aer/ukets/tasks/total-emissions/table/total-emissions-standard-fuels-table';
import { TotalEmissionsFormProvider } from '@aviation/request-task/aer/ukets/tasks/total-emissions/total-emissions-form.provider';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType } from '@aviation/request-task/util';
import { TotalEmissionsSummaryTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-summary-template/total-emissions-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerTotalEmissionsConfidentiality } from 'pmrv-api';

interface ViewModel {
  data: AviationAerTotalEmissionsConfidentiality;
  pageHeader: string;
}

@Component({
  selector: 'app-total-emissions-summary',
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.pageHeader }}</app-page-heading>

      <app-total-emissions-scheme-year-header></app-total-emissions-scheme-year-header>

      <ng-container *ngIf="vm.data">
        <h2 class="govuk-heading-m govuk-!-width-two-thirds">Confidentiality</h2>

        <app-total-emissions-summary-template [data]="vm.data"></app-total-emissions-summary-template>
      </ng-container>
    </ng-container>

    <app-return-to-link></app-return-to-link>
  `,
  imports: [
    SharedModule,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    RouterLink,
    TotalEmissionsStandardFuelsTableComponent,
    TotalEmissionsSchemeYearHeaderComponent,
    TotalEmissionsSummaryTemplateComponent,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class TotalEmissionsSummaryComponent {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerQuery.selectAerMonitoringPlanVersions),
  ]).pipe(
    map(([type, planVersions]) => {
      return {
        planVersions,
        data: this.form.valid ? this.form.value : null,
        pageHeader: getSummaryHeaderForTaskType(type, 'aviationAerTotalEmissionsConfidentiality'),
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: TotalEmissionsFormProvider,
    private store: RequestTaskStore,
  ) {}
}
