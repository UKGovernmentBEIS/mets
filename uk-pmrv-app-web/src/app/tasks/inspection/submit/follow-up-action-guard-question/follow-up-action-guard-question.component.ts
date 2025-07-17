import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs/operators';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { InspectionService } from '@tasks/inspection/core/inspection.service';
import {
  INSPECTION_TASK_FORM,
  InspectionSaveRequestTaskActionPayload,
  InspectionSubmitRequestTaskPayload,
  InspectionType,
} from '@tasks/inspection/shared/inspection';
import { InspectionTaskComponent } from '@tasks/inspection/shared/inspection-task/inspection-task.component';
import { DetailsSubtaskHeaderPipe } from '@tasks/inspection/shared/pipes/details-subtask-header.pipe';
import { DetailsSubtaskLinktextPipe } from '@tasks/inspection/shared/pipes/details-subtask-linktext.pipe';
import { followUpStatusKeySubmit } from '@tasks/inspection/shared/section-status';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukComponentsModule } from 'govuk-components';

import { followUpActionGuardFormProvider } from './follow-up-action-guard-form.provider';

@Component({
  selector: 'app-follow-up-action-guard-question',
  standalone: true,
  imports: [
    InspectionTaskComponent,
    SharedModule,
    DetailsSubtaskHeaderPipe,
    DetailsSubtaskLinktextPipe,
    GovukComponentsModule,
  ],
  templateUrl: './follow-up-action-guard-question.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [followUpActionGuardFormProvider],
})
export class FollowUpActionsGuardQuestionComponent {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  isEditable = toSignal(this.inspectionService.isEditable$);
  requestTasktype$ = this.store.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem?.requestTask?.type));

  isOnsiteInspection: boolean;

  isOnsiteInspection$ = this.requestTasktype$.pipe(
    map((type) => type === 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT'),
  );

  constructor(
    @Inject(INSPECTION_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly inspectionService: InspectionService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    public readonly store: CommonTasksStore,
  ) {
    inspectionService.setType(this.route.snapshot.paramMap.get('type') as InspectionType);
    this.isOnsiteInspection$.subscribe((value) => (this.isOnsiteInspection = value));
  }

  getDownloadUrl() {
    return this.inspectionService.getBaseFileDownloadUrl();
  }

  onSubmit() {
    let nextRoute = '../';
    this.inspectionService.payload$
      .pipe(
        first(),
        switchMap((payload) => {
          nextRoute = this.form.value?.followUpActionsRequired
            ? (payload as InspectionSaveRequestTaskActionPayload)?.installationInspection?.followUpActions?.length
              ? '../follow-up-summary'
              : '../0/add-follow-up-action'
            : '../follow-up-summary';
          return this.inspectionTaskSave(payload as InspectionSaveRequestTaskActionPayload);
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => {
        return this.router.navigate([nextRoute], {
          relativeTo: this.route,
        });
      });
  }
  private inspectionTaskSave(payload: InspectionSaveRequestTaskActionPayload, completeTask = false) {
    const currentInspection = payload?.installationInspection;
    if (this.form.value.followUpActionsRequired === false) {
      currentInspection.followUpActions = [];
    }
    return this.inspectionService.postInspectionTaskSave(
      {
        installationInspection: {
          ...currentInspection,
          followUpActionsRequired: this.form.value.followUpActionsRequired,
          followUpActionsOmissionJustification:
            this.form.value.followUpActionsRequired === false
              ? this.form.value?.followUpActionsOmissionJustification
              : null,
          followUpActionsOmissionFiles:
            this.form.value.followUpActionsRequired === false
              ? this.form.get('followUpActionsOmissionFiles').value?.map((file) => file.uuid)
              : [],
        },
        installationInspectionSectionsCompleted: {
          ...payload?.installationInspectionSectionsCompleted,
          [followUpStatusKeySubmit]: completeTask,
        },
      },
      {
        ...(payload as InspectionSubmitRequestTaskPayload)?.inspectionAttachments,
        ...this.getAttachments(),
      },
    );
  }

  private getAttachments() {
    const allFiles = [...(this.form.get('followUpActionsOmissionFiles').value || [])];

    return allFiles?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {});
  }
}
