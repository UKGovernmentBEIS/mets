import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap, take } from 'rxjs';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { BusinessErrorService } from '../../../../error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '../../../../error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '../../../../error/not-found-error';
import { BackLinkService } from '../../../../shared/back-link/back-link.service';
import { requestTaskReassignedError, taskNotFoundError } from '../../../../shared/errors/request-task-error';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { reviewGroupAllHeading } from '../../utils/review.global';

@Component({
  selector: 'app-return-for-amends',
  templateUrl: './return-for-amends.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnForAmendsComponent implements PendingRequest, OnInit {
  isTask$: Observable<boolean> = this.store.pipe(map((state) => state.isRequestTask));
  returnUrl = this.router.getCurrentNavigation()?.extras.state?.returnUrl ?? '/';

  decisionAmends$ = this.store.getSectionGroupsWithAmendDecision$();

  header = this.store.getReturnForAmendHeader();
  reviewGroupHeadings = reviewGroupAllHeading;
  requestType = this.store.getValue().requestType;
  isEditable = this.store.getValue().isEditable;

  constructor(
    private readonly tasksService: TasksService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly breadcrumbService: BreadcrumbService,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
    if (!this.isEditable) {
      this.breadcrumbService.cutLastBreadcrumbWithLinkandShow();
    } else {
      this.breadcrumbService.addToLastBreadcrumbAndShow('review');
    }
  }

  onSubmit(): void {
    this.route.paramMap
      .pipe(map((paramMap) => Number(paramMap.get('taskId'))))
      .pipe(
        first(),
        switchMap((taskId) =>
          this.tasksService.processRequestTaskAction({
            requestTaskActionType: this.store.getRequestTaskActionTypeReturnForAmend(),
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
      .subscribe(() => this.router.navigate(['./confirmation'], { relativeTo: this.route }));
  }
}
