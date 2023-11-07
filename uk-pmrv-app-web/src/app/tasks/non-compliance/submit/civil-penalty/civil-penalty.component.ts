import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { selectCurrentDomain } from '@core/store';
import { AuthStore } from '@core/store/auth/auth.store';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { NonComplianceService } from '../../core/non-compliance.service';
import { NON_COMPLIANCE_TASK_FORM } from '../../core/non-compliance-form.token';
import { civilPenaltyFormProvider } from './civil-penalty-form.provider';

@Component({
  selector: 'app-civil-penalty',
  templateUrl: './civil-penalty.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [civilPenaltyFormProvider],
})
export class CivilPenaltyComponent {
  private readonly nextWizardStep = '';
  currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));

  constructor(
    @Inject(NON_COMPLIANCE_TASK_FORM) readonly form: UntypedFormGroup,
    readonly nonComplianceService: NonComplianceService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
    public readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}

  onSubmit(): void {
    const civilPenalty: boolean = this.form.value.civilPenalty;

    this.nonComplianceService
      .saveNonCompliance(
        {
          civilPenalty,
          ...(!civilPenalty
            ? {
                noCivilPenaltyJustification: this.form.value.noCivilPenaltyJustification,
                dailyPenalty: null,
                noticeOfIntent: null,
              }
            : { noCivilPenaltyJustification: '' }),
        },
        false,
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        civilPenalty
          ? this.router.navigate(['..', 'notice-of-intent'], { relativeTo: this.route })
          : this.router.navigate(['..', 'summary'], { relativeTo: this.route });
      });
  }
}
