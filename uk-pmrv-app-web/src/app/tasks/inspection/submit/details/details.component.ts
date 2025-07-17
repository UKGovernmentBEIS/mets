import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable } from 'rxjs';
import { first, map, shareReplay, switchMap, withLatestFrom } from 'rxjs/operators';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { UserFullNamePipe } from '@shared/pipes/user-full-name.pipe';
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
import { detailsStatusKeySubmit } from '@tasks/inspection/shared/section-status';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { FieldsetDirective, GovukComponentsModule, GovukSelectOption } from 'govuk-components';

import { TasksAssignmentService } from 'pmrv-api';

import { createAnotherOfficer, detailsFormProvider } from './details-form.provider';

@Component({
  selector: 'app-details',
  standalone: true,
  imports: [
    InspectionTaskComponent,
    SharedModule,
    DetailsSubtaskHeaderPipe,
    DetailsSubtaskLinktextPipe,
    GovukComponentsModule,
  ],
  templateUrl: './details.component.html',
  styleUrl: './details.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [detailsFormProvider, FieldsetDirective, UserFullNamePipe],
})
export class DetailsComponent {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  isEditable = toSignal(this.inspectionService.isEditable$);
  requestTasktype$ = this.store.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem?.requestTask?.type));

  isOnsiteInspection: boolean;

  isOnsiteInspection$ = this.requestTasktype$.pipe(
    map((type) => type === 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT'),
  );

  officers$: Observable<GovukSelectOption<string>[]> = this.taskId$.pipe(
    withLatestFrom(this.requestTasktype$),
    switchMap(([taskId, taskType]) => this.tasksAssignmentService.getCandidateAssigneesByTaskType(taskId, taskType)),
    map((users) =>
      users.map((assignee) => ({
        text: this.fullNamePipe.transform(assignee),
        value: this.fullNamePipe.transform(assignee),
      })),
    ),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  constructor(
    @Inject(INSPECTION_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly inspectionService: InspectionService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    public readonly store: CommonTasksStore,
    private readonly tasksAssignmentService: TasksAssignmentService,
    private readonly fullNamePipe: UserFullNamePipe,
  ) {
    inspectionService.setType(this.route.snapshot.paramMap.get('type') as InspectionType);
    this.isOnsiteInspection$.subscribe((value) => (this.isOnsiteInspection = value));
  }

  get extraOfficers(): UntypedFormArray {
    return this.form.get('extraOfficers') as UntypedFormArray;
  }

  addAnotherOfficer(): void {
    this.extraOfficers.push(createAnotherOfficer());
    this.extraOfficers.at(this.extraOfficers.length - 1).markAllAsTouched();
  }

  getDownloadUrl() {
    return this.inspectionService.getBaseFileDownloadUrl();
  }

  onSubmit() {
    this.inspectionService.payload$
      .pipe(
        first(),
        switchMap((payload) => this.inspectionTaskSave(payload as InspectionSaveRequestTaskActionPayload)),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../details-summary'], { relativeTo: this.route }));
  }

  private inspectionTaskSave(payload: InspectionSaveRequestTaskActionPayload, completeTask = false) {
    const details = {
      ...(this.isOnsiteInspection ? { date: this.form.get('date').value } : {}),
      officerNames: [
        this.form.get('officerName1').value,
        this.form.get('officerName2')?.value,
        ...(this.form.get('extraOfficers') as UntypedFormArray).controls.map((c) => c.get('extraOfficer')?.value),
      ].filter((officer) => officer !== null && officer !== undefined),
      files: this.form.get('files').value?.map((file) => file.uuid),
      regulatorExtraFiles: this.form.get('regulatorExtraFiles').value?.map((file) => file.uuid),
      additionalInformation: this.form.get('additionalInformation').value,
    };

    return this.inspectionService.postInspectionTaskSave(
      {
        installationInspection: {
          ...payload?.installationInspection,
          details,
        },
        installationInspectionSectionsCompleted: {
          ...payload?.installationInspectionSectionsCompleted,
          [detailsStatusKeySubmit]: completeTask,
        },
      },
      {
        ...(payload as InspectionSubmitRequestTaskPayload)?.inspectionAttachments,
        ...this.getDetailsAttachments(),
      },
    );
  }

  private getDetailsAttachments() {
    const allFiles = [...(this.form.get('files').value || []), ...(this.form.get('regulatorExtraFiles').value || [])];

    return allFiles?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {});
  }
}
