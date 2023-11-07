/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
/* eslint-disable @angular-eslint/use-component-selector */
import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, takeUntil, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { FileUpload, UuidFilePair } from '@shared/file-input/file-upload-event';
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
    this.getOperatorDetails();
  }

  getOperatorDetails() {
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
            (authorityNames) => ({ text: authorityNames, value: authorityNames } as GovukSelectOption<string>),
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
    operatorDetails = this.getSubsidiaryFiles(operatorDetails);

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

  getSubsidiaryFiles(operatorDetails) {
    if (operatorDetails?.subsidiaryCompanies) {
      for (const item of operatorDetails.subsidiaryCompanies) {
        if (item.airOperatingCertificate.certificateExist) {
          if (
            item.airOperatingCertificate.certificateFiles &&
            typeof item.airOperatingCertificate.certificateFiles[0] === 'object'
          ) {
            if (item.airOperatingCertificate.certificateFiles[0]?.uuid) {
              item.airOperatingCertificate.certificateFiles = item.airOperatingCertificate.certificateFiles?.map(
                (doc: FileUpload) => doc.uuid,
              );
            } else {
              item.airOperatingCertificate.certificateFiles = this.transformCertificateFileToUuid(
                item.airOperatingCertificate.certificateFiles[0].downloadUrl,
              );
            }
          }
        }
      }
    }
    return operatorDetails;
  }

  transformCertificateFileToUuid(downloadUrl: string) {
    const fileName = downloadUrl.split('/').pop();
    const uuid = fileName.split('attachment').pop();

    return [uuid];
  }
}
