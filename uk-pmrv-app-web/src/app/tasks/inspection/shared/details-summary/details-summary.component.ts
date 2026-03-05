import { ChangeDetectionStrategy, Component, computed, Inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormGroup, ValidationErrors } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { first, map, Observable, switchMap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { UserFullNamePipe } from '@shared/pipes/user-full-name.pipe';
import { SharedModule } from '@shared/shared.module';
import { InspectionService } from '@tasks/inspection/core/inspection.service';
import {
  INSPECTION_TASK_FORM,
  InspectionSubmitRequestTaskPayload,
  InspectionType,
} from '@tasks/inspection/shared/inspection';
import { DetailsSubtaskHeaderPipe } from '@tasks/inspection/shared/pipes/details-subtask-header.pipe';
import { DetailsSubtaskLinktextPipe } from '@tasks/inspection/shared/pipes/details-subtask-linktext.pipe';
import { detailsStatusKeySubmit } from '@tasks/inspection/shared/section-status';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { TasksAssignmentService } from 'pmrv-api';

import { detailsFormProvider } from '../../submit/details/details-form.provider';
import { isInstallationInspectionDetailsSubmitCompleted, onSiteInspectionDateValid } from '../../submit/submit.wizard';

@Component({
  selector: 'app-details-summary',
  standalone: true,
  imports: [SharedModule, DetailsSubtaskHeaderPipe, DetailsSubtaskLinktextPipe, RouterLink],
  providers: [detailsFormProvider, UserFullNamePipe],
  templateUrl: './details-summary.component.html',
  styleUrl: '../../shared/css/form-errors.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsSummaryComponent {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  requestTasktype$ = this.store.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem.requestTask.type));

  inspectionPayload$: Observable<InspectionSubmitRequestTaskPayload> = this.inspectionService.payload$;

  isOnsiteInspection$ = this.requestTasktype$.pipe(
    map(
      (type) =>
        type === 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT' ||
        type === 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW' ||
        type === 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS',
    ),
  );

  isSubmit$ = this.requestTasktype$.pipe(
    map(
      (type) =>
        type === 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT' ||
        type === 'INSTALLATION_AUDIT_APPLICATION_SUBMIT',
    ),
  );

  isOperatorRespond$ = this.requestTasktype$.pipe(
    map(
      (type) =>
        type === 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS' ||
        type === 'INSTALLATION_AUDIT_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS',
    ),
  );

  isEditable = toSignal(this.inspectionService.isEditable$);
  isSubmit = toSignal(this.isSubmit$);
  isEditableDetails = computed(() => this.isEditable() && this.isSubmit());

  showConfirmButton$ = this.inspectionPayload$.pipe(
    withLatestFrom(this.isOnsiteInspection$),
    map(([payload, isOnsiteInspection]) => {
      if (isOnsiteInspection) {
        return (
          !payload.installationInspectionSectionsCompleted?.[detailsStatusKeySubmit] &&
          onSiteInspectionDateValid(payload.installationInspection?.details?.date)
        );
      } else {
        return !payload.installationInspectionSectionsCompleted?.[detailsStatusKeySubmit];
      }
    }),
  );

  visibleFiles$ = this.inspectionPayload$.pipe(
    map((payload) =>
      payload?.inspectionAttachments
        ? this.inspectionService.getOperatorDownloadUrlFiles(payload.installationInspection.details?.files)
        : [],
    ),
  );
  notVisibleFiles$ = this.inspectionPayload$.pipe(
    map((payload) =>
      payload?.inspectionAttachments
        ? this.inspectionService.getOperatorDownloadUrlFiles(
            payload.installationInspection.details?.regulatorExtraFiles,
          )
        : [],
    ),
  );

  constructor(
    @Inject(INSPECTION_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly inspectionService: InspectionService,
    private readonly tasksAssignmentService: TasksAssignmentService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    public readonly store: CommonTasksStore,
    private readonly taskTypeToBreadcrumbPipe: TaskTypeToBreadcrumbPipe,
    private readonly fullNamePipe: UserFullNamePipe,
  ) {
    inspectionService.setType(this.route.snapshot.paramMap.get('type') as InspectionType);
  }

  inspectionPayload: Signal<InspectionSubmitRequestTaskPayload> = toSignal(this.inspectionService.payload$);

  errors: Signal<ValidationErrors> = computed(() => {
    const installationInspection = this.inspectionPayload().installationInspection;
    const completedAndValid =
      isInstallationInspectionDetailsSubmitCompleted(installationInspection) &&
      onSiteInspectionDateValid(installationInspection?.details?.date);

    return completedAndValid
      ? null
      : Object.values(this.form.controls)
          .filter((control) => control?.errors)
          .reduce((errors, control) => {
            return {
              ...errors,
              ...control.errors,
            };
          }, {});
  });

  returnLinkText: Observable<string> = this.inspectionService.requestTaskItem$.pipe(
    map((requestTask) => this.taskTypeToBreadcrumbPipe.transform(requestTask?.requestTask?.type)),
  );

  onConfirm() {
    this.inspectionPayload$
      .pipe(
        first(),
        switchMap((payload) => {
          return this.inspectionService.postInspectionTaskSave({
            installationInspection: payload?.installationInspection,
            installationInspectionSectionsCompleted: {
              ...payload?.installationInspectionSectionsCompleted,
              [detailsStatusKeySubmit]: true,
            },
          });
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../'], { relativeTo: this.route }));
  }
}
