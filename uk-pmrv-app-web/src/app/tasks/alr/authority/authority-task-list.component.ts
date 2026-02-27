import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRAuthorityResponseSubmitRequestTaskPayload, ALRRequestMetaData, RequestTaskDTO } from 'pmrv-api';

import { AlrService } from '../core';
import { allSectionsAuthorityComplete, resolveAuthoritySectionStatus, taskListTitle } from '../utils';

interface ViewModel {
  pageTitle: string;
  requestTaskType: RequestTaskDTO['type'];
  daysRemaining: number;
  redirectApplicationSubmittedLink: string;
  applicationSubmittedSectionStatus: TaskItemStatus;
  redirectAuthorityResponseLink: string;
  authorityResponseSectionStatus: TaskItemStatus;
  redirectUploadLink: string;
  uploadSectionStatus: TaskItemStatus;
  allowNotify: boolean;
}

@Component({
  selector: 'app-alr-authority-task-list',
  imports: [SharedModule, TaskSharedModule],
  templateUrl: './authority-task-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrAuthorityTaskListComponent {
  requestTaskType = this.alrService.requestTaskType;
  daysRemaining = this.alrService.daysRemaining;
  requestMetadata = this.alrService.requestMetadata;
  payload = this.alrService.payload as Signal<ALRAuthorityResponseSubmitRequestTaskPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const requestTaskType = this.requestTaskType();
    const requestMetadata = this.requestMetadata() as ALRRequestMetaData;

    return {
      pageTitle: taskListTitle(requestTaskType, requestMetadata?.year, requestMetadata?.isFinal),
      requestTaskType,
      daysRemaining: this.daysRemaining(),
      redirectApplicationSubmittedLink: 'application-submitted',
      applicationSubmittedSectionStatus: resolveAuthoritySectionStatus(payload, 'applicationSubmitted'),
      redirectAuthorityResponseLink: 'response/summary',
      authorityResponseSectionStatus: resolveAuthoritySectionStatus(payload, 'authorityResponse'),
      redirectUploadLink: 'upload-activity-level-report',
      uploadSectionStatus: resolveAuthoritySectionStatus(payload, 'upload'),
      allowNotify: allSectionsAuthorityComplete(payload),
    };
  });

  constructor(
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }
}
