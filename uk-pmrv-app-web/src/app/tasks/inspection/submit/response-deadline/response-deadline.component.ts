import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { InspectionService } from '@tasks/inspection/core/inspection.service';
import {
  INSPECTION_TASK_FORM,
  InspectionSaveRequestTaskActionPayload,
  InspectionType,
} from '@tasks/inspection/shared/inspection';
import { InspectionTaskComponent } from '@tasks/inspection/shared/inspection-task/inspection-task.component';
import { followUpStatusKeySubmit } from '@tasks/inspection/shared/section-status';

import { responseDeadlineFormProvider } from './response-deadline-form.provider';

@Component({
  selector: 'app-response-deadline',
  template: `
    <app-inspection-task
      heading="Set a response deadline"
      caption="Follow-up actions for the operator"
      [returnToLink]="'..'">
      <app-wizard-step (formSubmit)="onSubmit()" [formGroup]="form" submitText="Continue" [hideSubmit]="!isEditable()">
        <div
          formControlName="responseDeadline"
          govuk-date-input
          hint="This will be applied to all follow-up actions"
          [isRequired]="true"></div>
      </app-wizard-step>
    </app-inspection-task>
  `,
  standalone: true,
  imports: [InspectionTaskComponent, SharedModule],
  providers: [responseDeadlineFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResponseDeadlineComponent {
  isEditable = toSignal(this.inspectionService.isEditable$);

  constructor(
    @Inject(INSPECTION_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly inspectionService: InspectionService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {
    inspectionService.setType(this.route.snapshot.paramMap.get('type') as InspectionType);
  }

  private inspectionTaskSave(payload: InspectionSaveRequestTaskActionPayload, completeTask = false) {
    return this.inspectionService.postInspectionTaskSave({
      installationInspection: {
        ...payload?.installationInspection,
        responseDeadline: this.form.value.responseDeadline,
      },
      installationInspectionSectionsCompleted: {
        ...payload?.installationInspectionSectionsCompleted,
        [followUpStatusKeySubmit]: completeTask,
      },
    });
  }

  onSubmit() {
    this.inspectionService.payload$
      .pipe(
        first(),
        switchMap((payload) => this.inspectionTaskSave(payload as InspectionSaveRequestTaskActionPayload)),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../follow-up-summary'], { relativeTo: this.route }));
  }
}
