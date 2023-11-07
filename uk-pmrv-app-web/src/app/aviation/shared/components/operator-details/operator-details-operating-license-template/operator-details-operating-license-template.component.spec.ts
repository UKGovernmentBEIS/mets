import { FormControl, FormGroup } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { render } from '@testing-library/angular';

import { OperatingLicense } from 'pmrv-api';

import { OperatorDetailsOperatingLicenseTemplateComponent } from './operator-details-operating-license-template.component';

describe('OperatorDetailsOperatingLicenseTemplateComponent', () => {
  const renderComponent = async () => {
    const { fixture, detectChanges } = await render(
      `
      <app-operator-details-operating-license-template
        [form]="form"
      ></app-operator-details-operating-license-template>
      `,
      {
        imports: [RouterTestingModule, OperatorDetailsOperatingLicenseTemplateComponent],
        componentProperties: {
          form: new FormGroup({
            licenseExist: new FormControl<OperatingLicense['licenseExist']>(null),
            licenseNumber: new FormControl<OperatingLicense['licenseNumber']>(null),
            issuingAuthority: new FormControl<OperatingLicense['issuingAuthority'] | null>(null),
          }),
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
