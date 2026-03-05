import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { parseCsv } from '@aviation/request-task/util';
import { OperatorDetailsNameTemplateComponent } from '@aviation/shared/components/operator-details/operator-details-name-template/operator-details-name-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { OperatorDetailsFlightIdentificationTypePipe } from '@aviation/shared/pipes/operator-details-flight-identification-type.pipe';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { CountryService } from '@core/services/country.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule, GovukSelectOption } from 'govuk-components';

import { EmpCorsiaOperatorDetails, IssuingAuthoritiesService, SubsidiaryCompanyCorsia } from 'pmrv-api';

import { BaseOperatorDetailsComponent } from '../../base-operator-details.component';
import { OperatorDetailsCorsiaFormProvider } from '../../operator-details-form.provider';

@Component({
  selector: 'app-add-subsidiary-company',
  templateUrl: './add-subsidiary-company.component.html',
  standalone: true,
  imports: [
    GovukComponentsModule,
    NgIf,
    NgFor,
    SharedModule,
    ReturnToLinkComponent,
    OperatorDetailsNameTemplateComponent,
    OperatorDetailsFlightIdentificationTypePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddSubsidiaryCompanyComponent extends BaseOperatorDetailsComponent implements OnInit, OnDestroy {
  private subsidiaryCompaniesFormArray = this.getform('subsidiaryCompanies') as unknown as FormArray;
  form: FormGroup;
  issuingAuthorityOptions$ = this.getIssuingAuthorityOptions(this.issuingAuthorityService);
  downloadUrl = `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/`;
  countriesOptions$: Observable<GovukSelectOption<string>[]> = this.countryService
    .getUkCountries()
    .pipe(
      map((countries) =>
        countries
          .map((country) => ({ text: country.name, value: country.code }) as GovukSelectOption<string>)
          .sort((a, b) => a.text.localeCompare(b.text)),
      ),
    );

  get isAddPath() {
    return this.route.snapshot.url[0].path === 'add';
  }

  get index() {
    return +this.route.snapshot.url[0].path;
  }

  constructor(
    public router: Router,
    public route: ActivatedRoute,
    public pendingRequestService: PendingRequestService,
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: OperatorDetailsCorsiaFormProvider,
    protected readonly store: RequestTaskStore,
    protected readonly destroy$: DestroySubject,
    private issuingAuthorityService: IssuingAuthoritiesService,
    private readonly countryService: CountryService,
  ) {
    super(router, route, pendingRequestService, formProvider, store, destroy$);
  }

  ngOnInit(): void {
    if (!this.isAddPath) {
      this.form = this.subsidiaryCompaniesFormArray.controls[this.index] as FormGroup;

      const subsidiaryCompanies = this.form?.value;

      if (subsidiaryCompanies && !!subsidiaryCompanies[this.index]) {
        for (const company of subsidiaryCompanies) {
          if (
            company.airOperatingCertificate &&
            company.airOperatingCertificate.certificateExist &&
            company.airOperatingCertificate.certificateFiles
          ) {
            company.airOperatingCertificate.certificateFiles = company.airOperatingCertificate.certificateFiles.map(
              (element) => (this.isCertificateFileObject(element) ? this.transformCertificateFile(element) : element),
            );
          }
        }

        this.form.patchValue(subsidiaryCompanies[this.index]);
      }
    } else {
      this.form = this.subsidiaryCompaniesFormArray.controls[
        this.subsidiaryCompaniesFormArray.controls.length - 1
      ] as FormGroup;
    }

    const airOperatingCertificateControl = (this.form.controls.airOperatingCertificate as FormGroup).controls;

    airOperatingCertificateControl.restrictionsExist.valueChanges.subscribe((restrictionsExist) => {
      if (restrictionsExist) {
        airOperatingCertificateControl.restrictionsDetails.enable();
      } else {
        airOperatingCertificateControl.restrictionsDetails.disable();
      }
    });
  }

  ngOnDestroy(): void {
    if (this.isAddPath && this.form.invalid && this.subsidiaryCompaniesFormArray.controls.length > 1) {
      this.subsidiaryCompaniesFormArray.removeAt(this.subsidiaryCompaniesFormArray.controls.length - 1);
    }
  }

  getIssuingAuthorityOptions(issuingAuthorityService: IssuingAuthoritiesService) {
    return issuingAuthorityService
      .getEmpIssuingAuthorityNames()
      .pipe(
        map((issuingAuthorityNames: string[]) =>
          issuingAuthorityNames.map(
            (authorityNames) => ({ text: authorityNames, value: authorityNames }) as GovukSelectOption<string>,
          ),
        ),
      );
  }

  onSubmit() {
    const { flightIdentification, airOperatingCertificate } = this.form.value as SubsidiaryCompanyCorsia;

    if (!airOperatingCertificate.certificateExist) {
      airOperatingCertificate.certificateFiles = [];
      airOperatingCertificate.certificateNumber = null;
      airOperatingCertificate.issuingAuthority = null;
      airOperatingCertificate.restrictionsDetails = null;
      airOperatingCertificate.restrictionsExist = null;
    } else {
      if (!airOperatingCertificate.restrictionsExist) {
        airOperatingCertificate.restrictionsDetails = null;
      }
    }

    this.form.patchValue({ ...this.form.value, flightIdentification, airOperatingCertificate });

    this.subsidiaryCompaniesFormArray.updateValueAndValidity();
    const subsidiaryCompanies = (
      this.subsidiaryCompaniesFormArray.value as EmpCorsiaOperatorDetails['subsidiaryCompanies']
    ).map((subsidiaryCompany) => {
      return subsidiaryCompany.flightIdentification.aircraftRegistrationMarkings?.length > 0
        ? {
            ...subsidiaryCompany,
            flightIdentification: {
              ...subsidiaryCompany.flightIdentification,
              aircraftRegistrationMarkings: parseCsv(
                subsidiaryCompany.flightIdentification.aircraftRegistrationMarkings as unknown as string,
              ),
            },
          }
        : subsidiaryCompany;
    });

    this.updateOperatorDetails();

    if (this.isAddPath) {
      this.submitForm('subsidiaryCompanies', { subsidiaryCompanies }, '../list');
    } else {
      this.submitForm('subsidiaryCompanies', { subsidiaryCompanies }, '../../list');
    }
  }

  transformCertificateFile(element): FileUpload {
    const url = element.downloadUrl.split('/').pop();
    const uuid = url.split('attachment').pop();

    return {
      file: {
        name: element.fileName,
      } as File,
      uuid: uuid,
    };
  }

  isCertificateFileObject(obj: any): boolean {
    return (
      obj &&
      typeof obj === 'object' &&
      Object.prototype.hasOwnProperty.call(obj, 'fileName') &&
      typeof obj.fileName === 'string' &&
      Object.prototype.hasOwnProperty.call(obj, 'downloadUrl') &&
      typeof obj.downloadUrl === 'string'
    );
  }
}
