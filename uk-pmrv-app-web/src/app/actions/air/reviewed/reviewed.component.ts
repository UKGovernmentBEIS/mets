import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';

import { AirApplicationReviewedRequestActionPayload } from 'pmrv-api';

import { AirService } from '../core/air.service';

@Component({
  selector: 'app-reviewed',
  templateUrl: './reviewed.component.html',
  providers: [UserInfoResolverPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewedComponent {
  airPayload$ = this.airService.payload$ as Observable<AirApplicationReviewedRequestActionPayload>;
  airTitle$ = this.airPayload$.pipe(
    map((payload) => payload.reportingYear + ' Annual improvement report decision submitted'),
  );
  officialNoticeFiles$ = this.airPayload$.pipe(
    map((payload) => this.airService.getOfficialNoticeFiles(payload.officialNotice)),
  );
  notificationUsers$ = this.airPayload$.pipe(
    map((payload) =>
      Object.keys(payload.usersInfo)
        .filter((userId) => userId !== payload.decisionNotification.signatory)
        .map((id) => this.userInfoResolverPipe.transform(id, payload.usersInfo)),
    ),
  );
  signatoryName$ = this.airPayload$.pipe(
    map((payload) => this.userInfoResolverPipe.transform(payload?.decisionNotification?.signatory, payload.usersInfo)),
  );

  constructor(
    private readonly airService: AirService,
    private readonly userInfoResolverPipe: UserInfoResolverPipe,
  ) {}
}
