import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AuthStore, selectUserRoleType, UserState } from '@core/store';

import { DecisionNotification, FileInfoDTO, PermitNotificationReviewDecision, RequestActionUserInfo } from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';
import { PermitNotificationService } from '../core/permit-notification.service';

@Component({
  selector: 'app-permit-notification-review-decision',
  templateUrl: './review-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewDecisionComponent implements OnInit {
  reviewDecision$: Observable<PermitNotificationReviewDecision>;
  actionId$: Observable<number>;
  reviewDecisionNotification$: Observable<DecisionNotification>;
  usersInfo$: Observable<{
    [key: string]: RequestActionUserInfo;
  }>;
  officialNotice$: Observable<FileInfoDTO>;
  signatory$: Observable<string>;
  operators$: Observable<string[]>;
  userRoleType$: Observable<UserState['roleType']>;

  constructor(
    readonly route: ActivatedRoute,
    public readonly permitNotificationService: PermitNotificationService,
    readonly store: CommonActionsStore,
    private readonly authStore: AuthStore,
  ) {}

  ngOnInit(): void {
    this.reviewDecision$ = this.permitNotificationService.reviewDecision$;
    this.actionId$ = this.store.pipe(map((state) => state?.action?.id));
    this.reviewDecisionNotification$ = this.store.pipe(
      map((state) => state?.action?.payload?.['reviewDecisionNotification']),
    ) as Observable<DecisionNotification>;
    this.usersInfo$ = this.store.pipe(map((state) => state?.action?.payload?.['usersInfo'])) as Observable<{
      [key: string]: RequestActionUserInfo;
    }>;
    this.officialNotice$ = this.store.pipe(
      map((state) => state?.action?.payload?.['officialNotice']),
    ) as Observable<FileInfoDTO>;
    this.signatory$ = this.store.pipe(
      map((state) => state?.action?.payload?.['reviewDecisionNotification']?.['signatory']),
    ) as Observable<string>;
    this.operators$ = combineLatest([this.usersInfo$, this.signatory$]).pipe(
      map(([usersInfo, signatory]) => Object.keys(usersInfo).filter((userId) => userId !== signatory)),
    );
    this.userRoleType$ = this.authStore.pipe(selectUserRoleType);
  }
}
