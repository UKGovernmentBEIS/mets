import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, map, Observable, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { ReviewGroupDecisionStatus } from '@permit-application/review/types/review.permit.type';
import { ReviewSectionsContainerAbstractComponent } from '@permit-application/shared/review-sections/review-sections-container-abstract.component';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { RequestActionsService, RequestItemsService } from 'pmrv-api';

import { PermitTransferStore } from '../../store/permit-transfer.store';
import { ReviewGroupStatusPermitTransferPipe } from '../review-group-status-permit-transfer.pipe';

@Component({
  selector: 'app-transfer-review-sections-container',
  templateUrl: './review-sections-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewSectionsContainerComponent extends ReviewSectionsContainerAbstractComponent implements OnInit {
  confirmTransferDetailsReviewStatus$: Observable<ReviewGroupDecisionStatus | TaskItemStatus> =
    this.store.getConfirmTransferDetailsReviewStatus$();

  readonly header$ = this.store.pipe(
    map((state) => {
      if (state.isRequestTask) {
        switch (state.requestTaskType) {
          case 'PERMIT_TRANSFER_B_APPLICATION_REVIEW':
          case 'PERMIT_TRANSFER_B_WAIT_FOR_AMENDS':
          case 'PERMIT_TRANSFER_B_WAIT_FOR_PEER_REVIEW':
            return `${state.permitType} permit transfer review`;
          case 'PERMIT_TRANSFER_B_WAIT_FOR_REVIEW':
            return 'Full transfer of permit';
          case 'PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW':
            return `${state.permitType} permit transfer peer review`;
        }
      } else {
        return 'Review full transfer of permit';
      }
    }),
  );

  readonly isPermitReviewSectionsVisible$ = this.store.pipe(
    filter(
      (state) =>
        [
          'PERMIT_TRANSFER_B_APPLICATION_REVIEW',
          'PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW',
          'PERMIT_TRANSFER_B_WAIT_FOR_PEER_REVIEW',
          'PERMIT_TRANSFER_B_WAIT_FOR_AMENDS',
        ].includes(state.requestTaskType) ||
        ['PERMIT_TRANSFER_B_APPLICATION_GRANTED'].includes(state.requestActionType),
    ),
  );

  constructor(
    protected readonly store: PermitTransferStore,
    protected readonly destroy$: DestroySubject,
    protected readonly route: ActivatedRoute,
    protected readonly router: Router,
    protected readonly requestItemsService: RequestItemsService,
    protected readonly requestActionsService: RequestActionsService,
    protected readonly backLinkService: BackLinkService,
    private title: Title,
  ) {
    super(store, router, route, requestItemsService, requestActionsService, destroy$, backLinkService);
  }

  ngOnInit(): void {
    this.statusResolverPipe = new ReviewGroupStatusPermitTransferPipe(this.store);

    this.header$.pipe(takeUntil(this.destroy$)).subscribe((header) => this.title.setTitle(header));
  }
}
