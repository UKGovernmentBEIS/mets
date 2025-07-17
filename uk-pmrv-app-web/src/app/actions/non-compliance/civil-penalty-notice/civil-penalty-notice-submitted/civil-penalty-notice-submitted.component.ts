import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map, Observable, switchMap } from 'rxjs';

import { AuthStore, selectCurrentDomain } from '@core/store';
import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';

import {
  CaExternalContactsDTO,
  CaExternalContactsService,
  NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload,
} from 'pmrv-api';

import { NonComplianceService } from '../../core/non-compliance.service';

@Component({
  selector: 'app-civil-penalty-notice-submitted',
  templateUrl: './civil-penalty-notice-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserInfoResolverPipe],
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

      return this.caExternalContactsService.getCaExternalContacts().pipe(
        map((externalContacts: CaExternalContactsDTO) => {
          // Filter external contacts based on the IDs in payload.externalContacts
          const filteredExternalContacts = externalContacts.caExternalContacts.filter((external) =>
            payload?.externalContacts?.includes(external.id),
          );

          return [...internalUsers, ...transformExternals(filteredExternalContacts)];
        }),
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
