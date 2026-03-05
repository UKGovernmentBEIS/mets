import { ChangeDetectionStrategy, Component, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, DoalDetermination } from 'pmrv-api';

@Component({
  selector: 'app-alr-determination',
  templateUrl: './determination.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [SharedModule, AlrTaskSharedModule],
})
export class AlrDeterminationComponent {
  editable$ = this.alrService.isEditable$;
  payload = this.alrService.payload as Signal<ALRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  constructor(
    private readonly alrService: AlrService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue(type: DoalDetermination['type']): void {
    if (!this.determinationChanged(type)) {
      this.router.navigate([this.resolveTypeUrl(type), 'reason'], { relativeTo: this.route });
    } else {
      this.alrService.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.alrService.postAlrReview(
              {
                ...payload?.regulatorReviewOutcome,
                determination: {
                  type,
                },
              },
              'DETERMINATION',
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() =>
          this.router.navigate([this.resolveTypeUrl(type), 'reason'], {
            relativeTo: this.route,
          }),
        );
    }
  }

  private determinationChanged(type: DoalDetermination['type']): boolean {
    const payload = this.payload();
    return payload.regulatorReviewOutcome?.determination?.type !== type;
  }

  private resolveTypeUrl(type: DoalDetermination['type']) {
    return type === 'CLOSED_ALR' ? 'close' : 'proceed-authority';
  }
}
