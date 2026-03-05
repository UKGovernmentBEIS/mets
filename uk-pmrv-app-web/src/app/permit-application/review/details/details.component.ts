import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import {
  BehaviorSubject,
  catchError,
  combineLatest,
  iif,
  map,
  Observable,
  of,
  switchMap,
  takeUntil,
  withLatestFrom,
} from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectUserRoleType } from '@core/store';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { CompanyInformationService, CompanyProfileDTO } from 'pmrv-api';

import { PermitApplicationState } from '../../store/permit-application.state';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(map((x) => x?.groupKey));
  showDiff$ = this.store.showDiff$;
  errorMessage$ = new BehaviorSubject<string>(null);

  originalInstallationOperatorDetails$ = (this.store as any).select('originalPermitContainer').pipe(
    withLatestFrom(this.showDiff$),
    map(([opc, showDiff]) => (showDiff ? opc.installationOperatorDetails : null)),
  );
  installationOperatorDetails$ = this.store.select('installationOperatorDetails');

  environmentalPermitsAndLicences$ = this.store.getTask('environmentalPermitsAndLicences');
  origEnvironmentalPermitsAndLicences$ = (this.store as any)
    .select('originalPermitContainer')
    .pipe(map((opc) => (opc as any).permit.environmentalPermitsAndLicences));

  userRoleType$ = this.authStore.pipe(selectUserRoleType, takeUntil(this.destroy$));

  companiesHouse$: Observable<CompanyProfileDTO> = combineLatest([
    this.installationOperatorDetails$,
    this.userRoleType$,
  ]).pipe(
    switchMap(([instOpDetails, roleType]) =>
      iif(
        () => roleType === 'REGULATOR',
        this.companyInformationService.getCompanyProfileByRegistrationNumber(instOpDetails.companyReferenceNumber),
        of({}) as any,
      ),
    ),
    catchNotFoundRequest(ErrorCode.NOTFOUND1001, () => {
      this.errorMessage$.next('The registration number was not found at the Companies House');
      return of({}) as any;
    }),
    catchError(() => {
      this.errorMessage$.next('The Companies House service is unavailable at the moment. Try again later');
      return of({}) as any;
    }),
  );

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly companyInformationService: CompanyInformationService,
    private readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}
}
