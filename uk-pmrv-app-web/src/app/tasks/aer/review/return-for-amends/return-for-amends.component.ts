import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { BehaviorSubject, map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

@Component({
  selector: 'app-return-for-amends',
  templateUrl: './return-for-amends.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnForAmendsComponent {
  isSubmitted$ = new BehaviorSubject<boolean>(false);
  requestId$: BehaviorSubject<string | null> = new BehaviorSubject(null);

  decisionAmends$: Observable<any> = this.aerService.getPayload().pipe(
    map((payload) =>
      Object.keys(payload?.reviewGroupDecisions ?? [])
        .filter((key) => payload?.reviewGroupDecisions[key].type === 'OPERATOR_AMENDS_NEEDED')
        .map((key) => ({ groupKey: key, data: payload.reviewGroupDecisions[key] }) as any),
    ),
  );

  constructor(
    readonly store: CommonTasksStore,
    readonly aerService: AerService,
    private readonly pendingRequest: PendingRequestService,
    private readonly breadcrumbs: BreadcrumbService,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.aerService
      .postSubmit('AER_REVIEW_RETURN_FOR_AMENDS')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.requestId$.next(this.aerService.requestId);
        this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
        this.isSubmitted$.next(true);
      });
  }
}
