import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';

import { VirApplicationReviewedRequestActionPayload } from 'pmrv-api';

import { VirService } from '../core/vir.service';

@Component({
  selector: 'app-reviewed',
  templateUrl: './reviewed.component.html',
  providers: [UserInfoResolverPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewedComponent {
  virPayload$ = this.virService.payload$ as Observable<VirApplicationReviewedRequestActionPayload>;
  virTitle$ = this.virPayload$.pipe(
    map((payload) => payload.reportingYear + ' verifier improvement report decision submitted'),
  );
  officialNoticeFiles$ = this.virPayload$.pipe(
    map((payload) => this.virService.getOfficialNoticeFiles(payload.officialNotice)),
  );
  notificationUsers$ = this.virPayload$.pipe(
    map((payload) =>
      Object.keys(payload.usersInfo)
        .filter((userId) => userId !== payload.decisionNotification.signatory)
        .map((id) => this.userInfoResolverPipe.transform(id, payload.usersInfo)),
    ),
  );
  signatoryName$ = this.virPayload$.pipe(
    map((payload) => this.userInfoResolverPipe.transform(payload?.decisionNotification?.signatory, payload.usersInfo)),
  );

  constructor(
    private readonly virService: VirService,
    private readonly userInfoResolverPipe: UserInfoResolverPipe,
  ) {}
}
