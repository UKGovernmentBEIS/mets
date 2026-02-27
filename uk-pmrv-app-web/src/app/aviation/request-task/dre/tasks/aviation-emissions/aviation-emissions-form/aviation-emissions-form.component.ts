import { Component, Input } from '@angular/core';

import { AviationDreEmissionsTypePipe } from '@aviation/shared/pipes/aviation-dre-emissions-type.pipe';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { SupportingDocumentsViewModel } from '../aviation-emissions-form.provider';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-aviation-emissions-form',
  imports: [GovukComponentsModule, SharedModule, AviationDreEmissionsTypePipe],
  templateUrl: './aviation-emissions-form.component.html',
  viewProviders: [existingControlContainer],
})
export class AviationEmissionsFormComponent {
  @Input() vm: SupportingDocumentsViewModel;
}
