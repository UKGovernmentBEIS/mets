import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable, of } from 'rxjs';

import { isAllSectionsApproved } from '@aviation/request-task/aer/shared/util/aer.util';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { hasRelatedViewActions } from '@shared/components/related-actions/request-task-allowed-actions.map';
import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';
import { TaskSection } from '@shared/task-list/task-list.interface';

import {
  DecisionNotification,
  ItemDTO,
  RequestActionInfoDTO,
  RequestInfoDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
} from 'pmrv-api';

import { requestTaskQuery, RequestTaskStore } from '../../store';
import {
  getMessageForTaskType,
  getPreviewDocumentsInfo,
  getRequestTaskHeaderForTaskType,
  getSectionsForTaskType,
  isAnySectionForAmends,
  isDeterminationCompleted as isDeterminationCompletedFn,
  notifyOperatorRequestTaskActionTypes,
  notifyOperatorUrlMapper,
  peerReviewRequestTaskActionTypes,
  returnForAmendsRequestTaskActionTypes,
  returnForAmendsUrlMapper,
  reviewButtonsVisible,
  sendForPeerReviewUrlMapper,
  startPeerReviewRequestTaskActionTypes,
} from '../../util';

interface ViewModel {
  header: string;
  showDeadlineMessage: boolean;
  requestTask: RequestTaskDTO;
  timeline: RequestActionInfoDTO[];
  relatedTasks: ItemDTO[];
  sections: TaskSection<any>[];
  isAssignable$: Observable<boolean>;
  taskId$: Observable<number>;
  hasRelatedActions$: Observable<boolean>;
  relatedActions$: Observable<RequestTaskItemDTO['allowedRequestTaskActions']>;
  requestInfo$: Observable<RequestInfoDTO>;
  requestType: RequestInfoDTO['type'];
  showReviewButtons: boolean;
  showNotifyOperator: boolean;
  isAnySectionForAmends: boolean;
  showReturnForAmends: boolean;
  showSendForPeerReview: boolean;
  showStartPeerReview: boolean;
  awaitForVerifierMsg: string;
  isCompleteReportDisplayed: boolean;
  decisionNotification: DecisionNotification;
  previewDocuments: DocumentFilenameAndDocumentType[];
}

@Component({
  selector: 'app-request-task-page',
  templateUrl: './request-task-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
  styles: `
    :host ::ng-deep .app-task-list {
      list-style-type: none;
      padding-left: 0;
    }
  `,
})
export class RequestTaskPageComponent implements OnInit {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTask),
    this.store.pipe(requestTaskQuery.selectTimeline),
    this.store.pipe(requestTaskQuery.selectRelatedTasks),
    this.store.pipe(requestTaskQuery.selectUserAssignCapable),
    this.store.pipe(requestTaskQuery.selectRelatedActions),
    this.store.pipe(requestTaskQuery.selectRequestTaskPayload),
    this.store.pipe(requestTaskQuery.selectRequestInfo),
  ]).pipe(
    map(([requestTask, timeline, relatedTasks, capableToAssign, relatedActions, payload, requestInfo]) => {
      const isDeterminationCompleted = isDeterminationCompletedFn(requestTask.type, requestInfo.type, payload);

      return {
        header: getRequestTaskHeaderForTaskType(requestTask.type, requestInfo.requestMetadata),
        showDeadlineMessage: true,
        requestTask,
        timeline,
        relatedTasks,
        sections: getSectionsForTaskType(requestTask.type, requestInfo.type, payload, relatedActions),
        isAssignable$: of(requestTask.assignable),
        taskId$: of(requestTask.id),
        hasRelatedActions$: of(
          relatedActions?.length > 0 ||
            (capableToAssign && requestTask.assignable) ||
            hasRelatedViewActions(requestInfo.type),
        ),
        relatedActions$: of(relatedActions),
        requestInfo$: of(requestInfo),
        requestType: requestInfo.type,
        showReviewButtons: reviewButtonsVisible.includes(requestTask.type),
        showNotifyOperator: relatedActions.some(
          (action) => isDeterminationCompleted && notifyOperatorRequestTaskActionTypes.includes(action),
        ),
        isAnySectionForAmends: isAnySectionForAmends(payload, requestTask.type),
        showReturnForAmends: relatedActions.some((action) => returnForAmendsRequestTaskActionTypes.includes(action)),
        showSendForPeerReview:
          isDeterminationCompleted &&
          relatedActions.some((action) => peerReviewRequestTaskActionTypes.includes(action)),
        showStartPeerReview: relatedActions.some((action) => startPeerReviewRequestTaskActionTypes.includes(action)),
        awaitForVerifierMsg: getMessageForTaskType(requestTask.type),
        isCompleteReportDisplayed: isAllSectionsApproved(payload, requestTask.type),
        decisionNotification: { signatory: requestTask.assigneeUserId },
        previewDocuments: getPreviewDocumentsInfo(requestTask.type, payload),
      } as ViewModel;
    }),
  );

  constructor(
    private readonly store: RequestTaskStore,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
    private titleService: Title,
  ) {}

  ngOnInit(): void {
    const routerState = this.router.routerState.snapshot;
    const state = this.store.getState();
    if (
      state.requestTaskItem.requestInfo.type === 'AVIATION_ACCOUNT_CLOSURE' &&
      !routerState.url.endsWith('/account-closure')
    ) {
      this.router.navigate(['account-closure'], { relativeTo: this.route });
    }

    this.store
      .pipe(
        requestTaskQuery.selectRequestTaskItem,
        map((rti) => getRequestTaskHeaderForTaskType(rti.requestTask.type, rti.requestInfo.requestMetadata)),
      )
      .subscribe((title) => this.titleService.setTitle(title));
  }

  onSubmit() {
    // Unnecessary
  }

  notifyOperator(requestTaskType: RequestTaskDTO['type']): void {
    this.router.navigate(notifyOperatorUrlMapper[requestTaskType], { relativeTo: this.route });
  }

  sendPeerReview(requestTaskType: RequestTaskDTO['type']): void {
    this.router.navigate(sendForPeerReviewUrlMapper[requestTaskType], { relativeTo: this.route });
  }

  startPeerReview() {
    this.router.navigate(['peer-review-decision'], { relativeTo: this.route });
  }

  returnForAmends(requestTaskType: RequestTaskDTO['type']): void {
    this.router.navigate(returnForAmendsUrlMapper[requestTaskType], { relativeTo: this.route, replaceUrl: true });
  }

  completeReport(): void {
    this.router.navigate(['complete-report'], { relativeTo: this.route });
  }
}
