import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, startWith, switchMap } from 'rxjs';

import { DRE_TASK_FORM } from '@tasks/dre/core/dre-task-form.token';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { DreService } from '../../core/dre.service';
import { calculateTotalFee } from './fee';
import { feeFormProvider } from './fee-form.provider';

@Component({
  selector: 'app-fee',
  templateUrl: './fee.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [feeFormProvider],
})
export class FeeComponent {
  private readonly nextWizardStep = 'summary';

  totalFee$: Observable<string> = this.form.valueChanges.pipe(
    startWith(this.form.value),
    map((formData) => calculateTotalFee(formData)),
  );

  constructor(
    @Inject(DRE_TASK_FORM) readonly form: UntypedFormGroup,
    readonly dreService: DreService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['..', this.nextWizardStep], { relativeTo: this.route });
    } else {
      this.dreService.dre$
        .pipe(
          first(),
          switchMap((dre) => {
            return this.dreService.saveDre(
              {
                fee: {
                  chargeOperator: dre.fee.chargeOperator,
                  feeDetails: this.form.value,
                },
              },
              false,
            );
          }),
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['..', this.nextWizardStep], { relativeTo: this.route }));
    }
  }
}
