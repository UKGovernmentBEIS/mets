import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, iif, map, of, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain } from '@core/store/auth';

import { AviationAccountViewService } from 'pmrv-api';

import { BreadcrumbService } from '../../../../shared/breadcrumbs/breadcrumb.service';
import { NonComplianceService } from '../../core/non-compliance.service';

@Component({
  selector: 'app-non-compliance-notify-operator',
  template: `
    <div class="govuk-grid-row">
      <div
        class="govuk-grid-column-two-thirds"
        *ngIf="(isAviationWithoutLocation$ | async) === false; else noContactAddressBlockNotification">
        <app-notify-operator
          [taskId]="taskId$ | async"
          [accountId]="accountId$ | async"
          requestTaskActionType="NON_COMPLIANCE_NOTICE_OF_INTENT_NOTIFY_OPERATOR"
          [confirmationMessage]="'Notice of intent sent to operator'"
          [referenceCode]="requestId$ | async"
          [hasSignature]="false"></app-notify-operator>
      </div>
      <ng-template #noContactAddressBlockNotification>
        <app-notify-operator-no-contact-address
          [accountId]="accountId$ | async"></app-notify-operator-no-contact-address>
      </ng-template>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotifyOperatorComponent implements OnInit {
  constructor(
    private readonly nonComplianceService: NonComplianceService,
    private readonly accountViewService: AviationAccountViewService,
    private readonly route: ActivatedRoute,
    private readonly breadcrumbService: BreadcrumbService,
    private readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly accountId$ = this.nonComplianceService.requestTaskItem$.pipe(map((data) => data.requestInfo.accountId));
  readonly requestId$ = this.nonComplianceService.requestTaskItem$.pipe(map((data) => data.requestInfo.id));
  readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));
  readonly isAviationWithoutLocation$ = combineLatest([
    this.currentDomain$.pipe(takeUntil(this.destroy$)),
    this.accountId$,
  ]).pipe(
    first(),
    switchMap(([domain, accountId]) =>
      iif(
        () => domain === 'AVIATION',
        this.accountViewService.getAviationAccountById(accountId).pipe(
          first(),
          map((account) => !!(!account || account?.aviationAccount?.location == null)),
        ),
        of(false),
      ),
    ),
  );

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
