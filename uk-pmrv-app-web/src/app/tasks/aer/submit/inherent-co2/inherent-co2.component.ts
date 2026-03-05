import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup, ValidationErrors } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { inherentCO2FormProvider } from '@tasks/aer/submit/inherent-co2/inherent-co2-form.provider';

import { isWizardComplete } from './inherent-co2-wizard';

@Component({
  selector: 'app-inherent-co2',
  templateUrl: './inherent-co2.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [inherentCO2FormProvider],
})
export class InherentCo2Component implements PendingRequest {
  displayErrors$ = new BehaviorSubject<ValidationErrors>(null);
  isWizardComplete = isWizardComplete;
  isEditable$ = this.aerService.isEditable$;
  inherentInstallations$ = this.aerService.aerInherentInstallations$.pipe(
    map((aerInherentInstallations) =>
      aerInherentInstallations?.map((aerInherent) => aerInherent?.inherentReceivingTransferringInstallation),
    ),
  );

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    const nextRoute = '..';
    if (!this.form.valid) {
      this.form.markAllAsTouched();
      this.displayErrors$.next(this.form.errors);
    } else {
      this.aerService
        .postTaskSave(undefined, undefined, true, 'INHERENT_CO2')
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
