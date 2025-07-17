import { Injectable } from '@angular/core';

import { first, map, Observable, switchMap, tap } from 'rxjs';

import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { TasksHelperService } from '@tasks/shared/services/tasks-helper.service';

import {
  NonComplianceApplicationSubmitRequestTaskPayload,
  NonComplianceCivilPenaltyRequestTaskPayload,
  NonComplianceCivilPenaltySaveApplicationRequestTaskActionPayload,
  NonComplianceDailyPenaltyNoticeRequestTaskPayload,
  NonComplianceDailyPenaltyNoticeSaveApplicationRequestTaskActionPayload,
  NonComplianceFinalDeterminationRequestTaskPayload,
  NonComplianceFinalDeterminationSaveApplicationRequestTaskActionPayload,
  NonComplianceNoticeOfIntentRequestTaskPayload,
  NonComplianceNoticeOfIntentSaveApplicationRequestTaskActionPayload,
  RequestTaskActionPayload,
} from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class NonComplianceService extends TasksHelperService {
  get accountId$() {
    return this.store.requestTaskItem$.pipe(map((task) => task.requestInfo.accountId));
  }

  saveNonCompliance(
    data: Partial<NonComplianceApplicationSubmitRequestTaskPayload>,
    sectionCompleted: boolean,
  ): Observable<any> {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        const payload = state.requestTaskItem.requestTask.payload as NonComplianceApplicationSubmitRequestTaskPayload;
        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType: 'NON_COMPLIANCE_SAVE_APPLICATION',
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: {
              ...payload,
              payloadType: 'NON_COMPLIANCE_SAVE_APPLICATION_PAYLOAD',
              ...data,
              sectionCompleted: sectionCompleted,
            } as RequestTaskActionPayload,
          })
          .pipe(
            catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
              this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
            ),
            catchTaskReassignedBadRequest(() =>
              this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
            ),
            tap(() =>
              this.store.setState({
                ...state,
                requestTaskItem: {
                  ...state.requestTaskItem,
                  requestTask: {
                    ...state.requestTaskItem.requestTask,
                    payload: {
                      ...state.requestTaskItem.requestTask.payload,
                      ...data,
                      sectionCompleted: sectionCompleted,
                    } as NonComplianceApplicationSubmitRequestTaskPayload,
                  },
                },
              }),
            ),
          );
      }),
    );
  }

  saveSectionStatus(sectionCompleted: boolean): Observable<any> {
    return this.saveNonCompliance(undefined, sectionCompleted);
  }

  submitNonCompliance(): Observable<any> {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType: 'NON_COMPLIANCE_SUBMIT_APPLICATION',
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: {
              payloadType: 'EMPTY_PAYLOAD',
            } as RequestTaskActionPayload,
          })
          .pipe(
            catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
              this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
            ),
            catchTaskReassignedBadRequest(() =>
              this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
            ),
          );
      }),
    );
  }

  private get attachments() {
    const payload = this.store.getValue().requestTaskItem.requestTask.payload;
    switch (payload.payloadType) {
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PAYLOAD':
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW_PAYLOAD':
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW_PAYLOAD':
        return (<NonComplianceDailyPenaltyNoticeRequestTaskPayload>payload).nonComplianceAttachments;
      case 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW_PAYLOAD':
      case 'NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW_PAYLOAD':
      case 'NON_COMPLIANCE_NOTICE_OF_INTENT_PAYLOAD':
        return (<NonComplianceNoticeOfIntentRequestTaskPayload>payload).nonComplianceAttachments;
      case 'NON_COMPLIANCE_CIVIL_PENALTY_PAYLOAD':
      case 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW_PAYLOAD':
      case 'NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW_PAYLOAD':
        return (<NonComplianceCivilPenaltyRequestTaskPayload>payload).nonComplianceAttachments;
      default:
        throw Error('Unhandled task type: ' + payload.payloadType);
    }
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const url = this.getBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: this.attachments[id],
      })) ?? []
    );
  }

  saveDailyPenaltyNotice(
    data: Partial<NonComplianceDailyPenaltyNoticeRequestTaskPayload>,
    dailyPenaltyCompleted: boolean,
    attachments?: { [key: string]: string },
  ): Observable<any> {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        const payload = state.requestTaskItem.requestTask
          .payload as NonComplianceDailyPenaltyNoticeSaveApplicationRequestTaskActionPayload;
        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType: 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SAVE_APPLICATION',
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: {
              dailyPenaltyNotice: payload?.dailyPenaltyNotice,
              comments: payload?.comments,
              payloadType: 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SAVE_APPLICATION_PAYLOAD',
              ...data,
              dailyPenaltyCompleted: dailyPenaltyCompleted,
            } as RequestTaskActionPayload,
          })
          .pipe(
            catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
              this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
            ),
            catchTaskReassignedBadRequest(() =>
              this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
            ),
            tap(() =>
              this.store.setState({
                ...state,
                requestTaskItem: {
                  ...state.requestTaskItem,
                  requestTask: {
                    ...state.requestTaskItem.requestTask,
                    payload: {
                      ...state.requestTaskItem.requestTask.payload,
                      ...data,
                      nonComplianceAttachments: {
                        ...(
                          state.requestTaskItem.requestTask.payload as NonComplianceDailyPenaltyNoticeRequestTaskPayload
                        )?.nonComplianceAttachments,
                        ...attachments,
                      },
                      dailyPenaltyCompleted: dailyPenaltyCompleted,
                    } as NonComplianceDailyPenaltyNoticeRequestTaskPayload,
                  },
                },
              }),
            ),
          );
      }),
    );
  }

  saveDailyPenaltyNoticeSectionStatus(dailyPenaltyCompleted: boolean): Observable<any> {
    return this.saveDailyPenaltyNotice(undefined, dailyPenaltyCompleted);
  }

  saveNoticeOfIntent(
    data: Partial<NonComplianceNoticeOfIntentRequestTaskPayload>,
    noticeOfIntentCompleted: boolean,
    attachments?: { [key: string]: string },
  ): Observable<any> {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        const payload = state.requestTaskItem.requestTask
          .payload as NonComplianceNoticeOfIntentSaveApplicationRequestTaskActionPayload;
        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SAVE_APPLICATION',
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: {
              noticeOfIntent: payload?.noticeOfIntent,
              comments: payload?.comments,
              payloadType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SAVE_APPLICATION_PAYLOAD',
              ...data,
              noticeOfIntentCompleted: noticeOfIntentCompleted,
            } as RequestTaskActionPayload,
          })
          .pipe(
            catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
              this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
            ),
            catchTaskReassignedBadRequest(() =>
              this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
            ),
            tap(() =>
              this.store.setState({
                ...state,
                requestTaskItem: {
                  ...state.requestTaskItem,
                  requestTask: {
                    ...state.requestTaskItem.requestTask,
                    payload: {
                      ...state.requestTaskItem.requestTask.payload,
                      ...data,
                      nonComplianceAttachments: {
                        ...(state.requestTaskItem.requestTask.payload as NonComplianceNoticeOfIntentRequestTaskPayload)
                          ?.nonComplianceAttachments,
                        ...attachments,
                      },
                      noticeOfIntentCompleted: noticeOfIntentCompleted,
                    } as NonComplianceNoticeOfIntentRequestTaskPayload,
                  },
                },
              }),
            ),
          );
      }),
    );
  }

  saveNoticeOfIntentSectionStatus(noticeOfIntentCompleted: boolean): Observable<any> {
    return this.saveNoticeOfIntent(undefined, noticeOfIntentCompleted);
  }

  saveCivilPenalty(
    data: Partial<NonComplianceCivilPenaltyRequestTaskPayload>,
    civilPenaltyCompleted: boolean,
    attachments?: { [key: string]: string },
  ): Observable<any> {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        const payload = state.requestTaskItem.requestTask
          .payload as NonComplianceCivilPenaltySaveApplicationRequestTaskActionPayload;
        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType: 'NON_COMPLIANCE_CIVIL_PENALTY_SAVE_APPLICATION',
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: {
              civilPenalty: payload?.civilPenalty,
              comments: payload?.comments,
              penaltyAmount: payload?.penaltyAmount,
              dueDate: payload?.dueDate,
              payloadType: 'NON_COMPLIANCE_CIVIL_PENALTY_SAVE_APPLICATION_PAYLOAD',
              ...data,
              civilPenaltyCompleted: civilPenaltyCompleted,
            } as RequestTaskActionPayload,
          })
          .pipe(
            catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
              this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
            ),
            catchTaskReassignedBadRequest(() =>
              this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
            ),
            tap(() =>
              this.store.setState({
                ...state,
                requestTaskItem: {
                  ...state.requestTaskItem,
                  requestTask: {
                    ...state.requestTaskItem.requestTask,
                    payload: {
                      ...state.requestTaskItem.requestTask.payload,
                      ...data,
                      nonComplianceAttachments: {
                        ...(state.requestTaskItem.requestTask.payload as NonComplianceCivilPenaltyRequestTaskPayload)
                          ?.nonComplianceAttachments,
                        ...attachments,
                      },
                      civilPenaltyCompleted: civilPenaltyCompleted,
                    } as NonComplianceCivilPenaltyRequestTaskPayload,
                  },
                },
              }),
            ),
          );
      }),
    );
  }

  saveCivilPenaltySectionStatus(dailyPenaltyCompleted: boolean): Observable<any> {
    return this.saveCivilPenalty(undefined, dailyPenaltyCompleted);
  }

  saveConclusion(
    data: Partial<NonComplianceFinalDeterminationRequestTaskPayload>,
    determinationCompleted: boolean,
  ): Observable<any> {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        const payload = state.requestTaskItem.requestTask
          .payload as NonComplianceFinalDeterminationSaveApplicationRequestTaskActionPayload;
        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType: 'NON_COMPLIANCE_FINAL_DETERMINATION_SAVE_APPLICATION',
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: {
              complianceRestored: payload?.complianceRestored,
              complianceRestoredDate: payload?.complianceRestoredDate,
              comments: payload?.comments,
              reissuePenalty: payload?.reissuePenalty,
              operatorPaid: payload?.operatorPaid,
              operatorPaidDate: payload?.operatorPaidDate,
              payloadType: 'NON_COMPLIANCE_FINAL_DETERMINATION_SAVE_APPLICATION_PAYLOAD',
              ...data,
              determinationCompleted: determinationCompleted,
            } as RequestTaskActionPayload,
          })
          .pipe(
            catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
              this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
            ),
            catchTaskReassignedBadRequest(() =>
              this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
            ),
            tap(() =>
              this.store.setState({
                ...state,
                requestTaskItem: {
                  ...state.requestTaskItem,
                  requestTask: {
                    ...state.requestTaskItem.requestTask,
                    payload: {
                      ...state.requestTaskItem.requestTask.payload,
                      ...data,
                      determinationCompleted: determinationCompleted,
                    } as NonComplianceFinalDeterminationRequestTaskPayload,
                  },
                },
              }),
            ),
          );
      }),
    );
  }

  saveConclusionSectionStatus(determinationCompleted: boolean): Observable<any> {
    return this.saveConclusion(undefined, determinationCompleted);
  }

  submitConclusion(): Observable<any> {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType: 'NON_COMPLIANCE_FINAL_DETERMINATION_SUBMIT_APPLICATION',
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: {
              payloadType: 'EMPTY_PAYLOAD',
            } as RequestTaskActionPayload,
          })
          .pipe(
            catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
              this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
            ),
            catchTaskReassignedBadRequest(() =>
              this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
            ),
          );
      }),
    );
  }
}
