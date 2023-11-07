import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { responseComponentFormProvider } from '@tasks/doal/authority-response/response/response.component-form.provider';
import { DoalService } from '@tasks/doal/core/doal.service';
import { DOAL_TASK_FORM } from '@tasks/doal/core/doal-task-form.token';

import { DoalGrantAuthorityWithCorrectionsResponse } from 'pmrv-api';

@Component({
  selector: 'app-response',
  templateUrl: './response.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [responseComponentFormProvider],
})
export class ResponseComponent {
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
      this.doalService.authorityPayload$
        .pipe(
          first(),
          map((payload) => payload.doalAuthority.authorityResponse.type === 'INVALID'),
        )
        .subscribe((isRejected) => this.nextWizardStep(isRejected));
    } else {
      this.doalService.authorityPayload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.doalService.saveDoalAuthority(
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
                } as DoalGrantAuthorityWithCorrectionsResponse,
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
