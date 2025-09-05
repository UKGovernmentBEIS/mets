import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { alrCommentsFormProvider } from './comments-form.provider';

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [SharedModule, AlrTaskSharedModule],
  providers: [alrCommentsFormProvider],
})
export class AlrCommentsComponent {
  private readonly nextWizardStep = 'summary';
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
      this.router.navigate(['../', this.nextWizardStep], {
        relativeTo: this.route,
      });
    } else {
      this.alrService.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.alrService.postAlrReview(
              {
                ...payload.regulatorReviewOutcome,
                ...this.form.value,
              },
              'ALC',
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() =>
          this.router.navigate(['../', this.nextWizardStep], {
            relativeTo: this.route,
          }),
        );
    }
  }
}
