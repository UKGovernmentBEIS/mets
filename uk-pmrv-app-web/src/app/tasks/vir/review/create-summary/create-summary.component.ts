import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { VirService } from '@tasks/vir/core/vir.service';
import { VIR_TASK_FORM } from '@tasks/vir/core/vir-task-form.token';
import { createSummaryFormProvider } from '@tasks/vir/review/create-summary/create-summary-form.provider';

import { VirApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-create-summary',
  templateUrl: './create-summary.component.html',
  providers: [createSummaryFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateSummaryComponent implements PendingRequest {
  isEditable$ = this.virService.isEditable$;

  constructor(
    @Inject(VIR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly virService: VirService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    const nextRoute = 'summary';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      (this.virService.payload$ as Observable<VirApplicationReviewRequestTaskPayload>)
        .pipe(
          first(),
          switchMap((payload) =>
            this.virService.postVirReviewTaskSave({
              regulatorReviewResponse: {
                ...payload?.regulatorReviewResponse,
                ...this.form.value,
              },
              reviewSectionsCompleted: {
                ...payload?.reviewSectionsCompleted,
                ['createSummary']: false,
              },
            }),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
