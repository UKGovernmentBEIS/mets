/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
/* eslint-disable @angular-eslint/use-component-selector */
import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, takeUntil, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { parseCsv } from '@aviation/request-task/util';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { UuidFilePair } from '@shared/file-input/file-upload-event';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { GovukSelectOption } from 'govuk-components';

import { EmpCorsiaOperatorDetails, IssuingAuthoritiesService } from 'pmrv-api';

import { OperatorDetailsCorsiaFormProvider } from './operator-details-form.provider';
import { OperatorDetailsQuery } from './store/operator-details.selectors';

@Component({
  template: '',
})
export abstract class BaseOperatorDetailsComponent implements OnInit {
  operatorDetails: EmpCorsiaOperatorDetails;
  downloadUrl = `${this.store.empCorsiaDelegate.baseFileAttachmentDownloadUrl}/`;
  isEditable$ = this.store.pipe(map((state) => state.isEditable));

  private sectionsWithAttachments = {
    airOperatingCertificate: 'certificateFiles',
    organisationStructure: 'evidenceFiles',
  };

  constructor(
    public router: Router,
    public route: ActivatedRoute,
    public pendingRequestService: PendingRequestService,
    protected readonly formProvider: OperatorDetailsCorsiaFormProvider,
    protected readonly store: RequestTaskStore,
    protected readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.updateOperatorDetails();
  }

  updateOperatorDetails() {
    this.store
      .pipe(OperatorDetailsQuery.selectOperatorDetails, takeUntil(this.destroy$))
      .subscribe((operatorDetails: EmpCorsiaOperatorDetails) => {
        this.operatorDetails = operatorDetails;
      });
  }

  getform(operatorDetailsSection: keyof EmpCorsiaOperatorDetails) {
    return this.formProvider.form.controls[operatorDetailsSection] as FormGroup<any>;
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

  submitForm(
    formControlName: keyof EmpCorsiaOperatorDetails,
    payload: Partial<EmpCorsiaOperatorDetails>,
    navigateTo: string,
    status: TaskItemStatus = 'in progress',
    change = null,
  ) {
    const currentForm = this.getform(formControlName);
    let operatorDetails = { ...this.operatorDetails, ...payload };
    operatorDetails = this.transformSubsidiaryCompanies(formControlName, operatorDetails);

    if (formControlName ? currentForm.valid : this.formProvider.form?.valid) {
      this.store.empCorsiaDelegate
        .saveEmp({ operatorDetails }, status)
        .pipe(
          this.pendingRequestService.trackRequest(),
          tap(() => {
            const sectionWithAttachment = this.sectionsWithAttachments[formControlName];
            if (sectionWithAttachment) {
              this.addFilesToEmpAttachments(currentForm, sectionWithAttachment);
            }
          }),
        )
        .subscribe(() => {
          this.router.navigate([navigateTo], {
            relativeTo: this.route,
            queryParams: { change },
          });
        });
    }
  }

  private addFilesToEmpAttachments(currentForm: FormGroup, formControlName: string) {
    (currentForm.value[formControlName] || []).forEach((file: UuidFilePair) => {
      this.store.empCorsiaDelegate.addEmpAttachment({ [file.uuid]: file.file.name });
    });
  }

  transformSubsidiaryCompanies(
    formControlName: keyof EmpCorsiaOperatorDetails,
    operatorDetails: EmpCorsiaOperatorDetails,
  ) {
    if (formControlName === 'subsidiaryCompanies') {
      operatorDetails.subsidiaryCompanyExist = this.getform('subsidiaryCompanyExist').value;
    }

    if (operatorDetails?.subsidiaryCompanies) {
      operatorDetails.subsidiaryCompanies.forEach((item) => {
        const airOperatingCertificate = item.airOperatingCertificate;
        if (airOperatingCertificate.certificateExist) {
          if (
            airOperatingCertificate.certificateFiles &&
            typeof airOperatingCertificate.certificateFiles[0] === 'object'
          ) {
            if ((airOperatingCertificate.certificateFiles as any)[0]?.uuid) {
              item.airOperatingCertificate.certificateFiles = airOperatingCertificate.certificateFiles?.map(
                (doc: any) => doc.uuid,
              );
            } else {
              item.airOperatingCertificate.certificateFiles = this.transformCertificateFileToUuid(
                (airOperatingCertificate.certificateFiles[0] as any).downloadUrl,
              );
            }
          }
        }

        const flightIdentification = item.flightIdentification;

        if (
          flightIdentification.flightIdentificationType === 'AIRCRAFT_REGISTRATION_MARKINGS' &&
          flightIdentification.aircraftRegistrationMarkings.length > 0 &&
          typeof flightIdentification.aircraftRegistrationMarkings === 'string'
        ) {
          item.flightIdentification.aircraftRegistrationMarkings = parseCsv(
            flightIdentification.aircraftRegistrationMarkings as unknown as string,
          );
        }
      });
    }
    return operatorDetails;
  }

  transformCertificateFileToUuid(downloadUrl: string) {
    const fileName = downloadUrl.split('/').pop();
    const uuid = fileName.split('attachment').pop();

    return [uuid];
  }
}
