import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, of, switchMap, take, withLatestFrom } from 'rxjs';

import { permitTypeMapLowercase } from '@permit-application/shared/utils/permit';

import { PermitVariationReviewDecision, RequestActionsService, RequestItemsService } from 'pmrv-api';

import { TaskStatusPipe } from '../../permit-application/shared/pipes/task-status.pipe';
import { SectionsContainerAbstractComponent } from '../../permit-application/shared/sections/sections-container-abstract.component';
import { StatusKey } from '../../permit-application/shared/types/permit-task.type';
import { getAvailableSections } from '../../permit-application/shared/utils/available-sections';
import { BackLinkService } from '../../shared/back-link/back-link.service';
import { BreadcrumbService } from '../../shared/breadcrumbs/breadcrumb.service';
import { TaskItemStatus } from '../../shared/task-list/task-list.interface';
import { PermitVariationStore } from '../store/permit-variation.store';
import { variationDetailsStatus } from '../variation-status';

@Component({
  selector: 'app-sections-container',
  templateUrl: './sections-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectionsContainerComponent extends SectionsContainerAbstractComponent implements OnInit {
  isTaskTypeAmendsSubmit$ = this.store.pipe(
    map((state) => state.requestTaskType),
    map((requestTaskType) => requestTaskType === 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT'),
  );
  permitTypeMapLowercase = permitTypeMapLowercase;
  header$: Observable<string> = this.store.pipe(
    first(),
    map(
      (state) =>
        `Make a change to ${this.store.isVariationRegulatorLedRequest ? 'the' : 'your'} ${permitTypeMapLowercase?.[state.permitType]} permit`,
    ),
  );

  isOperatorLed = !this.store.isVariationRegulatorLedRequest;

  aboutVariationAmendNeeded$: Observable<boolean> = this.store.pipe(
    first(),
    map(
      (state) =>
        state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT' &&
        (state.permitVariationDetailsReviewDecision as PermitVariationReviewDecision)?.type ===
          'OPERATOR_AMENDS_NEEDED',
    ),
  );

  aboutVariationAmendStatus$ = this.store.pipe(
    map((state) => (state.isRequestTask && !state.permitVariationDetailsAmendCompleted ? 'not started' : 'complete')),
  );

  aboutVariationStatus$ = this.store.pipe(
    map((state) => (state.isRequestTask ? variationDetailsStatus(state) : 'complete')),
  );

  variationSubmissionStatus$: Observable<TaskItemStatus> = this.store.pipe(
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
          withLatestFrom(this.aboutVariationStatus$, this.aboutVariationAmendNeeded$, this.aboutVariationAmendStatus$),
          map(([permitStatuses, variationDetailsStatus, aboutVariationAmendNeeded, aboutVariationAmendStatus]) =>
            permitStatuses.every((status) => status === 'complete') &&
            variationDetailsStatus === 'complete' &&
            (!aboutVariationAmendNeeded || aboutVariationAmendStatus === 'complete')
              ? 'not started'
              : 'cannot start yet',
          ),
        );
      }
    }),
  );

  isVariationSubmissionStatusCannotStartedYetOrComplete$ = this.variationSubmissionStatus$.pipe(
    map((status) => status === 'cannot start yet' || status === 'complete'),
  );

  constructor(
    protected readonly store: PermitVariationStore,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
    protected readonly requestItemsService: RequestItemsService,
    protected readonly requestActionsService: RequestActionsService,
    protected readonly backLinkService: BackLinkService,
    protected readonly breadcrumbService: BreadcrumbService,

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
            actions.filter((action) => action.type === 'PERMIT_VARIATION_APPLICATION_RETURNED_FOR_AMENDS')[0],
        ),
        map((action) => action.id),
        take(1),
      )
      .subscribe((actionId) =>
        this.router.navigate(['/permit-variation', 'action', actionId, 'review', 'return-for-amends'], {
          state: this.navigationState,
        }),
      );
  }
}
