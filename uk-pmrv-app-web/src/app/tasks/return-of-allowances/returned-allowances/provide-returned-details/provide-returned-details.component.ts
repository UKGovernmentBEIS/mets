import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { ReturnOfAllowancesService } from '../../core/return-of-allowances.service';
import { RETURN_OF_ALLOWANCES_TASK_FORM } from '../../core/return-of-allowances-task-form.token';
import { provideReturnedDetailsFormProvider } from './provide-returned-details-form.provider';

@Component({
  selector: 'app-provide-returned-details',
  templateUrl: './provide-returned-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [provideReturnedDetailsFormProvider],
})
export class ProvideReturnedDetailsComponent {
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
      .saveReturnOfAllowancesReturned(
        {
          isAllowancesReturned: this.form.controls.isAllowancesReturned.value,
          returnedAllowancesDate: this.form.controls.returnedAllowancesDate?.value,
          regulatorComments: this.form.controls.regulatorComments?.value,
        },
        false,
        'PROVIDE_RETURNED_DETAILS',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate([this.nextRoute], { relativeTo: this.route }));
  }
}
