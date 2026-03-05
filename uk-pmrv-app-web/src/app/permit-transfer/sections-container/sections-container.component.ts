import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable, of, switchMap, take, withLatestFrom } from 'rxjs';

import { TaskStatusPipe } from '@permit-application/shared/pipes/task-status.pipe';
import { SectionsContainerAbstractComponent } from '@permit-application/shared/sections/sections-container-abstract.component';
import { StatusKey } from '@permit-application/shared/types/permit-task.type';
import { getAvailableSections } from '@permit-application/shared/utils/available-sections';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { RequestActionsService, RequestItemsService } from 'pmrv-api';

import { PermitTransferStore } from '../store/permit-transfer.store';
import { transferDetailsStatus } from '../transfer-status';

@Component({
  selector: 'app-transfer-sections-container',
  templateUrl: './sections-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectionsContainerComponent extends SectionsContainerAbstractComponent implements OnInit {
  isTaskTypeAmendsSubmit$ = this.store.pipe(
    map((state) => state.requestTaskType),
    map((requestTaskType) => requestTaskType === 'PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT'),
  );

  transferDetailsConfirmationStatus$ = this.store.pipe(
    map((state) => (state.isRequestTask ? transferDetailsStatus(state) : 'complete')),
  );

  transferSubmissionStatus$: Observable<TaskItemStatus> = this.store.pipe(
    switchMap((state) => {
      if (!state.isRequestTask) {
        return of<TaskItemStatus>('complete');
      } else {
        return this.store.pipe(
          switchMap((state) =>
            combineLatest(
              getAvailableSections(state).map((section: StatusKey) => this.taskStatusPipe.transform(section)),
            ),
          ),
          withLatestFrom(this.transferDetailsConfirmationStatus$),
          map(([permitStatuses, transferDetailsStatus]) =>
            permitStatuses.every((status) => status === 'complete') && transferDetailsStatus === 'complete'
              ? 'not started'
              : 'cannot start yet',
          ),
        );
      }
    }),
  );

  isTransferSubmissionStatusCannotStartedYetOrComplete$ = this.transferSubmissionStatus$.pipe(
    map((status) => status === 'cannot start yet' || status === 'complete'),
  );

  constructor(
    protected readonly store: PermitTransferStore,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
    protected readonly requestItemsService: RequestItemsService,
    protected readonly requestActionsService: RequestActionsService,
    private readonly taskStatusPipe: TaskStatusPipe,
  ) {
    super(store, router, route, requestItemsService, requestActionsService);
  }

  ngOnInit(): void {
    this.init();
  }

  viewAmends(): void {
    this.requestActions$
      .pipe(
        map(
          (actions) =>
            actions.filter((action) => action.type === 'PERMIT_TRANSFER_B_APPLICATION_RETURNED_FOR_AMENDS')[0],
        ),
        map((action) => action.id),
        take(1),
      )
      .subscribe((actionId) =>
        this.router.navigate(['/permit-transfer', 'action', actionId, 'review', 'return-for-amends'], {
          state: this.navigationState,
        }),
      );
  }
}
