import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { MonitoringPlanChangesFormProvider } from '@aviation/request-task/aer/shared/monitoring-plan-changes/monitoring-plan-changes-form.provider';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType } from '@aviation/request-task/util';
import { AerMonitoringPlanVersionsComponent } from '@aviation/shared/components/aer/monitoring-plan-versions';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerMonitoringPlanChanges, AviationAerMonitoringPlanVersion } from 'pmrv-api';

interface ViewModel {
  planVersions: AviationAerMonitoringPlanVersion[];
  data: AviationAerMonitoringPlanChanges;
  pageHeader: string;
}

@Component({
  selector: 'app-monitoring-plan-changes-summary',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgFor,
    NgIf,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    AerMonitoringPlanVersionsComponent,
  ],
  // eslint-disable-next-line @angular-eslint/component-max-inline-declarations
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.pageHeader }}</app-page-heading>
      <app-aer-monitoring-plan-versions [planVersions]="vm.planVersions"></app-aer-monitoring-plan-versions>

      <ng-container *ngIf="vm.data">
        <h2 class="govuk-heading-m govuk-!-width-two-thirds">Changes not covered by the emissions monitoring plans</h2>

        <dl govuk-summary-list>
          <div govukSummaryListRow>
            <dt govukSummaryListRowKey>
              Were there any changes not covered by the emissions monitoring plans in the reporting year?
            </dt>

            <dd govukSummaryListRowValue>
              {{ vm.data.notCoveredChangesExist ? 'Yes' : 'No' }}
            </dd>
            <dd></dd>
          </div>
        </dl>

        <dl govuk-summary-list *ngIf="vm.data.notCoveredChangesExist">
          <div govukSummaryListRow>
            <dt govukSummaryListRowKey>Changes reported by the operator</dt>
            <dd govukSummaryListRowValue>
              {{ vm.data?.details }}
            </dd>
            <dd></dd>
          </div>
        </dl>
      </ng-container>
    </ng-container>

    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class MonitoringPlanChangesSummaryComponent {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerQuery.selectAerMonitoringPlanVersions),
  ]).pipe(
    map(([type, planVersions]) => {
      return {
        planVersions,
        data: this.form.valid ? this.form.value : null,
        pageHeader: getSummaryHeaderForTaskType(type, 'aerMonitoringPlanChanges'),
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: MonitoringPlanChangesFormProvider,
    private store: RequestTaskStore,
  ) {}
}
