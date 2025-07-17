import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AIR_TASK_FORM } from '@tasks/air/shared/air-task-form.token';
import { AirService } from '@tasks/air/shared/services/air.service';
import { improvementNegativeFormProvider } from '@tasks/air/submit/improvement-negative/improvement-negative-form.provider';

import { AirApplicationSubmitRequestTaskPayload, OperatorAirImprovementNoResponse } from 'pmrv-api';

@Component({
  selector: 'app-improvement-negative',
  templateUrl: './improvement-negative.component.html',
  providers: [improvementNegativeFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ImprovementNegativeComponent {
  reference = this.route.snapshot.paramMap.get('id');
  isEditable$ = this.airService.isEditable$;

  constructor(
    @Inject(AIR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly airService: AirService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    const nextRoute = `../summary`;

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      this.airService.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.airService.postAirTaskSave(
              {
                operatorImprovementResponses: {
                  ...payload?.operatorImprovementResponses,
                  [this.reference]: this.getFormData(payload),
                },
                airSectionsCompleted: {
                  ...payload?.airSectionsCompleted,
                  [this.reference]: false,
                },
              },
              {
                ...payload?.airAttachments,
                ...this.getAirAttachments(),
              },
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  getDownloadUrl() {
    return this.airService.getBaseFileDownloadUrl();
  }

  private getFormData(payload: AirApplicationSubmitRequestTaskPayload): OperatorAirImprovementNoResponse {
    const isCostUnreasonable = this.form.get('justification').value.includes('isCostUnreasonable');
    const isTechnicallyInfeasible = this.form.get('justification').value.includes('isTechnicallyInfeasible');
    const technicalInfeasibilityExplanation = this.form.get('technicalInfeasibilityExplanation').value;

    return {
      ...payload?.operatorImprovementResponses[this.reference],
      isCostUnreasonable: isCostUnreasonable,
      isTechnicallyInfeasible: isTechnicallyInfeasible,
      technicalInfeasibilityExplanation: isTechnicallyInfeasible ? technicalInfeasibilityExplanation : null,
      files: this.form.get('files').value?.map((file) => file.uuid),
    };
  }

  private getAirAttachments() {
    return this.form.get('files').value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {});
  }
}
