import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { InspectionService } from '@tasks/inspection/core/inspection.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { FollowUpAction, InstallationInspectionOperatorRespondRequestTaskPayload, RequestTaskDTO } from 'pmrv-api';

import { DetailsSubtaskHeaderPipe } from '../pipes/details-subtask-header.pipe';
import { DetailsSubtaskLinktextPipe } from '../pipes/details-subtask-linktext.pipe';
import {
  followUpActionSectionsCompleted,
  resolveSectionStatusRespond,
  resolveSectionStatusRespondSend,
} from '../section-status';

interface ViewModel {
  pageTitle: string;
  followUpActions: Array<FollowUpAction & { status: TaskItemStatus }>;
  expectedTaskType: RequestTaskDTO['type'];
  sendReportStatus: TaskItemStatus;
  sendReportLink: string;
  redirectReportLink: string;
  daysRemaining: number;
}

@Component({
  selector: 'app-task-list-response-container',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, DetailsSubtaskLinktextPipe, DetailsSubtaskHeaderPipe],
  templateUrl: './task-list-response-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskListResponseContainerComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.route.data,
    this.inspectionService.payload$ as Observable<InstallationInspectionOperatorRespondRequestTaskPayload>,
    this.inspectionService.requestTaskItem$,
  ]).pipe(
    map(([data, payload, requestTaskItem]) => {
      const taskType = requestTaskItem?.requestTask.type;
      const followUpActions = payload.installationInspection.followUpActions.map((action, index) => ({
        ...action,
        status: resolveSectionStatusRespond(payload, index),
      }));
      const sectionsCompleted = followUpActionSectionsCompleted(payload);

      return {
        pageTitle: data.pageTitle,
        followUpActions,
        expectedTaskType: taskType,
        sendReportStatus: resolveSectionStatusRespondSend(payload),
        sendReportLink: sectionsCompleted ? 'send-report' : null,
        redirectReportLink: './details-summary',
        daysRemaining: requestTaskItem.requestTask?.daysRemaining,
      };
    }),
  );

  constructor(
    public readonly inspectionService: InspectionService,
    readonly route: ActivatedRoute,
  ) {}
}
