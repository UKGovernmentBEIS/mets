import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, filter, map, Observable, takeUntil } from 'rxjs';

import { BREADCRUMB_ITEMS, BreadcrumbItem } from '@core/navigation/breadcrumbs';

import { RequestActionsService, RequestItemsService } from 'pmrv-api';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { ReviewGroupDecisionStatus } from '../../../permit-application/review/types/review.permit.type';
import {
  ReviewSectionsContainerAbstractComponent,
} from '../../../permit-application/shared/review-sections/review-sections-container-abstract.component';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';
import { PermitVariationStore } from '../../store/permit-variation.store';
import { ReviewGroupStatusPermitVariationPipe } from '../review-group-status-permit-variation.pipe';
import {
  ReviewGroupStatusPermitVariationRegulatorLedPipe,
} from '../review-group-status-permit-variation-regulator-led.pipe';

@Component({
  selector: 'app-variation-review-sections-container',
  templateUrl: './review-sections-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ReviewSectionsContainerComponent extends ReviewSectionsContainerAbstractComponent implements OnInit {
  variationDetailsReviewStatus$: Observable<ReviewGroupDecisionStatus | TaskItemStatus> =
    this.store.getVariationDetailsReviewStatus$();

  readonly header$ = this.store.pipe(
    map((state) => {
      if (state.isRequestTask) {
        switch (state.requestTaskType) {
          case 'PERMIT_VARIATION_APPLICATION_REVIEW':
            return `${state.permitType} permit variation review`;
          case 'PERMIT_VARIATION_WAIT_FOR_AMENDS':
            return `${state.permitType} permit variation review`;
          case 'PERMIT_VARIATION_WAIT_FOR_REVIEW':
            return 'Make a change to your permit';
          case 'PERMIT_VARIATION_APPLICATION_PEER_REVIEW':
          case 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW':
            return `${state.permitType} permit variation peer review`;
          case 'PERMIT_VARIATION_WAIT_FOR_PEER_REVIEW':
          case 'PERMIT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
            return `${state.permitType} permit variation review`;
          case 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT':
            return `Make a change to the ${state.permitType} permit`;
          default:
            return state.permitType + ' permit determination';
        }
      } else {
        return this.store.isVariationRegulatorLedRequest
          ? `Make a change to the ${state.permitType} permit`
          : `${state.permitType} permit variation review`;
      }
    }),
  );

  readonly isPermitReviewSectionsVisible$ = this.store.pipe(
    filter(
      (state) =>
        [
          'PERMIT_VARIATION_APPLICATION_REVIEW',
          'PERMIT_VARIATION_APPLICATION_PEER_REVIEW',
          'PERMIT_VARIATION_WAIT_FOR_PEER_REVIEW',
          'PERMIT_VARIATION_WAIT_FOR_AMENDS',
          'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT',
          'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW',
          'PERMIT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW',
        ].includes(state.requestTaskType) ||
        ['PERMIT_VARIATION_APPLICATION_GRANTED', 'PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED'].includes(
          state.requestActionType,
        ),
    ),
  );

  constructor(
    protected readonly store: PermitVariationStore,
    protected readonly destroy$: DestroySubject,
    protected readonly route: ActivatedRoute,
    protected readonly router: Router,
    protected readonly requestItemsService: RequestItemsService,
    protected readonly requestActionsService: RequestActionsService,
    protected readonly backLinkService: BackLinkService,
    @Inject(BREADCRUMB_ITEMS) private readonly breadcrumbs$: BehaviorSubject<BreadcrumbItem[]>,
    private title: Title,
  ) {
    super(store, router, route, requestItemsService, requestActionsService, destroy$, backLinkService);
  }

  ngOnInit(): void {
    this.resolveLastBreadcrumb();
    this.statusResolverPipe = this.store.isVariationRegulatorLedRequest
      ? new ReviewGroupStatusPermitVariationRegulatorLedPipe(this.store)
      : new ReviewGroupStatusPermitVariationPipe(this.store);

    this.header$.pipe(takeUntil(this.destroy$)).subscribe((header) => this.title.setTitle(header));
  }

  private resolveLastBreadcrumb(): void {
    const lastBreadcrumb = this.breadcrumbs$.getValue()[this.breadcrumbs$.getValue().length - 1];
    lastBreadcrumb.link = [...lastBreadcrumb.link, 'review'];

    const breadcrumbs = [
      ...this.breadcrumbs$.getValue().slice(0, this.breadcrumbs$.getValue().length - 1),
      lastBreadcrumb,
    ];
    this.breadcrumbs$.next(breadcrumbs);
  }
}
