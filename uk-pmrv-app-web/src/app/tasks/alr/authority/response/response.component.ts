import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRGrantAuthorityWithCorrectionsResponse } from 'pmrv-api';

import { alrResponseFormProvider } from './response-form.provider';

@Component({
  selector: 'app-alr-response',
  templateUrl: './response.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [SharedModule, TaskSharedModule, AlrTaskSharedModule],
  providers: [alrResponseFormProvider],
})
export class AlrResponseComponent {
  today = new Date();

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.alrService.authorityPayload$
        .pipe(
          first(),
          map((payload) => payload.authorityReviewOutcome.authorityResponse.type === 'INVALID'),
        )
        .subscribe((isRejected) => this.nextWizardStep(isRejected));
    } else {
      this.alrService.authorityPayload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.alrService.postAlrAuthority(
              {
                authorityResponse: {
                  authorityRespondDate: this.form.get('authorityRespondDate').value,
                  type: this.form.get('type').value,
                  ...(this.form.get('type').value === 'VALID_WITH_CORRECTIONS' ||
                  this.form.get('type').value === 'INVALID'
                    ? {
                        decisionNotice:
                          this.form.get('type').value === 'INVALID'
                            ? this.form.get('rejectedDecisionNotice').value
                            : this.form.get('acceptedDecisionNotice').value,
                      }
                    : {}),
                  ...(this.form.get('type').value !== 'INVALID'
                    ? {
                        preliminaryAllocations: payload.regulatorPreliminaryAllocations,
                      }
                    : {}),
                } as ALRGrantAuthorityWithCorrectionsResponse,
              },
              'authorityResponse',
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.nextWizardStep(this.form.get('type').value === 'INVALID'));
    }
  }

  private nextWizardStep(isRejected): void {
    isRejected
      ? this.router.navigate(['summary'], { relativeTo: this.route })
      : this.router.navigate(['preliminary-allocations'], { relativeTo: this.route });
  }
}
