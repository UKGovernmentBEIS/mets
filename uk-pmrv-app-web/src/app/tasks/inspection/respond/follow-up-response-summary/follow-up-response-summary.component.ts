import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { InspectionService } from '@tasks/inspection/core/inspection.service';
import { FollowUpItemComponent } from '@tasks/inspection/shared/follow-up-item/follow-up-item.component';
import { InspectionType } from '@tasks/inspection/shared/inspection';
import { InspectionTaskComponent } from '@tasks/inspection/shared/inspection-task/inspection-task.component';

import { FollowUpAction, FollowUpActionResponse } from 'pmrv-api';

import { FollowUpRespondAbstractComponent } from '../follow-up-respond-abstract.component';

interface ViewModel {
  actionId: number;
  followUpAction: FollowUpAction;
  isEditable: boolean;
  attachments: AttachedFile[];
  followupActionsResponses: FollowUpActionResponse;
  responseAttachments: AttachedFile[];
  showConfirmButton: boolean;
}

@Component({
  selector: 'app-follow-up-response-summary',
  standalone: true,
  imports: [FollowUpItemComponent, InspectionTaskComponent, SharedModule, RouterLink],
  templateUrl: './follow-up-response-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpResponseSummaryComponent extends FollowUpRespondAbstractComponent {
  responseAttachments = toSignal(
    this.payload$.pipe(
      map((payload) => {
        const attachments = payload.followupActionsResponses[this.actionId].followUpActionResponseAttachments;

        return this.inspectionService.getOperatorDownloadUrlFiles(attachments);
      }),
    ),
  );

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const isEditable = this.isEditable();
    const attachments = this.attachments()[this.actionId] || [];
    const responseAttachments = this.responseAttachments() || ([] as Array<AttachedFile>);
    const showConfirmButton =
      !payload.installationInspectionOperatorRespondSectionsCompleted[this.actionId] && isEditable;

    return {
      actionId: this.actionId,
      followUpAction: payload.installationInspection.followUpActions[this.actionId],
      isEditable,
      attachments,
      followupActionsResponses: payload.followupActionsResponses[this.actionId],
      responseAttachments,
      showConfirmButton,
    };
  });

  constructor(
    protected readonly inspectionService: InspectionService,
    protected readonly route: ActivatedRoute,
    private readonly router: Router,
    readonly pendingRequest: PendingRequestService,
  ) {
    super(route, inspectionService);
    inspectionService.setType(this.route.snapshot.paramMap.get('type') as InspectionType);
  }

  onConfirm() {
    this.payload$
      .pipe(
        first(),
        switchMap((payload) => {
          return this.inspectionService.postInspectionForRespondTaskSaveOrSend({
            followupActionsResponses: payload.followupActionsResponses,
            installationInspectionOperatorRespondSectionsCompleted: {
              ...payload.installationInspectionOperatorRespondSectionsCompleted,
              [this.actionId]: true,
            },
          });
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../'], { relativeTo: this.route }));
  }
}
