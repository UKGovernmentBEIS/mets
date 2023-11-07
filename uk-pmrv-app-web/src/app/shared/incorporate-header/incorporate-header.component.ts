/* eslint-disable @angular-eslint/component-max-inline-declarations */
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { catchError, EMPTY, map, Observable, of, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { selectCurrentDomain } from '@core/store/auth/auth.selectors';
import { AuthStore } from '@core/store/auth/auth.store';

import {
  AviationAccountHeaderInfoDTO,
  AviationAccountViewService,
  InstallationAccountHeaderInfoDTO,
  InstallationAccountViewService,
} from 'pmrv-api';

import { IncorporateHeaderStore } from './store/incorporate-header.store';

@Component({
  selector: 'app-incorporate-header',
  template: `
    <div *ngIf="(currentDomain$ | async) === 'INSTALLATION'; else aviationHeader">
      <div class="govuk-phase-banner" *ngIf="accountDetails$ | async as accountDetails">
        <div class="govuk-phase-banner__content">
          <span class="govuk-!-font-weight-bold"> {{ accountDetails.name }} </span>
          <span class="govuk-!-padding-left-3" *ngIf="accountDetails.permitId">
            Permit ID:
            <span> {{ accountDetails.permitId }} / {{ accountDetails.status | accountStatus }} </span>
          </span>
          <span class="govuk-!-padding-left-3" *ngIf="accountDetails?.emitterType">
            Type:
            <ng-container *ngIf="accountDetails.emitterType === 'GHGE'; else hseTemplate">
              {{ accountDetails.emitterType }} / {{ accountDetails.installationCategory | installationCategory }}
            </ng-container>
            <ng-template #hseTemplate>
              {{ accountDetails.emitterType }}
            </ng-template>
          </span>
        </div>
      </div>
    </div>
    <ng-template #aviationHeader>
      <div class="govuk-phase-banner" *ngIf="aviationAccountDetails$ | async as accountDetails">
        <div class="govuk-phase-banner__content">
          <span class="govuk-!-font-weight-bold"> {{ accountDetails.name }} </span>
          <span class="govuk-!-padding-left-3" *ngIf="accountDetails.empId">
            Emissions Plan ID:
            <span> {{ accountDetails.empId }} / {{ accountDetails.status | accountStatus }} </span>
          </span>
          <span class="govuk-!-padding-left-3" *ngIf="accountDetails?.emissionTradingScheme">
            Schema: {{ accountDetails.emissionTradingScheme | aviationNamePipe }}
          </span>
        </div>
      </div>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IncorporateHeaderComponent implements OnInit {
  accountDetails$: Observable<InstallationAccountHeaderInfoDTO>;
  aviationAccountDetails$: Observable<AviationAccountHeaderInfoDTO>;
  currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));

  constructor(
    private readonly store: IncorporateHeaderStore,
    private readonly accountViewService: InstallationAccountViewService,
    private readonly aviationAccountViewService: AviationAccountViewService,
    public readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.currentDomain$.pipe(takeUntil(this.destroy$)).subscribe((domain) => {
      domain === 'INSTALLATION'
        ? (this.accountDetails$ = this.store.pipe(
            map((state) => state.accountId),
            switchMap((accountId) => {
              return of(accountId).pipe(
                switchMap((accountId) =>
                  accountId ? this.accountViewService.getInstallationAccountHeaderInfoById(accountId) : of(undefined),
                ),
                catchError(() => EMPTY),
              );
            }),
          ))
        : (this.aviationAccountDetails$ = this.store.pipe(
            map((state) => state.accountId),
            switchMap((accountId) => {
              return of(accountId).pipe(
                switchMap((accountId) =>
                  accountId
                    ? this.aviationAccountViewService.getAviationAccountHeaderInfoById(accountId)
                    : of(undefined),
                ),
                catchError(() => EMPTY),
              );
            }),
          ));
    });
  }
}
