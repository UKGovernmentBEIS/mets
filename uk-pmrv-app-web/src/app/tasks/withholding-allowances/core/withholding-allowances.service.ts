import { Injectable } from '@angular/core';

import { first, switchMap, tap } from 'rxjs';

import { TasksHelperService } from '@tasks/shared/services/tasks-helper.service';

import {
  NonComplianceDailyPenaltyNoticeRequestTaskPayload,
  RequestTaskDTO,
  WithholdingOfAllowancesApplicationSubmitRequestTaskPayload,
  WithholdingOfAllowancesSaveApplicationRequestTaskActionPayload,
  WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload,
} from 'pmrv-api';

import { catchTaskReassignedBadRequest } from '../../../error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '../../../error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '../../../shared/errors/request-task-error';
import { StatusKey } from './withholding-allowances';

@Injectable({
  providedIn: 'root',
})
export class WithholdingAllowancesService extends TasksHelperService {
  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const url = this.getBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: this.attachments[id],
      })) ?? []
    );
  }

  private get attachments() {
    const payload = this.store.getValue().requestTaskItem.requestTask.payload;
    switch (payload.payloadType) {
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PAYLOAD':
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW_PAYLOAD':
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW_PAYLOAD':
        return (<NonComplianceDailyPenaltyNoticeRequestTaskPayload>payload).nonComplianceAttachments;

      default:
        throw Error('Unhandled task type: ' + payload.payloadType);
    }
  }

  postTaskSave(
    data:
      | WithholdingOfAllowancesApplicationSubmitRequestTaskPayload
      | WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload,
    statusValue?: boolean,
    statusKey?: StatusKey,
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        let requestTaskActionType;
        let payloadType;

        const requestTaskType: RequestTaskDTO['type'] = state.requestTaskItem.requestTask.type;

        switch (requestTaskType) {
          case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMIT':
            requestTaskActionType = 'WITHHOLDING_OF_ALLOWANCES_SAVE_APPLICATION';
            payloadType = 'WITHHOLDING_OF_ALLOWANCES_SAVE_APPLICATION_PAYLOAD';
            break;
          case 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_APPLICATION_SUBMIT':
            requestTaskActionType = 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_SAVE_APPLICATION';
            payloadType = 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_SAVE_APPLICATION_PAYLOAD';
            break;
        }

        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType: requestTaskActionType,
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: {
              payloadType: payloadType,
              ...data,
              sectionsCompleted: {
                ...(statusKey ? { [statusKey]: statusValue } : undefined),
              },
            } as WithholdingOfAllowancesSaveApplicationRequestTaskActionPayload,
          })
          .pipe(
            catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
              this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
            ),
            catchTaskReassignedBadRequest(() =>
              this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
            ),
            tap(() => {
              return this.store.setState({
                ...state,
                requestTaskItem: {
                  ...state.requestTaskItem,
                  requestTask: {
                    ...state.requestTaskItem.requestTask,
                    payload: {
                      ...state.requestTaskItem.requestTask.payload,
                      ...data,
                      sectionsCompleted: {
                        ...(statusKey ? { [statusKey]: statusValue } : undefined),
                      },
                    } as WithholdingOfAllowancesApplicationSubmitRequestTaskPayload,
                  },
                },
              });
            }),
          );
      }),
    );
  }
}
