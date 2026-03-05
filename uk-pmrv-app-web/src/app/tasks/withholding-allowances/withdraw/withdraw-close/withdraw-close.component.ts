import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { BusinessErrorService } from '../../../../error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '../../../../error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '../../../../error/not-found-error';
import { BackLinkService } from '../../../../shared/back-link/back-link.service';
import { requestTaskReassignedError, taskNotFoundError } from '../../../../shared/errors/request-task-error';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { WITHHOLDING_ALLOWANCES_TASK_FORM } from '../../core/withholding-allowances';
import { WithholdingAllowancesService } from '../../core/withholding-allowances.service';
import { withdrawCloseReasonFormProvider } from './withdraw-close-form.provider';

@Component({
  selector: 'app-withdraw-close',
  templateUrl: './withdraw-close.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [withdrawCloseReasonFormProvider],
})
export class WithdrawCloseComponent implements OnInit, OnDestroy {
  isEditable$: Observable<boolean> = this.withholdingAllowancesService.isEditable$;

  constructor(
    @Inject(WITHHOLDING_ALLOWANCES_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
    private readonly withholdingAllowancesService: WithholdingAllowancesService,
    private readonly tasksService: TasksService,
    private readonly store: CommonTasksStore,
    private readonly businessErrorService: BusinessErrorService,
    readonly pendingRequest: PendingRequestService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit() {
    const nextRoute = `confirmation`;

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route });
    } else {
      this.store
        .pipe(
          first(),
          switchMap((state) => {
            return this.tasksService
              .processRequestTaskAction({
                requestTaskActionType: 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_CLOSE_APPLICATION',
                requestTaskId: state.requestTaskItem.requestTask.id,
                requestTaskActionPayload: {
                  payloadType: 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_CLOSE_APPLICATION_PAYLOAD',
                  closeJustification: {
                    reason: this.form.value.reason,
                    files: this.form.value.files?.map((file) => file.uuid),
                  },
                } as RequestTaskActionPayload,
              })
              .pipe(
                this.pendingRequest.trackRequest(),
                catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
                  this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
                ),
                catchTaskReassignedBadRequest(() =>
                  this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
                ),
              );
          }),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route, state: { notification: true } }));
    }
  }

  getBaseFileDownloadUrl() {
    return this.withholdingAllowancesService.getBaseFileDownloadUrl();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }
}
