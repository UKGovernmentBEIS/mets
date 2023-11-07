import { FormControl, FormGroup } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { render } from '@testing-library/angular';

import { AviationOperatorDetails } from 'pmrv-api';

import { OperatorDetailsNameTemplateComponent } from './operator-details-name-template.component';

describe('OperatorDetailsNameTemplateComponent', () => {
  const renderComponent = async () => {
    const { fixture, detectChanges } = await render(
      `
      <app-operator-details-name-template
      [form]="form"
    ></app-operator-details-name-template>
      `,
      {
        imports: [RouterTestingModule, OperatorDetailsNameTemplateComponent],
        componentProperties: {
          form: new FormGroup({
            operatorName: new FormControl<AviationOperatorDetails['operatorName']>(null),
            crcoCode: new FormControl<AviationOperatorDetails['crcoCode']>(null),
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
