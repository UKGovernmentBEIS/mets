import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { OperatorDetailsNameTemplateComponent } from '@aviation/shared/components/operator-details/operator-details-name-template/operator-details-name-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { OperatorDetailsFlightIdentificationTypePipe } from '@aviation/shared/pipes/operator-details-flight-identification-type.pipe';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { CountryService } from '@core/services/country.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule, GovukSelectOption } from 'govuk-components';

import { IssuingAuthoritiesService } from 'pmrv-api';

import { BaseOperatorDetailsComponent } from '../../base-operator-details.component';
import { OperatorDetailsCorsiaFormProvider } from '../../operator-details-form.provider';

interface CertificateFile {
  file: {
    name: string;
  };
  uuid: string;
}

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
export class AddSubsidiaryCompanyComponent extends BaseOperatorDetailsComponent implements OnInit {
  form = this.formProvider.subsidiaryCompaniesForm();
  issuingAuthorityOptions$ = this.getIssuingAuthorityOptions(this.issuingAuthorityService);
  downloadUrl = `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/`;
  countriesOptions$: Observable<GovukSelectOption<string>[]> = this.countryService
    .getUkCountries()
    .pipe(
      map((countries) =>
        countries
          .map((country) => ({ text: country.name, value: country.code } as GovukSelectOption<string>))
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
      const subsidiaryCompanies = this.formProvider.form.get('subsidiaryCompanies')?.value;

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
    }
  }

  getIssuingAuthorityOptions(issuingAuthorityService: IssuingAuthoritiesService) {
    return issuingAuthorityService
      .getEmpIssuingAuthorityNames()
      .pipe(
        map((issuingAuthorityNames: string[]) =>
          issuingAuthorityNames.map(
            (authorityNames) => ({ text: authorityNames, value: authorityNames } as GovukSelectOption<string>),
          ),
        ),
      );
  }

  onSubmit() {
    let subsidiaryCompanies = [];
    if (this.formProvider.form.value?.subsidiaryCompanies?.length > 0) {
      subsidiaryCompanies = [...this.formProvider.form.value?.subsidiaryCompanies];
      if (!isNaN(this.index)) {
        subsidiaryCompanies.splice(this.index, 1, this.form.value);
      } else {
        subsidiaryCompanies.push(this.form.value);
      }
    } else {
      subsidiaryCompanies = [this.form.value];
    }

    subsidiaryCompanies = Object.values(this.removeNullKeysTransformationFiles(subsidiaryCompanies));

    this.getOperatorDetails();
    this.formProvider.addSubsidiaryCompanyControl(subsidiaryCompanies);

    if (!isNaN(this.index)) {
      this.submitForm('subsidiaryCompanies', { subsidiaryCompanies }, '../../list');
    } else {
      this.submitForm('subsidiaryCompanies', { subsidiaryCompanies }, '../list');
    }
  }

  private removeNullKeysTransformationFiles<T>(obj: T): T {
    const newObj = { ...obj };

    for (const key in newObj) {
      if (newObj[key] === null) {
        delete newObj[key];
      } else if (typeof newObj[key] === 'object' && !Array.isArray(newObj[key])) {
        newObj[key] = this.removeNullKeysTransformationFiles(newObj[key]);

        if ((newObj[key] as any)?.certificateExist) {
          (newObj[key] as any).certificateFiles = (newObj[key] as any).certificateFiles.map(
            (doc: FileUpload) => doc.uuid,
          );
        }
      }
    }

    return newObj;
  }

  transformCertificateFile(element): CertificateFile {
    const url = element.downloadUrl.split('/').pop();
    const uuid = url.split('attachment').pop();

    return {
      file: {
        name: element.fileName,
      },
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
