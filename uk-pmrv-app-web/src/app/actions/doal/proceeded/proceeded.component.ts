import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, iif, map, of, switchMap } from 'rxjs';

import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';

import { DoalActionService } from '../core/doal-action.service';

@Component({
  selector: 'app-doal-proceeded',
  templateUrl: './proceeded.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserInfoResolverPipe],
})
export class ProceededComponent {
  decisionNotification$ = this.doalActionService
    .getProceededPayload$()
    .pipe(map((payload) => payload.decisionNotification));
  notificationUsers$ = this.doalActionService.getProceededPayload$().pipe(
    first(),
    switchMap((payload) =>
      iif(
        () => !!payload.decisionNotification,
        of(
          Object.keys(payload.usersInfo)
            .filter((userId) => userId !== payload.decisionNotification.signatory)
            .map((id) => this.userInfoResolverPipe.transform(id, payload.usersInfo)),
        ),
        of([]),
      ),
    ),
  );
  signatoryName$ = this.doalActionService.getProceededPayload$().pipe(
    first(),
    switchMap((payload) =>
      iif(
        () => !!payload.decisionNotification,
        of(this.userInfoResolverPipe.transform(payload.decisionNotification.signatory, payload.usersInfo)),
        of(''),
      ),
    ),
  );
  officialNoticeFiles$ = this.doalActionService
    .getProceededPayload$()
    .pipe(map((payload) => this.doalActionService.getOfficialNoticeFiles(payload.officialNotice)));

  constructor(
    private readonly doalActionService: DoalActionService,
    private readonly userInfoResolverPipe: UserInfoResolverPipe,
  ) {}
}
