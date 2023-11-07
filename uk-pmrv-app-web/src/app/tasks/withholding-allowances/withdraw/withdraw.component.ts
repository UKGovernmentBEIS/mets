import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { WithholdingOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { WithholdingAllowancesService } from '../core/withholding-allowances.service';
import { getSectionStatus } from './withdraw';

@Component({
  selector: 'app-withdraw',
  templateUrl: './withdraw.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WithdrawComponent implements OnInit {
  sectionStatus$ = this.withholdingAllowancesService.payload$.pipe(
    first(),
    map((payload) => getSectionStatus(payload as WithholdingOfAllowancesApplicationSubmitRequestTaskPayload)),
  );

  canViewSectionDetails$: Observable<boolean> = combineLatest([
    this.withholdingAllowancesService.isEditable$,
    this.sectionStatus$,
  ]).pipe(map(([isEditable, sectionStatus]) => isEditable || sectionStatus === 'complete'));

  allowNotifyOperator$ = this.withholdingAllowancesService.requestTaskItem$.pipe(
    first(),
    map(
      (data) =>
        getSectionStatus(data.requestTask.payload as WithholdingOfAllowancesApplicationSubmitRequestTaskPayload) ===
          'complete' &&
        data.allowedRequestTaskActions.includes('WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_NOTIFY_OPERATOR_FOR_DECISION'),
    ),
  );

  constructor(
    private readonly withholdingAllowancesService: WithholdingAllowancesService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.breadcrumbService.clear();
  }

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }
}
