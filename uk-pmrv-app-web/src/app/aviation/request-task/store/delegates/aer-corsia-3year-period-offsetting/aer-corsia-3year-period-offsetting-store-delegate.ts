import { Observable, tap } from 'rxjs';

import { sectionName } from '@aviation/request-task/aer-corsia-3year-period-offsetting/util/3year-period-offsetting.util';
import { getSaveRequestTaskActionTypeForRequestTaskType } from '@aviation/request-task/util';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import produce from 'immer';

import {
  AviationAerCorsia3YearPeriodOffsetting,
  AviationAerCorsia3YearPeriodOffsettingSaveRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

import { RequestTaskStore } from '../../request-task.store';
import { AerCorsia3YearOffsettingPayload } from '../../request-task.types';
import { RequestTaskStoreDelegate } from '../store-delegate';

export class AerCorsia3YearOffsettingStoreDelegate implements RequestTaskStoreDelegate {
  static readonly INITIAL_STATE: Partial<AviationAerCorsia3YearPeriodOffsetting> = {
    schemeYears: [],
    yearlyOffsettingData: null,
    totalYearlyOffsettingData: null,
    periodOffsettingRequirements: null,
    operatorHaveOffsettingRequirements: null,
  };

  constructor(
    private readonly store: RequestTaskStore,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  get payload(): AerCorsia3YearOffsettingPayload | null {
    return this.store.getState().requestTaskItem?.requestTask?.payload as AerCorsia3YearOffsettingPayload;
  }

  init() {
    return this;
  }

  saveOffsettingRequirement(
    offsettingRequirements: AviationAerCorsia3YearPeriodOffsetting,
    status: TaskItemStatus = 'complete',
  ): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const payloadToUpdate = produce(this.payload, (draft) => {
      if (draft.aviationAerCorsia3YearPeriodOffsetting) {
        draft.aviationAerCorsia3YearPeriodOffsetting = offsettingRequirements;
      }

      if (!draft.aviationAerCorsia3YearPeriodOffsettingSectionsCompleted) {
        draft.aviationAerCorsia3YearPeriodOffsettingSectionsCompleted = {};
      }
      draft.aviationAerCorsia3YearPeriodOffsettingSectionsCompleted[sectionName] = status === 'complete';
    });

    return this.sendProcessRequestTaskAction(requestTask, payloadToUpdate);
  }

  private sendProcessRequestTaskAction(
    requestTask: RequestTaskDTO,
    payloadToUpdate: AviationAerCorsia3YearPeriodOffsettingSaveRequestTaskActionPayload,
  ): Observable<any> {
    return this.store.tasksService
      .processRequestTaskAction(
        this.constructSaveActionReqBody(
          requestTask,
          payloadToUpdate,
          getSaveRequestTaskActionTypeForRequestTaskType(requestTask.type),
        ),
      )
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => {
          this.store.setPayload({
            ...payloadToUpdate,
          } as any);
        }),
      );
  }

  private constructSaveActionReqBody(
    requestTask: RequestTaskDTO,
    payloadToUpdate: AviationAerCorsia3YearPeriodOffsettingSaveRequestTaskActionPayload,
    requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
  ): RequestTaskActionProcessDTO {
    switch (requestTaskActionType) {
      case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SAVE_APPLICATION':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            aviationAerCorsia3YearPeriodOffsetting: payloadToUpdate.aviationAerCorsia3YearPeriodOffsetting,
            aviationAerCorsia3YearPeriodOffsettingSectionsCompleted:
              payloadToUpdate.aviationAerCorsia3YearPeriodOffsettingSectionsCompleted,
            payloadType: 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SAVE_PAYLOAD',
          },
        };
    }
  }
}
