import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { existingControlContainer } from '@shared/providers/control-container.factory';

import { GovukComponentsModule } from 'govuk-components';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-data-gaps-exist-form',
  imports: [GovukComponentsModule, ReactiveFormsModule],
  template: `
    <div formControlName="exist" govuk-radio class="govuk-!-width-two-thirds">
      <govuk-radio-option [value]="true" label="Yes"></govuk-radio-option>
      <govuk-radio-option [value]="false" label="No"></govuk-radio-option>
    </div>
  `,
  viewProviders: [existingControlContainer],
})
export class DataGapsExistFormComponent {}
