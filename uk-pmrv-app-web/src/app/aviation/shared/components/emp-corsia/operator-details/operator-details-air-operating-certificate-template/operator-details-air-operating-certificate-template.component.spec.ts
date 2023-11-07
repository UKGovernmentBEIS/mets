import { FormControl, FormGroup } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { FileUpload } from '@shared/file-input/file-upload-event';
import { render } from '@testing-library/angular';

import { AirOperatingCertificate } from 'pmrv-api';

import { OperatorDetailsAirOperatingCertificateTemplateComponent } from './operator-details-air-operating-certificate-template.component';

describe('OperatorDetailsAirOperatingCertificateComponent', () => {
  const renderComponent = async () => {
    const { fixture, detectChanges } = await render(
      `
      <app-operator-details-air-operating-certificate-template
        [form]="form"
      ></app-operator-details-air-operating-certificate-template>
      `,
      {
        imports: [RouterTestingModule, OperatorDetailsAirOperatingCertificateTemplateComponent],
        componentProperties: {
          form: new FormGroup({
            certificateExist: new FormControl<AirOperatingCertificate['certificateExist']>(null),
            certificateNumber: new FormControl<AirOperatingCertificate['certificateNumber']>(null),
            issuingAuthority: new FormControl<AirOperatingCertificate['issuingAuthority']>(null),
            certificateFiles: new FormControl<FileUpload[]>(null),
            restrictionsExist: new FormControl<boolean>(null),
            restrictionsDetails: new FormControl<string>(null),
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
