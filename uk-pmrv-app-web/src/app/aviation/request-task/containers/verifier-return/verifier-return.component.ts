import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, switchMap } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { RequestTaskStore } from '@aviation/request-task/store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { SharedModule } from '@shared/shared.module';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { AER_VERIFIER_RETURN_FORM, aerVerifierReturnProvider } from './verifier-return-form.provider';

@Component({
  selector: 'app-verifier-return',
  templateUrl: './verifier-return.component.html',
  standalone: true,
  imports: [SharedModule],
  providers: [aerVerifierReturnProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class VerifierReturnComponent {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  isCorsia$ = this.store.pipe(aerQuery.selectIsCorsia);
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);

  protected isVerifierReturnSubmitted$ = new BehaviorSubject<boolean>(false);

  constructor(
    @Inject(AER_VERIFIER_RETURN_FORM) public form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
    private store: RequestTaskStore,
  ) {}

  onConfirmReturn() {
    if (this.form.valid) {
      combineLatest([this.taskId$, this.isCorsia$])
        .pipe(
          first(),
          switchMap(([taskId, isCorsia]) =>
            this.tasksService.processRequestTaskAction({
              requestTaskActionType: isCorsia
                ? 'AVIATION_AER_CORSIA_VERIFICATION_RETURN_TO_OPERATOR'
                : 'AVIATION_AER_UKETS_VERIFICATION_RETURN_TO_OPERATOR',
              requestTaskId: taskId,
              requestTaskActionPayload: {
                payloadType: isCorsia
                  ? 'AVIATION_AER_CORSIA_VERIFICATION_RETURN_TO_OPERATOR_PAYLOAD'
                  : 'AVIATION_AER_UKETS_VERIFICATION_RETURN_TO_OPERATOR_PAYLOAD',
                changesRequired: this.form.value.changesRequired,
              } as RequestTaskActionPayload,
            }),
          ),
          catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
            this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
          ),
          catchTaskReassignedBadRequest(() =>
            this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.isVerifierReturnSubmitted$.next(true));
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }
}
