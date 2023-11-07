import { FormArray, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';

import {
  IndividualOrganisation,
  LimitedCompanyOrganisation,
  LocationOnShoreStateDTO,
  OrganisationStructure,
  PartnershipOrganisation,
} from 'pmrv-api';

import { LegalStatusTypeFormComponent } from './legal-status-type-form.component';

describe('LegalStatusTypeFormComponent', () => {
  const addressForm = new FormGroup({
    type: new FormControl<LocationOnShoreStateDTO['type'] | null>('ONSHORE_STATE'),
    line1: new FormControl<LocationOnShoreStateDTO['line1'] | null>(null),
    line2: new FormControl<LocationOnShoreStateDTO['line2'] | null>(null),
    city: new FormControl<LocationOnShoreStateDTO['city'] | null>(null),
    state: new FormControl<LocationOnShoreStateDTO['state'] | null>(null),
    postcode: new FormControl<LocationOnShoreStateDTO['postcode'] | null>(null),
    country: new FormControl<LocationOnShoreStateDTO['country'] | null>(null),
  });

  const form = new FormGroup({
    legalStatusType: new FormControl<OrganisationStructure['legalStatusType'] | null>(null),
    partnershipName: new FormControl<PartnershipOrganisation['partnershipName'] | null>(null),
    organisationLocation: addressForm,
    partners: new FormArray<FormControl<string>>([]),
    registrationNumber: new FormControl<LimitedCompanyOrganisation['registrationNumber'] | null>(null),
    evidenceFiles: new FormControl<LimitedCompanyOrganisation['evidenceFiles'] | null>(null),
    differentContactLocationExist: new FormControl<LimitedCompanyOrganisation['differentContactLocationExist'] | null>(
      null,
    ),
    differentContactLocation: addressForm,
    fullName: new FormControl<IndividualOrganisation['fullName'] | null>(null),
  });

  const renderComponent = async () => {
    const { fixture, detectChanges } = await render(
      `
      <form [formGroup]="form">
        <app-organisation-structure-legal-status-type-form
          downloadUrl="/aviation/tasks/1/file-download/attachment"
          [partnersControl]="partnersControl"
          [isEditable]="true"
        ></app-organisation-structure-legal-status-type-form>
      </form>
      `,
      {
        imports: [ReactiveFormsModule, LegalStatusTypeFormComponent],
        componentProperties: {
          form,
          partnersControl: form.get('partners') as FormArray,
        },
      },
    );

    return { component: fixture.componentInstance, detectChanges };
  };

  it('should create', async () => {
    const { component } = await renderComponent();
    expect(component).toBeTruthy();
  });
});
