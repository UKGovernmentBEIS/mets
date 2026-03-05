import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { DoalService } from '../../../core/doal.service';
import { DOAL_TASK_FORM } from '../../../core/doal-task-form.token';
import { estimatesFormProvider } from './estimates-form.provider';

@Component({
  selector: 'app-estimates',
  templateUrl: './estimates.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [estimatesFormProvider],
})
export class EstimatesComponent {
  private readonly nextWizardStep = 'preliminary-allocations';
  editable$: Observable<boolean> = this.doalService.isEditable$;

  constructor(
    @Inject(DOAL_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../', this.nextWizardStep], { relativeTo: this.route });
    } else {
      this.doalService.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.doalService.saveDoal(
              {
                activityLevelChangeInformation: {
                  ...payload.doal.activityLevelChangeInformation,
                  ...this.form.value,
                  explainEstimates: this.form.value.areConservativeEstimates ? this.form.value.explainEstimates : null,
                },
              },
              this.route.snapshot.data.sectionKey,
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../', this.nextWizardStep], { relativeTo: this.route }));
    }
  }
}
