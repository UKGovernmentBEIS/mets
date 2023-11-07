import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable, switchMap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BaseSuccessComponent } from '@shared/base-success/base-success.component';
import { SharedModule } from '@shared/shared.module';

import { AccountVerificationBodyService } from 'pmrv-api';

export interface ConfirmationVerifierViewModel {
  competentAuthority: 'ENGLAND' | 'NORTHERN_IRELAND' | 'OPRED' | 'SCOTLAND' | 'WALES';
  assignedVerifier: string;
  requestId: string;
  submitHidden: boolean;
}

@Component({
  selector: 'app-confirmation-verifier',
  templateUrl: './confirmation-verifier.component.html',
  imports: [SharedModule, RouterLinkWithHref, ReturnToLinkComponent],
  standalone: true,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationVerifierComponent extends BaseSuccessComponent {
  vm$: Observable<ConfirmationVerifierViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(
      requestTaskQuery.selectRequestInfo,
      map((info) => info.accountId),
      switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
      map((vb) => vb.name),
    ),
    this.store.pipe(requestTaskQuery.selectCompetentAuthority),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([requestInfo, assignedVerifier, authority, isEditable]) => {
      return {
        requestId: requestInfo.id,
        assignedVerifier,
        competentAuthority: authority,
        submitHidden: !isEditable,
      };
    }),
  );

  constructor(
    private readonly store: RequestTaskStore,
    private accountVerificationBodyService: AccountVerificationBodyService,
  ) {
    super();
  }
}
