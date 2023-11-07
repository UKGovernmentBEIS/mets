import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { dataGapsFormProvider } from '@tasks/aer/verification-submit/data-gaps/data-gaps-form.provider';

@Component({
  selector: 'app-data-gaps',
  templateUrl: './data-gaps.component.html',
  providers: [dataGapsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataGapsComponent {
  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.nextUrl();
    } else {
      this.aerService
        .postVerificationTaskSave(
          {
            methodologiesToCloseDataGaps: {
              ...this.form.value,
              ...(!this.form.get('dataGapRequired').value ? { dataGapRequiredDetails: null } : {}),
            },
          },
          false,
          'methodologiesToCloseDataGaps',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.nextUrl());
    }
  }

  private nextUrl() {
    return this.form.get('dataGapRequired').value
      ? this.router.navigate(['regulator-approved'], { relativeTo: this.route })
      : this.router.navigate(['summary'], { relativeTo: this.route });
  }
}
