import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  first,
  map,
  Observable,
  of,
  shareReplay,
  switchMap,
  take,
  withLatestFrom,
} from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BREADCRUMB_ITEMS, BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { AuthStore, selectUserProfile } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { GovukSelectOption } from 'govuk-components';

import {
  ItemDTO,
  RequestItemsService,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  TasksAssignmentService,
  TasksService,
} from 'pmrv-api';

import { requestTaskReassignedError, taskNotFoundError } from '../../errors/request-task-error';
import { UserFullNamePipe } from '../../pipes/user-full-name.pipe';
import { resolveRequestType } from '../../store-resolver/request-type.resolver';
import { StoreContextResolver } from '../../store-resolver/store-context.resolver';
import { PEER_REVIEW_FORM, peerReviewFormFactory } from './peer-review-form.provider';
import {
  resolvePeerReviewTaskType,
  resolveRequestTaskActionPayloadType,
  resolveRequestTaskActionType,
  resolveReturnToText,
  resolveWaitActionTypes,
} from './peer-review-type-resolver';

@Component({
  selector: 'app-peer-review-shared',
  templateUrl: './peer-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [peerReviewFormFactory, UserFullNamePipe],
})
export class PeerReviewComponent implements OnInit {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  private readonly requestType = resolveRequestType(this.location.path());

  private requestTaskType$ = this.storeResolver.getRequestTaskType(this.requestType);
  private requestTaskActionType$: Observable<RequestTaskActionProcessDTO['requestTaskActionType']> =
    this.requestTaskType$.pipe(map((type) => resolveRequestTaskActionType(type)));

  private payloadType$: Observable<RequestTaskActionPayload['payloadType']> = this.requestTaskType$.pipe(
    map((type) => resolveRequestTaskActionPayloadType(type)),
  );

  private waitActions$: Observable<ItemDTO['taskType'][]> = this.requestTaskType$.pipe(
    map((type) => resolveWaitActionTypes(type)),
  );

  isAviation = this.router.url.includes('/aviation/') ? '/aviation' : '';

  pendingRfi$: Observable<boolean> = combineLatest([
    this.waitActions$,
    this.storeResolver.getRequestId(this.requestType),
  ]).pipe(
    switchMap(([waitActions, requestId]) => {
      if (!waitActions.length) {
        return of(false);
      } else {
        return this.requestItemsService
          .getItemsByRequest(requestId)
          .pipe(map((itemResponse) => itemResponse.items.some((item) => waitActions.includes(item.taskType))));
      }
    }),
  );

  assignees$: Observable<GovukSelectOption<string>[]> = this.taskId$.pipe(
    withLatestFrom(this.requestTaskType$.pipe(map(resolvePeerReviewTaskType))),
    switchMap(([taskId, taskType]) =>
      combineLatest([
        this.authStore.pipe(selectUserProfile),
        this.tasksAssignmentService.getCandidateAssigneesByTaskType(taskId, taskType),
      ]),
    ),
    map(([userProfile, assignees]) =>
      assignees
        .filter((assignee) => assignee.id !== userProfile.id)
        .map((assignee) => ({ text: this.fullNamePipe.transform(assignee), value: assignee.id })),
    ),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  isFormSubmitted$ = new BehaviorSubject(false);
  assignee$: BehaviorSubject<string> = new BehaviorSubject(null);
  requestId$ = this.storeResolver.getRequestId(this.requestType);

  returnTo$ = this.storeResolver
    .getRequestTaskType(this.requestType)
    .pipe(map((requestTaskType) => resolveReturnToText(this.requestType, requestTaskType)));

  constructor(
    @Inject(PEER_REVIEW_FORM) readonly form: UntypedFormGroup,
    @Inject(BREADCRUMB_ITEMS) private readonly breadcrumbs$: BehaviorSubject<BreadcrumbItem[]>,
    private readonly pendingRequest: PendingRequestService,
    private readonly location: Location,
    private readonly route: ActivatedRoute,
    private readonly tasksAssignmentService: TasksAssignmentService,
    private readonly fullNamePipe: UserFullNamePipe,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly storeResolver: StoreContextResolver,
    private readonly requestItemsService: RequestItemsService,
    private readonly authStore: AuthStore,
    private readonly router: Router,
    private readonly breadcrumbs: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.requestTaskType$
      .pipe(
        take(1),
        withLatestFrom(this.breadcrumbs$),
        map(([requestTaskType, breadcrumbs]) => {
          if (
            ['PERMIT_VARIATION_APPLICATION_REVIEW', 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT'].includes(
              requestTaskType,
            )
          ) {
            const lastBreadcrumb = breadcrumbs[breadcrumbs.length - 1];
            lastBreadcrumb.link = [...lastBreadcrumb.link, 'review'];
          }
          return breadcrumbs;
        }),
      )
      .subscribe((breadcrumbs) => this.breadcrumbs$.next(breadcrumbs));
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.taskId$
        .pipe(
          first(),
          withLatestFrom(this.requestTaskActionType$, this.payloadType$),
          switchMap(([taskId, requestTaskActionType, payloadType]) =>
            this.tasksService.processRequestTaskAction({
              requestTaskActionType: requestTaskActionType,
              requestTaskId: taskId,
              requestTaskActionPayload: {
                payloadType: payloadType,
                peerReviewer: this.form.get('assignees').value,
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
          switchMap(() => this.assignees$),
          take(1),
          map((assignees) => assignees.find((assignee) => assignee.value === this.form.get('assignees').value)),
        )
        .subscribe((assignee) => {
          this.assignee$.next(assignee.text);
          this.isFormSubmitted$.next(true);
          this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
        });
    }
  }
}
