import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, map, Observable } from 'rxjs';

import { AuthStore, selectUserProfile } from '@core/store';
import { ReviewDeterminationStatus } from '@permit-application/review/types/review.permit.type';
import { permitTypeMap } from '@permit-application/shared/utils/permit';
import { getPreviewDocumentsInfo } from '@permit-application/shared/utils/previewDocuments.utils';

import { DecisionNotification, RequestActionsService, RequestItemsService } from 'pmrv-api';

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
  decision$ = this.store.pipe(
    map((state): ReviewDeterminationStatus => {
      switch (state?.determination?.type) {
        case 'GRANTED':
          return 'granted';
        case 'REJECTED':
          return 'rejected';
        case 'DEEMED_WITHDRAWN':
          return 'deemed withdrawn';
        default:
          return null;
      }
    }),
  );

  previewDocuments$ = combineLatest([this.decision$, this.store]).pipe(
    map(([decision, state]) => {
      return getPreviewDocumentsInfo(state.requestTaskType, decision, state.permitType);
    }),
  );
  permitTypeMap = permitTypeMap;
  userProfile$ = this.authStore.pipe(selectUserProfile);

  decisionNotification$: Observable<DecisionNotification> = this.userProfile$.pipe(
    map((userProfile) => {
      return {
        operators: [''],
        externalContacts: [],
        signatory: userProfile?.id,
      };
    }),
  );

  readonly header$ = this.store.pipe(
    map((state) => {
      if (state.isRequestTask) {
        switch (state.requestTaskType) {
          case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW':
            return permitTypeMap?.[state.permitType] + ' permit peer review';
          case 'PERMIT_ISSUANCE_WAIT_FOR_REVIEW':
            return 'Apply for a permit';
          default:
            return permitTypeMap?.[state.permitType] + ' permit determination';
        }
      } else {
        return `${permitTypeMap?.[state.permitType]} permit determination`;
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
    protected readonly authStore: AuthStore,
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
