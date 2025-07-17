import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { selectCurrentDomain } from '@core/store/auth/auth.selectors';
import { AuthStore } from '@core/store/auth/auth.store';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { NonComplianceCivilPenaltyRequestTaskPayload } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { NonComplianceService } from '../../core/non-compliance.service';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements OnInit {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));
  link: string;
  notification = this.router.getCurrentNavigation()?.extras?.state?.notification;
  isEditable$ = this.nonComplianceService.isEditable$;
  payload$ = this.nonComplianceService.getPayload().pipe(
    first(),
    map((payload) => payload as NonComplianceCivilPenaltyRequestTaskPayload),
  );

  documentFiles$ = this.payload$.pipe(
    first(),
    map((payload) => payload?.civilPenalty),
    map((file) => (file ? this.nonComplianceService.getDownloadUrlFiles([file]) : [])),
  );

  isTaskSubmitted$ = this.nonComplianceService.payload$.pipe(
    first(),
    map(
      (payload) =>
        !(
          payload.payloadType === 'NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW_PAYLOAD' ||
          payload.payloadType === 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW_PAYLOAD'
        ),
    ),
  );

  requestTask$ = this.nonComplianceService.requestTaskItem$.pipe(
    first(),
    map((requestTaskItem) => requestTaskItem.requestTask),
  );

  returnTo: { text: string; link: string };

  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly nonComplianceService: NonComplianceService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    public readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    combineLatest([this.taskId$, this.currentDomain$])
      .pipe(
        switchMap(async ([taskId, domain]) => {
          this.link = domain === 'AVIATION' ? '/aviation/tasks/' + taskId : '..';
        }),
      )
      .subscribe();

    combineLatest([this.isTaskSubmitted$, this.requestTask$, this.currentDomain$])
      .pipe(
        switchMap(async ([isTaskSubmitted, requestTask, currentDomain]) => {
          if (!isTaskSubmitted) {
            const taskId = requestTask?.id;
            const text = 'Non compliance';
            let link;
            switch (requestTask.payload.payloadType) {
              case 'NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW_PAYLOAD':
                link = ['/tasks', taskId, 'non-compliance', 'civil-penalty-notice', 'peer-review-wait'];
                if (currentDomain === 'INSTALLATION') {
                  this.breadcrumbService.addToLastBreadcrumbAndShow('peer-review-wait');
                }
                break;
              case 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW_PAYLOAD':
                link = ['/tasks', taskId, 'non-compliance', 'civil-penalty-notice', 'cpn-peer-review'];
                if (currentDomain === 'INSTALLATION') {
                  this.breadcrumbService.addToLastBreadcrumbAndShow('cpn-peer-review');
                }
                break;
              default:
                link = ['/tasks', taskId, 'non-compliance', 'civil-penalty-notice', 'submit'];
                break;
            }
            this.returnTo = { text, link };
          } else {
            const text = 'non compliance task';
            const link = '..';
            this.returnTo = { text, link };
          }
        }),
      )
      .subscribe();
  }

  onSubmit(): void {
    this.nonComplianceService
      .saveCivilPenaltySectionStatus(true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate([this.link], { relativeTo: this.route }));
  }
}
