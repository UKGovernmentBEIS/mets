import { ChangeDetectionStrategy, Component, computed, Inject, OnInit, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { InspectionService } from '@tasks/inspection/core/inspection.service';
import { FollowUpItemComponent } from '@tasks/inspection/shared/follow-up-item/follow-up-item.component';
import { INSPECTION_TASK_FORM, InspectionType } from '@tasks/inspection/shared/inspection';
import { InspectionTaskComponent } from '@tasks/inspection/shared/inspection-task/inspection-task.component';

import { FollowUpAction, FollowUpActionResponse } from 'pmrv-api';

import { FollowUpRespondAbstractComponent } from '../follow-up-respond-abstract.component';
import { followUpActionRespondFormProvider } from './follow-up-action-form.provider';

interface ViewModel {
  actionId: number;
  followUpAction: FollowUpAction;
  isEditable: boolean;
  attachments: AttachedFile[];
}

@Component({
  selector: 'app-follow-up-action-respond',
  standalone: true,
  imports: [FollowUpItemComponent, InspectionTaskComponent, SharedModule],
  providers: [followUpActionRespondFormProvider, DestroySubject],
  templateUrl: './follow-up-action.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpActionRespondComponent extends FollowUpRespondAbstractComponent implements OnInit {
  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const isEditable = this.isEditable();
    const attachments = this.attachments()[this.actionId] || [];

    return {
      actionId: this.actionId,
      followUpAction: payload.installationInspection.followUpActions[this.actionId],
      isEditable,
      attachments,
    };
  });

  constructor(
    @Inject(INSPECTION_TASK_FORM) readonly form: UntypedFormGroup,
    protected readonly inspectionService: InspectionService,
    protected readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly destroy$: DestroySubject,
    readonly pendingRequest: PendingRequestService,
  ) {
    super(route, inspectionService);
    inspectionService.setType(this.route.snapshot.paramMap.get('type') as InspectionType);
  }

  ngOnInit(): void {
    this.form
      .get('completed')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((isCompleted: boolean) => {
        let newValues: { [key: string]: any };

        if (isCompleted) {
          newValues = {
            explanationFalse: null,
            followUpActionResponseAttachmentsFalse: [],
          };
        } else {
          newValues = {
            explanationTrue: null,
            followUpActionResponseAttachmentsTrue: [],
          };
        }
        this.form.patchValue(newValues);
      });
  }

  getDownloadUrl() {
    return this.inspectionService.getBaseFileDownloadUrl();
  }

  onSubmit() {
    const nextRoute = `../`;

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      const {
        explanationTrue,
        explanationFalse,
        followUpActionResponseAttachmentsTrue,
        followUpActionResponseAttachmentsFalse,
        ...values
      } = this.form.value;

      const followUpActionResponseAttachments = followUpActionResponseAttachmentsTrue
        ? followUpActionResponseAttachmentsTrue.map((file) => file.uuid)
        : followUpActionResponseAttachmentsFalse?.map((file) => file.uuid);

      const saveValues: FollowUpActionResponse = {
        ...values,
        explanation: explanationTrue || explanationFalse,
        followUpActionResponseAttachments,
      };

      this.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.inspectionService.postInspectionForRespondTaskSaveOrSend(
              {
                followupActionsResponses: {
                  ...payload.followupActionsResponses,
                  [this.actionId]: saveValues,
                },
                installationInspectionOperatorRespondSectionsCompleted: {
                  ...payload.installationInspectionOperatorRespondSectionsCompleted,
                  [this.actionId]: false,
                },
              },
              {
                ...payload?.inspectionAttachments,
                ...this.getRespondAttachments(),
              },
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  private getRespondAttachments() {
    const attachments =
      this.form.value.followUpActionResponseAttachmentsTrue || this.form.value.followUpActionResponseAttachmentsFalse;

    return attachments?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {});
  }
}
