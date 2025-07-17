import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, catchError, EMPTY, map, of, switchMap, tap, throwError } from 'rxjs';

import { CountryService } from '@core/services/country.service';
import { ErrorCodes } from '@error/business-errors';

import { CompanyInformationService } from 'pmrv-api';

import { LEGAL_ENTITY_FORM_OP } from '../../factories/legal-entity/legal-entity-form-op.factory';
import { InstallationAccountApplicationStore } from '../../store/installation-account-application.store';

@Component({
  selector: 'app-legal-entity-regno-op',
  templateUrl: './legal-entity-regno-op.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LegalEntityRegnoOpComponent {
  form: UntypedFormGroup;
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);

  constructor(
    @Inject(LEGAL_ENTITY_FORM_OP) readonly legalEntityForm: UntypedFormGroup,
    private readonly companyInformationService: CompanyInformationService,
    public readonly store: InstallationAccountApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly countryService: CountryService,
  ) {
    this.form = this.legalEntityForm.get('referenceNumberGroup') as UntypedFormGroup;
  }

  onSubmit(): void {
    if (this.form.valid) {
      if (this.form.get('isEntityRegistered').value) {
        this.companyInformationService
          .getCompanyProfileByRegistrationNumber(this.form.get('referenceNumber').value)
          .pipe(
            catchError((res) => {
              switch (res?.error?.code) {
                case ErrorCodes.NOTFOUND1001:
                  this.form.get('referenceNumber').setErrors({
                    referenceNumberNotExists: 'The registration number was not found at the Companies House',
                  });
                  break;
                default:
                  return throwError(() => res);
              }
              this.isSummaryDisplayed$.next(true);
              return EMPTY;
            }),
            switchMap((companyProfile) => {
              const country = companyProfile.address?.country ?? '';
              if (country) {
                return this.countryService
                  .getCountryCode(country)
                  .pipe(map((countryCode) => ({ companyProfile, countryCode })));
              } else {
                return of({ companyProfile, countryCode: null });
              }
            }),
            tap(({ companyProfile, countryCode }) => {
              const address = { ...companyProfile.address, country: countryCode };
              this.legalEntityForm.get('detailsGroup')?.patchValue({
                address,
                name: companyProfile.name,
              });
            }),
          )
          .subscribe(() => {
            this.router.navigate(['../details'], { relativeTo: this.route });
          });
      } else {
        this.router.navigate(['../details'], { relativeTo: this.route });
      }
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }
}
