import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { BehaviorSubject, first, map, switchMap, take } from 'rxjs';

import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { empHeaderTaskMap } from '@aviation/request-task/emp/shared/util/emp.util';
import { sendReturnForAmendsRequestTaskActionTypesMapper } from '@aviation/request-task/util';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { ItemDTO, RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { ReturnToLinkComponent } from '../return-to-link/return-to-link.component';

@Component({
  selector: 'app-return-for-amends-shared',
  templateUrl: './return-for-amends-shared.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReactiveFormsModule, ReturnToLinkComponent, RouterLinkWithHref],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnForAmendsSharedComponent implements PendingRequest {
  @Input() decisionAmends: { groupKey: string; data: any }[];
  @Input() reviewAttachments: { [key: string]: string };
  @Input() downloadBaseUrl: string;
  @Input() pageHeader: string;
  @Input() creationDate: string;
  @Input() requestTaskType: ItemDTO['taskType'];
  @Input() isAer = false;
  @Input() isCorsia = false;

  isSubmitted$ = new BehaviorSubject<boolean>(false);
  readonly empHeaderTaskMap = empHeaderTaskMap;
  readonly aerHeaderTaskMap = aerHeaderTaskMap;
  readonly aerReviewCorsiaHeaderTaskMap = aerReviewCorsiaHeaderTaskMap;

  constructor(
    private readonly tasksService: TasksService,
    private readonly route: ActivatedRoute,
    private readonly businessErrorService: BusinessErrorService,
    readonly pendingRequest: PendingRequestService,
    private readonly breadcrumbs: BreadcrumbService,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.route.paramMap
      .pipe(map((paramMap) => Number(paramMap.get('taskId'))))
      .pipe(
        first(),
        switchMap((taskId) =>
          this.tasksService.processRequestTaskAction({
            requestTaskActionType: sendReturnForAmendsRequestTaskActionTypesMapper[this.requestTaskType],
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: 'EMPTY_PAYLOAD',
            } as RequestTaskActionPayload,
          }),
        ),
        this.pendingRequest.trackRequest(),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        take(1),
      )
      .subscribe(() => {
        this.isSubmitted$.next(true);
        this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
      });
  }

  getDownloadUrlFiles(
    files: string[],
    attachments: { [key: string]: string },
    downloadBaseUrl: string,
  ): { downloadUrl: string; fileName: string }[] {
    return (
      files?.map((file) => ({
        fileName: attachments[file],
        downloadUrl: downloadBaseUrl + `/${file}`,
      })) ?? []
    );
  }
}
