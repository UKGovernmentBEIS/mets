import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { InspectionService } from '@tasks/inspection/core/inspection.service';
import { FollowUpItemComponent } from '@tasks/inspection/shared/follow-up-item/follow-up-item.component';
import { InspectionSubmitRequestTaskPayload, InspectionType } from '@tasks/inspection/shared/inspection';
import { InspectionTaskComponent } from '@tasks/inspection/shared/inspection-task/inspection-task.component';
import { followUpStatusKeySubmit } from '@tasks/inspection/shared/section-status';

import { isInstallationInspectionFollowUpSubmitCompleted } from '../submit.wizard';

@Component({
  selector: 'app-follow-up-actions',
  templateUrl: './follow-up-actions.component.html',
  standalone: true,
  imports: [InspectionTaskComponent, FollowUpItemComponent, SharedModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpActionsComponent {
  isCheckYourAnswersPage = this.route.snapshot.data.isCheckYourAnswersPage;
  headerTitle = this.isCheckYourAnswersPage ? 'Check your answers' : 'Follow-up actions needed';
  isEditable = toSignal(this.inspectionService.isEditable$);
  inspectionPayload$: Observable<InspectionSubmitRequestTaskPayload> = this.inspectionService.payload$;
  inspectionPayload: Signal<InspectionSubmitRequestTaskPayload> = toSignal(this.inspectionService.payload$);

  showConfirmButton$ = this.inspectionPayload$.pipe(
    map(
      (payload) =>
        !payload.installationInspectionSectionsCompleted[followUpStatusKeySubmit] &&
        this.isCheckYourAnswersPage &&
        isInstallationInspectionFollowUpSubmitCompleted(payload.installationInspection),
    ),
  );

  showContinueButton$ = this.inspectionPayload$.pipe(
    map((payload) => payload.installationInspection?.followUpActions?.length > 0 && !this.isCheckYourAnswersPage),
  );

  attachments$ = this.inspectionPayload$.pipe(
    map((payload) =>
      payload?.inspectionAttachments
        ? payload.installationInspection.followUpActions.map((val) => {
            return this.inspectionService.getOperatorDownloadUrlFiles(val.followUpActionAttachments);
          })
        : [],
    ),
  );

  files: Signal<AttachedFile[]> = computed(() => {
    const payload = this.inspectionPayload();
    return payload?.installationInspection?.followUpActionsOmissionFiles
      ? this.inspectionService.getOperatorDownloadUrlFiles(
          payload?.installationInspection?.followUpActionsOmissionFiles,
        )
      : [];
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly inspectionService: InspectionService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {
    inspectionService.setType(this.route.snapshot.paramMap.get('type') as InspectionType);
  }

  removeFollowUpItem(index: number) {
    this.router.navigate(['../', index, 'delete-follow-up-action'], { relativeTo: this.route });
  }

  addAnotherFollowUpAction() {
    this.inspectionPayload$
      .pipe(
        first(),
        map((payload) => payload.installationInspection?.followUpActions),
      )
      .subscribe((followUpActions) =>
        this.router.navigate([`../${+followUpActions?.length || 0}/add-follow-up-action`], { relativeTo: this.route }),
      );
  }

  onConfirm() {
    this.inspectionPayload$
      .pipe(
        first(),
        switchMap((payload) => {
          return this.inspectionService.postInspectionTaskSave({
            installationInspection: payload?.installationInspection,
            installationInspectionSectionsCompleted: {
              ...payload?.installationInspectionSectionsCompleted,
              [followUpStatusKeySubmit]: true,
            },
          });
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../'], { relativeTo: this.route }));
  }
}
