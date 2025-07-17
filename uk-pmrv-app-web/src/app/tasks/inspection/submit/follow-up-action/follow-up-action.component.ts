import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

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
import { followUpStatusKeySubmit } from '@tasks/inspection/shared/section-status';

import { GovukSelectOption } from 'govuk-components';

import { FollowUpAction } from 'pmrv-api';

import { followUpActionSubmitFormProvider } from './follow-up-action-form.provider';

@Component({
  selector: 'app-follow-up-action-submit',
  templateUrl: './follow-up-action.component.html',
  standalone: true,
  imports: [InspectionTaskComponent, SharedModule],
  providers: [followUpActionSubmitFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpActionSubmitComponent {
  reference = this.route.snapshot.paramMap.get('id');
  inspectionPayload$: Observable<InspectionSaveRequestTaskActionPayload> = this.inspectionService.payload$;

  followUpAction = this.route.snapshot.data.followUpAction as FollowUpAction;
  isEditable$ = this.inspectionService.isEditable$;
  today = new Date();
  followUpActionTypes: GovukSelectOption[] = [
    {
      text: 'Misstatement ',
      value: 'MISSTATEMENT',
    },
    {
      text: 'Non-conformity',
      value: 'NON_CONFORMITY',
    },
    {
      text: 'Non-compliance',
      value: 'NON_COMPLIANCE',
    },
    {
      text: 'Recommended improvement',
      value: 'RECOMMENDED_IMPROVEMENT',
    },
    {
      text: 'Unresolved issue from previous audit',
      value: 'UNRESOLVED_ISSUE_FROM_PREVIOUS_AUDIT',
    },
  ];

  constructor(
    @Inject(INSPECTION_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly inspectionService: InspectionService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {
    inspectionService.setType(this.route.snapshot.paramMap.get('type') as InspectionType);
  }

  onSubmit() {
    const nextRoute = `../../follow-up-actions`;

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      this.inspectionPayload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.inspectionService.postInspectionTaskSave(
              {
                installationInspection: {
                  ...payload.installationInspection,
                  followUpActions: this.getFollowUpActionData(payload),
                },
                installationInspectionSectionsCompleted: {
                  ...payload?.installationInspectionSectionsCompleted,
                  [followUpStatusKeySubmit]: false,
                },
              },
              {
                ...(payload as InspectionSubmitRequestTaskPayload)?.inspectionAttachments,
                ...this.getAuditAttachments(),
              },
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  getDownloadUrl() {
    return this.inspectionService.getBaseFileDownloadUrl();
  }

  private getFormData(payload: InspectionSaveRequestTaskActionPayload): FollowUpAction {
    return {
      ...payload?.installationInspection?.followUpActions?.[this.reference],
      explanation: this.form.get('explanation').value,
      followUpActionType: this.form.get('followUpActionType').value,
      followUpActionAttachments: this.form.get('followUpActionAttachments').value?.map((file) => file.uuid),
    };
  }

  private getFollowUpActionData(payload: InspectionSaveRequestTaskActionPayload): FollowUpAction[] {
    let result = [];
    if (!payload?.installationInspection?.followUpActions) {
      payload = { ...payload, installationInspection: { ...payload.installationInspection, followUpActions: [] } };
    }

    result =
      +this.reference >= payload?.installationInspection?.followUpActions?.length
        ? [...payload.installationInspection.followUpActions, this.getFormData(payload)]
        : payload?.installationInspection?.followUpActions?.map((item, index) =>
            index === +this.reference ? this.getFormData(payload) : item,
          );

    return result;
  }

  private getAuditAttachments() {
    return this.form
      .get('followUpActionAttachments')
      .value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {});
  }
}
