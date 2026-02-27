import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { estimatesFormProvider } from './estimates-form.provider';

@Component({
  selector: 'app-alr-estimates',
  imports: [SharedModule, AlrTaskSharedModule],
  templateUrl: './estimates.component.html',
  providers: [estimatesFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrEstimatesComponent {
  private readonly nextWizardStep = 'preliminary-allocations';
  editable$: Observable<boolean> = this.alrService.isEditable$;

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../', this.nextWizardStep], { relativeTo: this.route });
    } else {
      this.alrService.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.alrService.postAlrReview(
              {
                ...payload.regulatorReviewOutcome,

                conservativeDeterminesActivity: this.form.value?.conservativeDeterminesActivity,
                conservativeDeterminesActivityComment: this.form.value.conservativeDeterminesActivityComment
                  ? this.form.value.conservativeDeterminesActivityComment
                  : null,
              },
              'ALC',
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../', this.nextWizardStep], { relativeTo: this.route }));
    }
  }
}
