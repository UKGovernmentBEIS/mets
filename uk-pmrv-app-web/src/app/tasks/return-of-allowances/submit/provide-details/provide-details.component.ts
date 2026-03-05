import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { ReturnOfAllowancesService } from '../../core/return-of-allowances.service';
import { RETURN_OF_ALLOWANCES_TASK_FORM } from '../../core/return-of-allowances-task-form.token';
import { provideDetailsFormProvider } from './provide-details-form.provider';

@Component({
  selector: 'app-provide-details',
  templateUrl: './provide-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [provideDetailsFormProvider],
})
export class ProvideDetailsComponent {
  years: number[] = [2021, 2022, 2023, 2024, 2025, 2026, 2027, 2028, 2029, 2030];

  nextRoute = '../summary';
  constructor(
    @Inject(RETURN_OF_ALLOWANCES_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
    readonly returnOfAllowancesService: ReturnOfAllowancesService,
  ) {}

  onSubmit() {
    this.returnOfAllowancesService
      .saveReturnOfAllowances(
        {
          numberOfAllowancesToBeReturned: this.form.controls.numberOfAllowancesToBeReturned.value,
          years: this.form.controls.years.value,
          reason: this.form.controls.reason.value,
          dateToBeReturned: this.form.controls.dateToBeReturned?.value,
          regulatorComments: this.form.controls.regulatorComments?.value,
        },
        false,
        'PROVIDE_DETAILS',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate([this.nextRoute], { relativeTo: this.route }));
  }
}
