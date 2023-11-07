import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, map } from 'rxjs';

import { RequestActionsService, RequestItemsService } from 'pmrv-api';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { ReviewSectionsContainerAbstractComponent } from '../../../permit-application/shared/review-sections/review-sections-container-abstract.component';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { PermitIssuanceStore } from '../../store/permit-issuance.store';
import { ReviewGroupStatusPermitIssuancePipe } from '../review-group-status-permit-issuance.pipe';

@Component({
  selector: 'app-issuance-review-sections-container',
  templateUrl: './review-sections-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ReviewSectionsContainerComponent extends ReviewSectionsContainerAbstractComponent implements OnInit {
  readonly header$ = this.store.pipe(
    map((state) => {
      if (state.isRequestTask) {
        switch (state.requestTaskType) {
          case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW':
            return state.permitType + ' permit peer review';
          case 'PERMIT_ISSUANCE_WAIT_FOR_REVIEW':
            return 'Apply for a permit';
          default:
            return state.permitType + ' permit determination';
        }
      } else {
        return `${state.permitType} permit determination`;
      }
    }),
  );

  readonly isPermitReviewSectionsVisible$ = this.store.pipe(
    filter(
      (state) =>
        [
          'PERMIT_ISSUANCE_APPLICATION_REVIEW',
          'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW',
          'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW',
          'PERMIT_ISSUANCE_WAIT_FOR_AMENDS',
        ].includes(state.requestTaskType) || ['PERMIT_ISSUANCE_APPLICATION_GRANTED'].includes(state.requestActionType),
    ),
  );

  constructor(
    protected readonly store: PermitIssuanceStore,
    protected readonly destroy$: DestroySubject,
    protected readonly route: ActivatedRoute,
    protected readonly router: Router,
    protected readonly requestItemsService: RequestItemsService,
    protected readonly requestActionsService: RequestActionsService,
    protected readonly backLinkService: BackLinkService,
  ) {
    super(store, router, route, requestItemsService, requestActionsService, destroy$, backLinkService);
  }

  ngOnInit(): void {
    this.statusResolverPipe = new ReviewGroupStatusPermitIssuancePipe(this.store);
  }
}
