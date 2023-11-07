import { Component, Input } from '@angular/core';
import { FormArray } from '@angular/forms';

import { LocationStateFormComponent } from '@aviation/shared/components/location-state-form/location-state-form.component';
import { organisationStructureCreatePartnerFormControl } from '@aviation/shared/components/operator-details/utils/operator-details-form.util';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-organisation-structure-partnership-form',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, LocationStateFormComponent],
  templateUrl: './partnership-form.component.html',
  viewProviders: [existingControlContainer],
})
export class PartnershipFormComponent {
  @Input() partnersControl: FormArray<any>;
  @Input() isEditable: boolean;

  addOtherRequiredChange() {
    this.partnersControl.push(organisationStructureCreatePartnerFormControl());
  }
}
