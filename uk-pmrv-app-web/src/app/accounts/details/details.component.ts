import { ChangeDetectionStrategy, Component, Input, OnInit, signal } from '@angular/core';
import { FormControl, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, catchError, combineLatest, iif, map, Observable, of, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectUserRoleType } from '@core/store/auth';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { permitTypeMap } from '@permit-application/shared/utils/permit';

import {
  CompanyInformationService,
  CompanyProfileDTO,
  InstallationAccountDTO,
  InstallationAccountPermitDTO,
} from 'pmrv-api';

import { accountFinalStatuses } from '../core/accountFinalStatuses';

@Component({
  selector: 'app-account-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class DetailsComponent implements OnInit {
  @Input() currentTab: string;
  errorMessage$ = new BehaviorSubject<string>(null);

  accountPermit$ = (
    this.route.data as Observable<{
      accountPermit: InstallationAccountPermitDTO;
    }>
  ).pipe(map((data) => data?.accountPermit));

  permit$ = this.accountPermit$.pipe(map((accountPermit) => accountPermit.permit));
  account$ = this.accountPermit$.pipe(map((accountPermit) => accountPermit.account));

  userRoleType$ = this.authStore.pipe(selectUserRoleType, takeUntil(this.destroy$));

  permitTypeMap = permitTypeMap;

  companiesHouse$: Observable<CompanyProfileDTO> = combineLatest([this.account$, this.userRoleType$]).pipe(
    switchMap(([account, roleType]) =>
      iif(
        () => roleType === 'REGULATOR',
        this.companyInformationService.getCompanyProfileByRegistrationNumber(account.legalEntity.referenceNumber),
        of({}) as any,
      ),
    ),
    catchNotFoundRequest(ErrorCode.NOTFOUND1001, () => {
      this.errorMessage$.next('The registration number was not found at the Companies House');
      return of({});
    }),
    catchError(() => {
      this.errorMessage$.next('The Companies House service is unavailable at the moment. Try again later');
      return of({});
    }),
  );

  form = this.fb.group({ companiesHouseDetails: new FormControl<boolean>(null) }, { updateOn: 'change' });
  showCompaniesHouse = signal(false);

  constructor(
    private readonly route: ActivatedRoute,
    private readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
    private readonly companyInformationService: CompanyInformationService,
    private readonly fb: UntypedFormBuilder,
  ) {}

  ngOnInit(): void {
    this.form.valueChanges.subscribe((value: { companiesHouseDetails: Array<boolean> }) => {
      if (value.companiesHouseDetails.length > 0) {
        this.showCompaniesHouse.set(true);
      } else {
        this.showCompaniesHouse.set(false);
      }
    });
  }

  canChangeByStatus(status: InstallationAccountDTO['status']): boolean {
    return accountFinalStatuses(status);
  }
}
