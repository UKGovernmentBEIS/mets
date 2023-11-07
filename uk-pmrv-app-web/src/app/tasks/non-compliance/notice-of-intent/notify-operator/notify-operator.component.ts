import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain } from '@core/store/auth';

import { BreadcrumbService } from '../../../../shared/breadcrumbs/breadcrumb.service';
import { NonComplianceService } from '../../core/non-compliance.service';

@Component({
  selector: 'app-non-compliance-notify-operator',
  template: `<div class="govuk-grid-row">
    <div class="govuk-grid-column-two-thirds">
      <app-notify-operator
        [taskId]="taskId$ | async"
        [accountId]="accountId$ | async"
        requestTaskActionType="NON_COMPLIANCE_NOTICE_OF_INTENT_NOTIFY_OPERATOR"
        [confirmationMessage]="'Notice of intent sent to operator'"
        [isRegistryToBeNotified]="false"
        [referenceCode]="requestId$ | async"
        [hasSignature]="false"
      ></app-notify-operator>
    </div>
  </div> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotifyOperatorComponent implements OnInit {
  constructor(
    private readonly nonComplianceService: NonComplianceService,
    private readonly route: ActivatedRoute,
    private readonly breadcrumbService: BreadcrumbService,
    private readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly accountId$ = this.nonComplianceService.requestTaskItem$.pipe(map((data) => data.requestInfo.accountId));
  readonly requestId$ = this.nonComplianceService.requestTaskItem$.pipe(map((data) => data.requestInfo.id));
  readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));

  ngOnInit(): void {
    this.currentDomain$.pipe(takeUntil(this.destroy$)).subscribe((domain) => {
      if (domain === 'INSTALLATION') {
        this.route.paramMap.subscribe((paramMap) => {
          const link = ['/tasks', paramMap.get('taskId'), 'non-compliance', 'notice-of-intent'];
          this.breadcrumbService.show([{ text: 'Upload notice of intent: non-compliance', link }]);
        });
      }
    });
  }
}
