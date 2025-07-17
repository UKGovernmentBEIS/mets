import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { QuestionFormProvider } from './outcome-27a-question-form.provider';

@Component({
  selector: 'app-outcome-27a-question',
  templateUrl: './outcome-27a-question.component.html',
  providers: [QuestionFormProvider],
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Outcome27aQuestionComponent implements PendingRequest {
  isEditable = this.bdrService.isEditable;
  bdrPayload = this.bdrService.payload as Signal<BDRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  constructor(
    @Inject(BDR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrService: BdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    const nextRoute = '../upload-files';

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      this.bdrService
        .postRegulatorTaskSave(
          {
            hasOperatorMetDataSubmissionRequirements:
              this.form.controls.hasOperatorMetDataSubmissionRequirements.value[0],
          },
          false,
          'outcome',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
