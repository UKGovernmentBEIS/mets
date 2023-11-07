import { Component, Input } from '@angular/core';

import { LocationStateFormComponent } from '@aviation/shared/components/location-state-form/location-state-form.component';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-organisation-structure-limited-company-form',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, LocationStateFormComponent],
  templateUrl: './limited-company-form.component.html',
  viewProviders: [existingControlContainer],
})
export class LimitedCompanyFormComponent {
  @Input() downloadUrl: string;
}
