import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AlrAlcInformationSummaryTemplateComponent } from '@shared/components/alr/alc-information-summary-template/alc-information-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { ALRApplicationRegulatorReviewOutcome } from 'pmrv-api';

@Component({
  selector: 'app-alr-alc-information-summary',
  imports: [SharedModule, AlrTaskSharedModule, AlrAlcInformationSummaryTemplateComponent],
  templateUrl: './alc-information-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ALCInformationSummaryComponent {
  editable$: Observable<boolean> = this.alrService.isEditable$;
  alc$: Observable<ALRApplicationRegulatorReviewOutcome> = this.alrService.payload$.pipe(
    map((payload) => payload.regulatorReviewOutcome),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly alrService: AlrService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.alrService.payload$
      .pipe(
        first(),
        switchMap((payload) =>
          this.alrService.postAlrReview(
            {
              ...payload.regulatorReviewOutcome,
            },
            'ALC',
            true,
          ),
        ),
        this.pendingRequest.trackRequest(),
      )

      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
