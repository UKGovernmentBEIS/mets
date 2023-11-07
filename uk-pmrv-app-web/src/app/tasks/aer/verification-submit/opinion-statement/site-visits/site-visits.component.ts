import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SiteVisitsService } from '@shared/components/review-groups/opinion-statement-group/services/site-visits.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { siteVisitsFormProvider } from '@tasks/aer/verification-submit/opinion-statement/site-visits/site-visits.form.provider';

import { OpinionStatement } from 'pmrv-api';

@Component({
  selector: 'app-site-visits',
  templateUrl: './site-visits.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [siteVisitsFormProvider],
})
export class SiteVisitsComponent implements PendingRequest {
  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly siteVisitsService: SiteVisitsService,
  ) {}

  onSubmit(): void {
    const nextRoute = this.siteVisitsService.mapVisitTypeToPath(this.form.get('siteVisit.siteVisitType').value);
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      this.aerService
        .getMappedPayload<OpinionStatement>(['verificationReport', 'opinionStatement'])
        .pipe(
          switchMap((opinionStatement) =>
            this.aerService.postVerificationTaskSave(
              {
                opinionStatement: {
                  ...opinionStatement,
                  ...this.form.value,
                },
              },
              false,
              'opinionStatement',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
