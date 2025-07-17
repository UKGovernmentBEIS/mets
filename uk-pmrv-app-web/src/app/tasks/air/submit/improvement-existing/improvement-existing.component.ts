import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AIR_TASK_FORM } from '@tasks/air/shared/air-task-form.token';
import { AirService } from '@tasks/air/shared/services/air.service';
import { improvementExistingFormProvider } from '@tasks/air/submit/improvement-existing/improvement-existing-form.provider';

import { AirApplicationSubmitRequestTaskPayload, OperatorAirImprovementAlreadyMadeResponse } from 'pmrv-api';

@Component({
  selector: 'app-improvement-existing',
  templateUrl: './improvement-existing.component.html',
  providers: [improvementExistingFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ImprovementExistingComponent {
  reference = this.route.snapshot.paramMap.get('id');
  isEditable$ = this.airService.isEditable$;
  today = new Date();

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

  private getFormData(payload: AirApplicationSubmitRequestTaskPayload): OperatorAirImprovementAlreadyMadeResponse {
    return {
      ...payload?.operatorImprovementResponses[this.reference],
      explanation: this.form.get('explanation').value,
      improvementDate: this.form.get('improvementDate').value ? this.form.get('improvementDate').value : null,
      files: this.form.get('files').value?.map((file) => file.uuid),
    };
  }

  private getAirAttachments() {
    return this.form.get('files').value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {});
  }
}
