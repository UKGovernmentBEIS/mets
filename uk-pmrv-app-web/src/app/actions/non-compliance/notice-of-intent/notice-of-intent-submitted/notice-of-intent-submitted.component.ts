import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map, Observable, switchMap } from 'rxjs';

import { NonComplianceService } from '@actions/non-compliance/core/non-compliance.service';
import { AuthStore, selectCurrentDomain } from '@core/store';
import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';

import {
  CaExternalContactsDTO,
  CaExternalContactsService,
  NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload,
} from 'pmrv-api';

@Component({
  selector: 'app-notice-of-intent-submitted',
  templateUrl: './notice-of-intent-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserInfoResolverPipe],
})
export class NoticeOfIntentSubmittedComponent {
  payload$ = (
    this.nonComplianceService.getPayload() as Observable<NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload>
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
    map((payload) => payload?.noticeOfIntent),
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
