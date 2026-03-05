import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { permitTypeMapLowercase } from '@permit-application/shared/utils/permit';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';

import { PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload, TasksService } from 'pmrv-api';

import { BusinessErrorService } from '../../error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '../../error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { PERMIT_AMEND_STATUS_PREFIX } from '../../permit-application/shared/types/amend.permit.type';
import { StatusKey } from '../../permit-application/shared/types/permit-task.type';
import { getAvailableSections } from '../../permit-application/shared/utils/available-sections';
import { resolvePermitSectionStatus } from '../../permit-application/shared/utils/permit-section-status-resolver';
import { PermitIssuanceStore } from '../store/permit-issuance.store';
@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements PendingRequest {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  allowSubmission$ = this.store.pipe(
    map((state) => getAvailableSections(state).map((section: StatusKey) => resolvePermitSectionStatus(state, section))),
    map((statuses) => statuses.every((status) => status === 'complete')),
  );

  permitTypeMapLowercase = permitTypeMapLowercase;
  permitType$ = this.store.pipe(map((state) => permitTypeMapLowercase?.[state.permitType]));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly store: PermitIssuanceStore,
    private readonly router: Router,
    private readonly tasksService: TasksService,
    private readonly route: ActivatedRoute,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  onSubmit(): void {
    combineLatest([this.store, this.taskId$])
      .pipe(
        first(),
        switchMap(([state, taskId]) =>
          this.tasksService.processRequestTaskAction({
            requestTaskActionType:
              state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT'
                ? 'PERMIT_ISSUANCE_SUBMIT_APPLICATION_AMEND'
                : 'PERMIT_ISSUANCE_SUBMIT_APPLICATION',
            requestTaskId: taskId,
            requestTaskActionPayload:
              state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT'
                ? ({
                    payloadType: 'PERMIT_ISSUANCE_SUBMIT_APPLICATION_AMEND_PAYLOAD',
                    permitSectionsCompleted: {
                      ...Object.keys(state.permitSectionsCompleted ?? [])
                        .filter((statusKey) => !statusKey.startsWith(PERMIT_AMEND_STATUS_PREFIX))
                        .reduce((res, key) => ((res[key] = state.permitSectionsCompleted[key]), res), {}),
                    },
                  } as PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload)
                : {
                    payloadType: 'EMPTY_PAYLOAD',
                  },
          }),
        ),
        this.pendingRequest.trackRequest(),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      )
      .subscribe(() => this.router.navigate(['../application-submitted'], { relativeTo: this.route }));
  }
}
