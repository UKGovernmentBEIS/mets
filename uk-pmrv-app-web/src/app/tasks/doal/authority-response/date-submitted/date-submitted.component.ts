import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { dateSubmittedComponentFormProvider } from '@tasks/doal/authority-response/date-submitted/date-submitted.component-form.provider';
import { DoalService } from '@tasks/doal/core/doal.service';
import { DOAL_TASK_FORM } from '@tasks/doal/core/doal-task-form.token';

@Component({
  selector: 'app-date-submitted',
  templateUrl: './date-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [dateSubmittedComponentFormProvider],
})
export class DateSubmittedComponent {
  today = new Date();

  constructor(
    @Inject(DOAL_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['summary'], { relativeTo: this.route });
    } else {
      this.doalService
        .saveDoalAuthority(
          {
            dateSubmittedToAuthority: {
              ...this.form.value,
            },
          },
          'dateSubmittedToAuthority',
          false,
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route }));
    }
  }
}
