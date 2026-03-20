import { ChangeDetectionStrategy, Component, Inject, OnInit, signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { selectCurrentDomain } from '@core/store';
import { AuthStore } from '@core/store/auth/auth.store';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { NonComplianceApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { NonComplianceService } from '../../core/non-compliance.service';
import { NON_COMPLIANCE_TASK_FORM } from '../../core/non-compliance-form.token';
import { detailsOfBreanchFormProvider } from './details-of-breach-form.provider';

@Component({
  selector: 'app-details-of-breach',
  standalone: false,
  templateUrl: './details-of-breach.component.html',
  providers: [detailsOfBreanchFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsOfBreachComponent implements OnInit {
  reasonOptions: NonComplianceApplicationSubmitRequestTaskPayload['reason'][] = [
    'FAILURE_TO_SURRENDER_ALLOWANCES_100',
    'FAILURE_TO_SURRENDER_ALLOWANCES_20',
    'CARRYING_OUT_A_REGULATED_ACTIVITY_WITHOUT_A_PERMIT',
    'FAILURE_TO_MONITOR_REPORTABLE_EMISSIONS',
    'FAILURE_TO_REPORT_REPORTABLE_EMISSIONS',
    'FAILURE_TO_SUBMIT_AN_IMPROVEMENT_REPORT',
    'FAILURE_TO_NOTIFY',
    'FAILURE_TO_MONITOR_ACTIVITY_LEVELS',
    'FAILURE_TO_REPORT_ACTIVITY_LEVELS',
    'FAILURE_TO_COMPLY_WITH_THE_CONDITION_OF_A_PERMIT',
    'FAILURE_TO_TRANSFER_OR_SURRENDER_ALLOWANCES_WHEN_UNDERREPORTING_DISCOVERED_AFTER_TRANSFER',
    'FAILURE_TO_SURRENDER_A_PERMIT',
    'FAILURE_TO_SUBMIT_INFORMATION_UNDER_ARTICLE_27_A',
    'EXCEEDING_EMISSIONS_TARGET',
    'FAILURE_TO_PAY_PENALTY_FOR_EXCEEDING_EMISSIONS_TARGET',
    'UNDER_REPORTING_EMISSIONS',
    'FAILURE_TO_NOTIFY_WHEN_CEASE_TO_MEET_CRITERIA',
    'REPORTABLE_EMISSIONS_EXCEED_MAXIMUM_AMOUNT',
    'FAILURE_TO_NOTIFY_WHEN_REPORTABLE_EMISSIONS_EXCEED_MAXIMUM_AMOUNT',
    'FAILURE_TO_APPLY_FOR_AN_EMISSIONS_MONITORING_PLAN_ETS',
    'FAILURE_TO_COMPLY_WITH_A_CONDITION_OF_AN_EMISSIONS_MONITORING_PLAN_ETS',
    'FAILURE_TO_MONITOR_AVIATION_EMISSIONS_ETS',
    'FAILURE_TO_REPORT_AVIATION_EMISSIONS_ETS',
    'FAILURE_TO_APPLY_FOR_AN_EMISSIONS_MONITORING_PLAN_CORSIA',
    'FAILURE_TO_COMPLY_WITH_A_CONDITION_OF_AN_EMISSIONS_MONITORING_PLAN_CORSIA',
    'FAILURE_TO_MONITOR_EMISSIONS_CORSIA',
    'FAILURE_TO_REPORT_EMISSIONS_CORSIA',
    'FAILURE_TO_KEEP_RECORDS_CORSIA',
    'FAILURE_TO_COMPLY_WITH_DEFICIT_NOTICE',
    'FAILURE_TO_COMPLY_WITH_NOTICE_TO_RETURN_ALLOWANCES',
    'FAILURE_TO_COMPLY_WITH_AN_ENFORCEMENT_NOTICE_ETS',
    'FAILURE_TO_COMPLY_WITH_AN_ENFORCEMENT_NOTICE_CORSIA',
    'FAILURE_TO_COMPLY_WITH_AN_INFORMATION_NOTICE_ETS',
    'FAILURE_TO_COMPLY_WITH_AN_INFORMATION_NOTICE_CORSIA',
    'PROVIDING_FALSE_OR_MISLEADING_INFORMATION_ETS',
    'PROVIDING_FALSE_OR_MISLEADING_INFORMATION_CORSIA',
    'REFUSAL_TO_ALLOW_ACCESS_TO_PREMISES_ETS',
    'REFUSAL_TO_ALLOW_ACCESS_TO_PREMISES_CORSIA',
  ];
  private nextWizardStep = '../choose-workflow';
  currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));
  requestTaskItem = this.nonComplianceService.requestTaskItem;
  isAmendable = this.requestTaskItem().allowedRequestTaskActions.includes('NON_COMPLIANCE_AMEND_DETAILS');
  requestTaskType = this.requestTaskItem().requestTask?.type;
  cancelLink = signal(this.isAmendable ? this.nonComplianceService.getAmendmentUrl(this.requestTaskType) : null);
  returnToLink = signal(this.isAmendable ? this.nonComplianceService.getAmendmentUrl(this.requestTaskType) : '..');

  breadcrumbs: BreadcrumbItem[];

  ngOnInit() {
    const parentUrlSegments = this.route.snapshot.pathFromRoot.map((route) => route.url).flat();
    const parentUrl = '/' + parentUrlSegments.slice(0, parentUrlSegments.length - 2).join('/');

    this.breadcrumbs = [
      {
        link: this.router.url.startsWith('/aviation') ? ['/aviation/dashboard'] : ['/dashboard'],
        text: 'Dashboard',
      },
      {
        link: [
          parentUrl + '/' + this.nonComplianceService.getAmendmentUrl(this.requestTaskType)?.split('/')?.reverse()[0],
        ],
        text: this.nonComplianceService.getAmendmentText(this.requestTaskType),
      },
    ];

    this.breadcrumbService.show(this.breadcrumbs);
  }

  constructor(
    @Inject(NON_COMPLIANCE_TASK_FORM) readonly form: UntypedFormGroup,
    readonly nonComplianceService: NonComplianceService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
    private readonly breadcrumbService: BreadcrumbService,
    public readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
    private readonly commonStore: CommonTasksStore,
  ) {}

  onSubmit(): void {
    const taskId = this.route.snapshot.params['taskId'];

    this.nonComplianceService.payload$
      .pipe(
        first(),
        switchMap((payload) => {
          const nonCompliance = payload as NonComplianceApplicationSubmitRequestTaskPayload;
          if (this.isAmendable) {
            this.nextWizardStep = this.nonComplianceService.getAmendmentUrl(this.requestTaskType);
          }
          return this.nonComplianceService.saveNonCompliance(
            {
              ...(nonCompliance as any)?.payload,
              ...this.form.value,
            },
            false,
          );
        }),
        switchMap(() => this.commonStore.requestTaskObservable(taskId)),
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate([this.nextWizardStep], { relativeTo: this.route }));
  }
}
