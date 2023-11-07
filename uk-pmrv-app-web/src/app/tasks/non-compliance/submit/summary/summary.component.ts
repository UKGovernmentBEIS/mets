import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { selectCurrentDomain } from '@core/store';
import { AuthStore } from '@core/store/auth/auth.store';

import { NonComplianceApplicationSubmitRequestTaskPayload, RequestInfoDTO } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { NonComplianceService } from '../../core/non-compliance.service';
@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements OnInit {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  nonCompliance$ = this.nonComplianceService.getPayload();
  isEditable$ = this.nonComplianceService.isEditable$;

  selectedRequests$ = this.nonComplianceService.payload$.pipe(
    map((payload) => (payload as NonComplianceApplicationSubmitRequestTaskPayload)?.selectedRequests || []),
  );

  availableRequests$ = this.nonComplianceService.payload$.pipe(
    map((payload) => (payload as NonComplianceApplicationSubmitRequestTaskPayload)?.availableRequests),
  );

  requests: Array<RequestInfoDTO>;
  currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));

  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly nonComplianceService: NonComplianceService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    public readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    combineLatest([this.selectedRequests$, this.availableRequests$])
      .pipe(
        first(),
        switchMap(([selectedRequests, availableRequests]) => {
          return (this.requests = availableRequests.filter((a) => selectedRequests.some((b) => b === a?.id)));
        }),
      )
      .subscribe();
  }

  onSubmit(): void {
    this.nonComplianceService
      .saveSectionStatus(true)
      .pipe(switchMap(() => this.nonComplianceService.submitNonCompliance().pipe(this.pendingRequest.trackRequest())))
      .subscribe(() => this.router.navigate(['../confirmation'], { relativeTo: this.route }));
  }
}
