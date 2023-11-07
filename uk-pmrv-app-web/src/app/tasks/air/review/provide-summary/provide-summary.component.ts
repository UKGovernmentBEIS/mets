import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { provideSummaryFormProvider } from '@tasks/air/review/provide-summary/provide-summary-form.provider';
import { AIR_TASK_FORM } from '@tasks/air/shared/air-task-form.token';
import { AirService } from '@tasks/air/shared/services/air.service';

import { AirApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-provide-summary',
  templateUrl: './provide-summary.component.html',
  providers: [provideSummaryFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProvideSummaryComponent {
  isEditable$ = this.airService.isEditable$;

  constructor(
    @Inject(AIR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly airService: AirService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    const nextRoute = 'summary';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      (this.airService.payload$ as Observable<AirApplicationReviewRequestTaskPayload>)
        .pipe(
          first(),
          switchMap((payload) =>
            this.airService.postAirReviewTaskSave({
              regulatorReviewResponse: {
                ...payload?.regulatorReviewResponse,
                ...this.form.value,
              },
              reviewSectionsCompleted: {
                ...payload?.reviewSectionsCompleted,
                ['provideSummary']: false,
              },
            }),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
