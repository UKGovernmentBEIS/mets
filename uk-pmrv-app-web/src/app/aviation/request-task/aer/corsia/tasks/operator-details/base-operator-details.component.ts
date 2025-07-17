/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
/* eslint-disable @angular-eslint/use-component-selector */
import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, takeUntil, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { UuidFilePair } from '@shared/file-input/file-upload-event';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { GovukSelectOption } from 'govuk-components';

import { AviationCorsiaOperatorDetails, IssuingAuthoritiesService } from 'pmrv-api';

import { OperatorDetailsFormProvider } from './operator-details-form.provider';
import { OperatorDetailsQuery } from './store/operator-details.selectors';

@Component({
  template: '',
})
export abstract class BaseOperatorDetailsComponent implements OnInit {
  operatorDetails: AviationCorsiaOperatorDetails;
  downloadUrl = `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/`;
  isEditable$ = this.store.pipe(map((state) => state.isEditable));

  private sectionsWithAttachments = {
    airOperatingCertificate: 'certificateFiles',
    organisationStructure: 'evidenceFiles',
  };

  constructor(
    public router: Router,
    public route: ActivatedRoute,
    public pendingRequestService: PendingRequestService,
    protected readonly formProvider: OperatorDetailsFormProvider,
    protected readonly store: RequestTaskStore,
    protected readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.store
      .pipe(OperatorDetailsQuery.selectOperatorDetails, takeUntil(this.destroy$))
      .subscribe((operatorDetails) => {
        this.operatorDetails = operatorDetails;
      });
  }

  getform(operatorDetailsSection: keyof AviationCorsiaOperatorDetails) {
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
    formControlName: keyof AviationCorsiaOperatorDetails,
    operatorDetails: AviationCorsiaOperatorDetails,
    navigateTo: string,
    status: TaskItemStatus = 'in progress',
  ) {
    const currentForm = this.getform(formControlName);

    if (formControlName ? currentForm.valid : this.formProvider.form?.valid) {
      this.store.aerDelegate
        .saveAer({ operatorDetails: operatorDetails as any }, status)
        .pipe(
          this.pendingRequestService.trackRequest(),
          tap(() => {
            const sectionWithAttachment = this.sectionsWithAttachments[formControlName];
            if (sectionWithAttachment) {
              this.addFilesToAerAttachments(currentForm, sectionWithAttachment);
            }
          }),
        )
        .subscribe(() => {
          this.router.navigate(
            formControlName === 'operatorName' || formControlName === null ? [navigateTo] : ['../', navigateTo],
            {
              relativeTo: this.route,
            },
          );
        });
    }
  }

  private addFilesToAerAttachments(currentForm: FormGroup, formControlName: string) {
    (currentForm.value[formControlName] || []).forEach((file: UuidFilePair) => {
      this.store.aerDelegate.addAerAttachment({ [file.uuid]: file.file.name });
    });
  }
}
