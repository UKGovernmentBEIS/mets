import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { selectCurrentDomain } from '@core/store';
import { AuthStore } from '@core/store/auth/auth.store';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { NonComplianceService } from '../../../core/non-compliance.service';
import { NON_COMPLIANCE_TASK_FORM } from '../../../core/non-compliance-form.token';
import { noticeOfIntentFormProvider } from './notice-of-intent-form.provider';

@Component({
  selector: 'app-notice-of-intent',
  templateUrl: './notice-of-intent.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [noticeOfIntentFormProvider],
})
export class NoticeOfIntentComponent {
  private readonly nextWizardStep = 'daily-penalty';
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
    if (!this.form.dirty) {
      this.router.navigate(['..', this.nextWizardStep], { relativeTo: this.route });
    } else {
      this.nonComplianceService
        .saveNonCompliance(
          {
            ...this.form.value,
          },
          false,
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => {
          this.router.navigate(['..', this.nextWizardStep], { relativeTo: this.route });
        });
    }
  }
}
