import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AirService } from '@tasks/air/shared/services/air.service';

import { AirApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements PendingRequest {
  regulatorAirReviewResponse$ = (this.airService.payload$ as Observable<AirApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload?.regulatorReviewResponse),
  );
  isEditable$ = this.airService.isEditable$;

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly airService: AirService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    (this.airService.payload$ as Observable<AirApplicationReviewRequestTaskPayload>)
      .pipe(
        first(),
        switchMap((payload) => {
          return this.airService.postAirReviewTaskSave({
            regulatorReviewResponse: payload?.regulatorReviewResponse,
            reviewSectionsCompleted: {
              ...payload?.reviewSectionsCompleted,
              ['provideSummary']: true,
            },
          });
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
