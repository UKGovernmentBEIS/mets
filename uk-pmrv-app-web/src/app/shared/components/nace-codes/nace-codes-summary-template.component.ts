import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';

import { BehaviorSubject, catchError, Observable, of, Subject, switchMap, takeUntil, tap } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@core/store';
import { ErrorCode } from '@error/not-found-error';
import { getInstallationActivityLabelByValue, NaceCode } from '@tasks/aer/submit/nace-codes/nace-code-types';

import { GovukTableColumn } from 'govuk-components';

import { CompanyInformationService, CompanyProfileDTO, NaceCodes } from 'pmrv-api';

@Component({
  selector: 'app-nace-codes-summary-template',
  templateUrl: './nace-codes-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NaceCodesSummaryTemplateComponent implements OnInit, OnDestroy {
  @Input() noBottomBorder: boolean;
  @Input() isEditable: boolean;
  @Input() naceCodes: NaceCodes;

  @Input() set registrationNumber(regNum: string) {
    this.loadCompanyProfile(regNum);
  }

  companyProfile$: Observable<CompanyProfileDTO>;
  errorMessage$ = new BehaviorSubject<string>(null);
  private destroy$ = new Subject<void>();

  data: { code: NaceCode; label: string }[];

  columns: GovukTableColumn[] = [
    { field: 'label', header: '', widthClass: 'govuk-input--width-20' },
    { field: 'delete', header: '', widthClass: 'govuk-input--width-20' },
  ];

  constructor(
    private readonly companyInformationService: CompanyInformationService,
    private readonly authStore: AuthStore,
  ) {}

  ngOnInit(): void {
    this.data =
      this.naceCodes?.codes?.map((val) => ({ code: val, label: getInstallationActivityLabelByValue(val) })) ?? [];
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadCompanyProfile(registrationNumber: string): void {
    if (registrationNumber) {
      this.companyProfile$ = this.authStore.pipe(
        selectUserRoleType,
        switchMap((roleType) => {
          if (roleType === 'REGULATOR') {
            return this.companyInformationService.getCompanyProfileByRegistrationNumber(registrationNumber).pipe(
              tap(() => this.errorMessage$.next(null)),
              catchError((responseObj) => {
                if (responseObj?.error?.code === ErrorCode.NOTFOUND1001) {
                  this.errorMessage$.next('The registration number was not found at the Companies House');
                } else {
                  this.errorMessage$.next('The Companies House service is unavailable at the moment. Try again later');
                }
                return of({});
              }),
            );
          } else {
            return of(null);
          }
        }),
        takeUntil(this.destroy$),
      );
    }
  }
}
