import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerMonitoringApproachFormProvider } from '@aviation/request-task/aer/ukets/tasks/monitoring-approach';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType } from '@aviation/request-task/util';
import { MonitoringApproachSummaryTemplateComponent } from '@aviation/shared/components/aer/monitoring-approach-summary-template';
import { EmissionSmallEmittersSupportFacilityFormValues } from '@aviation/shared/components/aer/monitoring-approach-summary-template/monitoring-approach.interfaces';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

interface ViewModel {
  data: EmissionSmallEmittersSupportFacilityFormValues | null;
  pageHeader: string;
}

@Component({
  selector: 'app-monitoring-approach-summary',
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.pageHeader }}</app-page-heading>

      <ng-container *ngIf="vm.data">
        <app-monitoring-approach-summary-template [data]="vm.data"></app-monitoring-approach-summary-template>
      </ng-container>
    </ng-container>

    <app-return-to-link></app-return-to-link>
  `,
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgFor,
    NgIf,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    MonitoringApproachSummaryTemplateComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export default class MonitoringApproachSummaryComponent {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([this.store.pipe(requestTaskQuery.selectRequestTaskType)]).pipe(
    map(([type]) => {
      return {
        data: this.form.valid ? this.formProvider.getFormValue() : null,
        pageHeader: getSummaryHeaderForTaskType(type, 'monitoringApproach'),
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) protected formProvider: AerMonitoringApproachFormProvider,
    protected store: RequestTaskStore,
  ) {}
}
