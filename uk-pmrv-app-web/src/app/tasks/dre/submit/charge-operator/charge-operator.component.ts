import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { DRE_TASK_FORM } from '@tasks/dre/core/dre-task-form.token';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { DreService } from '../../core/dre.service';
import { chargeOperatorFormProvider } from './charge-operator-form.provider';

@Component({
  selector: 'app-charge-operator',
  templateUrl: './charge-operator.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [chargeOperatorFormProvider],
})
export class ChargeOperatorComponent {
  constructor(
    @Inject(DRE_TASK_FORM) readonly form: UntypedFormGroup,
    readonly dreService: DreService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    const chargeOperator: boolean = this.form.value.chargeOperator;
    this.dreService.dre$
      .pipe(
        first(),
        switchMap((dre) => {
          return this.dreService.saveDre(
            {
              fee: {
                chargeOperator,
                ...(chargeOperator ? { feeDetails: dre.fee?.feeDetails } : {}),
              },
            },
            false,
          );
        }),
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['..', this.resolveNextStep(chargeOperator)], { relativeTo: this.route }));
  }

  private resolveNextStep(chargeOperator: boolean) {
    return chargeOperator ? 'fee' : 'summary';
  }
}
