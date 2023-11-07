import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';

import { WithholdingAllowancesActionService } from '../core/withholding-allowances.service';

@Component({
  selector: 'app-withholding-allowances-action-submitted-withdrawn',
  templateUrl: './submitted-withdrawn.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserInfoResolverPipe],
})
export class SubmittedWithdrawnComponent {
  actionType = this.route.snapshot.data.actionType;

  payload$ = this.actionService.getPayload();
  officialNoticeFiles$ = this.payload$.pipe(
    map((payload) => this.actionService.getOfficialNoticeFiles(payload.officialNotice)),
  );
  notificationUsers$ = this.payload$.pipe(
    map((payload) =>
      Object.keys(payload.usersInfo)
        .filter((userId) => userId !== payload.decisionNotification.signatory)
        .map((id) => this.userInfoResolverPipe.transform(id, payload.usersInfo)),
    ),
  );
  signatoryName$ = this.payload$.pipe(
    map((payload) => this.userInfoResolverPipe.transform(payload?.decisionNotification?.signatory, payload.usersInfo)),
  );

  constructor(
    readonly route: ActivatedRoute,
    readonly actionService: WithholdingAllowancesActionService,
    private readonly userInfoResolverPipe: UserInfoResolverPipe,
  ) {}
}
