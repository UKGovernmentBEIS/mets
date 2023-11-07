import { ChangeDetectionStrategy, Component, ElementRef, Inject, ViewChild } from '@angular/core';
import { FormArray, FormControl } from '@angular/forms';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { LegalStatusTypeFormComponent } from '@aviation/shared/components/operator-details/operator-details-organisation-structure-forms/legal-status-type-form/legal-status-type-form.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import {
  IndividualOrganisation,
  LimitedCompanyOrganisation,
  OrganisationStructure,
  PartnershipOrganisation,
} from 'pmrv-api';

import { BaseOperatorDetailsComponent } from '../base-operator-details.component';
import { OperatorDetailsCorsiaFormProvider } from '../operator-details-form.provider';

@Component({
  selector: 'app-operator-details-organisation-structure-page',
  standalone: true,
  imports: [
    GovukComponentsModule,
    SharedModule,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    LegalStatusTypeFormComponent,
  ],
  templateUrl: './operator-details-organisation-structure.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class OperatorDetailsOrganisationStructureComponent extends BaseOperatorDetailsComponent {
  @ViewChild('conditionalHeader') header: ElementRef;

  form = this.getform('organisationStructure');

  constructor(
    public router: Router,
    public route: ActivatedRoute,
    public pendingRequestService: PendingRequestService,
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: OperatorDetailsCorsiaFormProvider,
    protected readonly store: RequestTaskStore,
    protected readonly destroy$: DestroySubject,
  ) {
    super(router, route, pendingRequestService, formProvider, store, destroy$);
  }

  get partnersControl(): FormArray | null {
    return (this.form.get('partners') as FormArray) ?? null;
  }

  onSubmit() {
    this.submitForm(
      'organisationStructure',
      { organisationStructure: this.cleanUpFormBeforeSubmit() },
      '../activities-description',
    );
  }

  cleanUpFormBeforeSubmit() {
    const legalStatusType = (this.form.value as OrganisationStructure).legalStatusType;

    const limitedCompanyInitialState = {
      registrationNumber: null,
      evidenceFiles: [],
      differentContactLocationExist: null,
      differentContactLocation: {
        type: 'ONSHORE_STATE',
        city: null,
        country: null,
        line1: null,
        line2: null,
        postcode: null,
        state: null,
      } as LimitedCompanyOrganisation['differentContactLocation'],
    };

    const individualInitialState = { fullName: null };

    const partnershipInitialState = () => {
      const partnersForm = this.form.controls.partners as FormArray<FormControl<string>>;

      if (partnersForm.controls.length > 0) {
        while (partnersForm.controls.length !== 1) {
          partnersForm.removeAt(0);
        }

        partnersForm.controls[0].patchValue(null);
      }

      return { partnershipName: null };
    };

    switch (legalStatusType) {
      case 'LIMITED_COMPANY': {
        this.form.patchValue({ ...individualInitialState, ...partnershipInitialState() });

        const {
          legalStatusType,
          organisationLocation,
          registrationNumber,
          differentContactLocationExist,
          differentContactLocation,
        } = this.form.value as LimitedCompanyOrganisation;

        return {
          legalStatusType,
          organisationLocation,
          registrationNumber,
          evidenceFiles: this.form.value.evidenceFiles?.map((doc: FileUpload) => doc.uuid),
          differentContactLocationExist,
          differentContactLocation,
        };
      }

      case 'INDIVIDUAL': {
        this.form.patchValue({ ...limitedCompanyInitialState, ...partnershipInitialState() });

        const { legalStatusType, organisationLocation, fullName } = this.form.value as IndividualOrganisation;

        return {
          legalStatusType,
          organisationLocation,
          fullName,
        };
      }

      case 'PARTNERSHIP': {
        this.form.patchValue({ ...limitedCompanyInitialState, ...individualInitialState });

        const { legalStatusType, organisationLocation, partnershipName, partners } = this.form
          .value as PartnershipOrganisation;

        return {
          legalStatusType,
          organisationLocation,
          partnershipName,
          partners,
        };
      }
    }
  }
}
