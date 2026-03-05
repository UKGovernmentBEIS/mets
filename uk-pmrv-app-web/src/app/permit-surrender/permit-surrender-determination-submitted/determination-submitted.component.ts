import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { combineLatest, filter, first, map, Observable } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@core/store/auth';
import { BackLinkService } from '@shared/back-link/back-link.service';

import {
  PermitSurrenderReviewDecision,
  PermitSurrenderReviewDeterminationDeemWithdraw,
  PermitSurrenderReviewDeterminationGrant,
  PermitSurrenderReviewDeterminationReject,
  RequestActionUserInfo,
} from 'pmrv-api';

import { PermitSurrenderStore } from '../store/permit-surrender.store';

@Component({
  selector: 'app-review-decision',
  templateUrl: './determination-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeterminationSubmittedComponent implements OnInit {
  userRoleType$: Observable<any>;
  reviewDeterminationGrant$: Observable<PermitSurrenderReviewDeterminationGrant>;
  reviewDeterminationReject$: Observable<PermitSurrenderReviewDeterminationReject>;
  reviewDeterminationDeemWithdrawn$: Observable<PermitSurrenderReviewDeterminationDeemWithdraw>;
  reviewDecision$: Observable<PermitSurrenderReviewDecision>;
  signatory$: Observable<string>;
  usersInfo$: Observable<{
    [key: string]: RequestActionUserInfo;
  }>;
  operators$: Observable<string[]>;

  creationDate$ = this.store.select('requestActionCreationDate');

  constructor(
    readonly store: PermitSurrenderStore,
    private readonly authStore: AuthStore,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
    this.userRoleType$ = this.authStore.pipe(selectUserRoleType);

    this.reviewDeterminationGrant$ = this.store.pipe(
      filter((state) => state.reviewDetermination?.type === 'GRANTED'),
      map((state) => state.reviewDetermination as PermitSurrenderReviewDeterminationGrant),
    );

    this.reviewDeterminationReject$ = this.store.pipe(
      filter((state) => state.reviewDetermination?.type === 'REJECTED'),
      map((state) => state.reviewDetermination as PermitSurrenderReviewDeterminationReject),
    );

    this.reviewDeterminationDeemWithdrawn$ = this.store.pipe(
      filter((state) => state.reviewDetermination?.type === 'DEEMED_WITHDRAWN'),
      map((state) => state.reviewDetermination as PermitSurrenderReviewDeterminationDeemWithdraw),
    );

    this.reviewDecision$ = this.store.pipe(map((state) => state.reviewDecision));

    this.signatory$ = this.store.pipe(map((state) => state.reviewDecisionNotification?.signatory));
    this.usersInfo$ = this.store.pipe(map((state) => state.usersInfo)) as Observable<{
      [key: string]: RequestActionUserInfo;
    }>;
    this.operators$ = combineLatest([this.usersInfo$, this.signatory$]).pipe(
      first(),
      map(([usersInfo, signatory]) => Object.keys(usersInfo).filter((userId) => userId !== signatory)),
    );
  }
}
