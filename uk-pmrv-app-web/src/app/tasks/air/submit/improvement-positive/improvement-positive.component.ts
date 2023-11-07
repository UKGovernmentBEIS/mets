import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AIR_TASK_FORM } from '@tasks/air/shared/air-task-form.token';
import { AirService } from '@tasks/air/shared/services/air.service';
import { improvementPositiveFormProvider } from '@tasks/air/submit/improvement-positive/improvement-positive-form.provider';

import { AirApplicationSubmitRequestTaskPayload, OperatorAirImprovementYesResponse } from 'pmrv-api';

@Component({
  selector: 'app-improvement-positive',
  templateUrl: './improvement-positive.component.html',
  providers: [improvementPositiveFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ImprovementPositiveComponent {
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
    return this.airService.createBaseFileDownloadUrl();
  }

  private getFormData(payload: AirApplicationSubmitRequestTaskPayload): OperatorAirImprovementYesResponse {
    return {
      ...payload?.operatorImprovementResponses[this.reference],
      proposal: this.form.get('proposal').value,
      proposedDate: this.form.get('proposedDate').value ? this.form.get('proposedDate').value : null,
      files: this.form.get('files').value?.map((file) => file.uuid),
    };
  }

  private getAirAttachments() {
    return this.form.get('files').value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {});
  }
}
