import { Observable, tap } from 'rxjs';

import { sectionName } from '@aviation/request-task/aer-corsia-annual-offsetting/util/annual-offsetting.util';
import { getSaveRequestTaskActionTypeForRequestTaskType } from '@aviation/request-task/util';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import produce from 'immer';

import {
  AviationAerCorsiaAnnualOffsetting,
  AviationAerCorsiaAnnualOffsettingSaveRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

import { RequestTaskStore } from '../../request-task.store';
import { AerCorsiaAnnualOffsettingPayload } from '../../request-task.types';
import { RequestTaskStoreDelegate } from '../store-delegate';

export class AerCorsiaAnnualOffsettingStoreDelegate implements RequestTaskStoreDelegate {
  static readonly INITIAL_STATE: Partial<AviationAerCorsiaAnnualOffsetting> = {
    schemeYear: null,
    totalChapter: null,
    sectorGrowth: null,
    calculatedAnnualOffsetting: null,
  };

  constructor(
    private readonly store: RequestTaskStore,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  get payload(): AerCorsiaAnnualOffsettingPayload | null {
    return this.store.getState().requestTaskItem?.requestTask?.payload as AerCorsiaAnnualOffsettingPayload;
  }

  init() {
    return this;
  }

  saveOffsettingRequirement(
    offsettingRequirements: AviationAerCorsiaAnnualOffsetting,
    status: TaskItemStatus = 'complete',
  ): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const payloadToUpdate = produce(this.payload, (draft) => {
      if (draft.aviationAerCorsiaAnnualOffsetting) {
        draft.aviationAerCorsiaAnnualOffsetting = offsettingRequirements;
      }
      draft.aviationAerCorsiaAnnualOffsettingSectionsCompleted[sectionName] = status === 'complete';
    });

    return this.sendProcessRequestTaskAction(requestTask, payloadToUpdate);
  }

  private sendProcessRequestTaskAction(
    requestTask: RequestTaskDTO,
    payloadToUpdate: AviationAerCorsiaAnnualOffsettingSaveRequestTaskActionPayload,
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
    payloadToUpdate: AviationAerCorsiaAnnualOffsettingSaveRequestTaskActionPayload,
    requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
  ): RequestTaskActionProcessDTO {
    switch (requestTaskActionType) {
      case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SAVE_APPLICATION':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            aviationAerCorsiaAnnualOffsetting: payloadToUpdate.aviationAerCorsiaAnnualOffsetting,
            aviationAerCorsiaAnnualOffsettingSectionsCompleted:
              payloadToUpdate.aviationAerCorsiaAnnualOffsettingSectionsCompleted,
            payloadType: 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SAVE_PAYLOAD',
          },
        };
    }
  }
}
