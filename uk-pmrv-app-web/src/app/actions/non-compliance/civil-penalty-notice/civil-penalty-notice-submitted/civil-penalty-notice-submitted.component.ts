import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, iif, map, Observable, of, switchMap } from 'rxjs';

import { AuthStore, selectCurrentDomain } from '@core/store';
import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';

import { CaExternalContactsService, NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { NonComplianceService } from '../../core/non-compliance.service';

@Component({
  selector: 'app-civil-penalty-notice-submitted',
  standalone: false,
  templateUrl: './civil-penalty-notice-submitted.component.html',
  providers: [UserInfoResolverPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CivilPenaltyNoticeSubmittedComponent {
  payload$ = (
    this.nonComplianceService.getPayload() as Observable<NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload>
  ).pipe(
    first(),
    map((payload) => payload),
  );

  isAviation$ = this.authStore.pipe(
    selectCurrentDomain,
    map((v) => v === 'AVIATION'),
  );

  documentFiles$ = this.payload$.pipe(
    first(),
    map((payload) => payload?.civilPenalty),
    map((file) => (file ? this.nonComplianceService.getDownloadUrlFiles([file]) : [])),
  );

  notificationUsers$ = this.payload$.pipe(
    switchMap((payload) => {
      const internalUsers = payload?.usersInfo
        ? Object.keys(payload.usersInfo).map((id) => this.userInfoResolverPipe.transform(id, payload.usersInfo))
        : [];

      const transformExternals = (externalUsers) => externalUsers.map((user) => `${user.name} - External contact`);

      return iif(
        () => payload?.externalContacts && payload?.externalContacts.length > 0,
        this.caExternalContactsService.getCaExternalContactsByIds(new Set(payload?.externalContacts)).pipe(
          map(([...externalContacts]) => {
            return [...internalUsers, ...transformExternals(externalContacts)];
          }),
        ),
        of([...internalUsers]),
      );
    }),
  );

  constructor(
    readonly nonComplianceService: NonComplianceService,
    public readonly authStore: AuthStore,
    private readonly userInfoResolverPipe: UserInfoResolverPipe,
    private readonly caExternalContactsService: CaExternalContactsService,
  ) {}
}
