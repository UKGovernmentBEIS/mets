import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { AerMonitoringPlanChangesSummaryTemplateComponent } from '@aviation/shared/components/aer/monitoring-plan-changes-summary-template/monitoring-plan-changes-summary-template.component';
import { AerMonitoringPlanVersionsComponent } from '@aviation/shared/components/aer/monitoring-plan-versions';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerMonitoringPlanChanges, AviationAerMonitoringPlanVersion } from 'pmrv-api';

import { MonitoringPlanChangesFormProvider } from '../monitoring-plan-changes-form.provider';

interface ViewModel {
  planVersions: AviationAerMonitoringPlanVersion[];
  data: AviationAerMonitoringPlanChanges;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
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
    AerMonitoringPlanChangesSummaryTemplateComponent,
    AerReviewDecisionGroupComponent,
  ],
  templateUrl: './monitoring-plan-changes-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringPlanChangesSummaryComponent implements OnInit, OnDestroy {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerQuery.selectAerMonitoringPlanVersions),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerQuery.selectStatusForTask('aerMonitoringPlanChanges')),
  ]).pipe(
    map(([type, planVersions, isEditable, taskStatus]) => {
      return {
        planVersions,
        data: this.form.valid ? this.form.value : null,
        pageHeader: getSummaryHeaderForTaskType(type, 'aerMonitoringPlanChanges'),
        isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: MonitoringPlanChangesFormProvider,
    private store: RequestTaskStore,
    private backLinkService: BackLinkService,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onSubmit() {
    if (this.form?.valid) {
      this.store.aerDelegate
        .saveAer({ aerMonitoringPlanChanges: this.form.value }, 'complete')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../../../'], { relativeTo: this.route, queryParams: { change: null } });
        });
    }
  }
}
