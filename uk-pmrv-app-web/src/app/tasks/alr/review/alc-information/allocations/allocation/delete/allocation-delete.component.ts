import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';

@Component({
  selector: 'app-alr-allocation-delete',
  templateUrl: './allocation-delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [SharedModule, RouterLink],
})
export class AlrAllocationDeleteComponent {
  index = this.route.snapshot.paramMap.get('index');
  editable$ = this.alrService.isEditable$;

  constructor(
    readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  delete(): void {
    this.alrService.payload$
      .pipe(
        first(),
        switchMap((payload) =>
          this.alrService.postAlrReview(
            {
              ...payload.regulatorReviewOutcome,
              allocations: payload.regulatorReviewOutcome.allocations
                ?.filter((_, i) => {
                  return i !== Number(this.index);
                })
                .map((element, index) => {
                  return { ...element, allocationId: index + '' };
                }),
            },

            'ALC',
            false,
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
