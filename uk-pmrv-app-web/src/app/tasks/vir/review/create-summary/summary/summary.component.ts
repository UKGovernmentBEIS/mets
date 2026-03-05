import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { VirService } from '@tasks/vir/core/vir.service';

import { VirApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements PendingRequest {
  regulatorReviewResponse$ = (this.virService.payload$ as Observable<VirApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload?.regulatorReviewResponse),
  );
  isEditable$ = this.virService.isEditable$;

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly virService: VirService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    (this.virService.payload$ as Observable<VirApplicationReviewRequestTaskPayload>)
      .pipe(
        first(),
        switchMap((payload) => {
          return this.virService.postVirReviewTaskSave({
            regulatorReviewResponse: payload?.regulatorReviewResponse,
            reviewSectionsCompleted: {
              ...payload?.reviewSectionsCompleted,
              ['createSummary']: true,
            },
          });
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
