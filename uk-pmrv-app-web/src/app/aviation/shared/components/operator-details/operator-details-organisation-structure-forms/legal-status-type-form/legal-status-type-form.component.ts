import { Component, Input } from '@angular/core';
import { FormArray } from '@angular/forms';

import { OperatorDetailsLegalStatusTypePipe } from '@aviation/shared/pipes/operator-details-legal-status-type.pipe';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { IndividualFormComponent } from '../individual-form/individual-form.component';
import { LimitedCompanyFormComponent } from '../limited-company-form/limited-company-form.component';
import { PartnershipFormComponent } from '../partnership-form/partnership-form.component';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-organisation-structure-legal-status-type-form',
  standalone: true,
  imports: [
    SharedModule,
    OperatorDetailsLegalStatusTypePipe,
    LimitedCompanyFormComponent,
    IndividualFormComponent,
    PartnershipFormComponent,
  ],
  templateUrl: './legal-status-type-form.component.html',
  viewProviders: [existingControlContainer],
})
export class LegalStatusTypeFormComponent {
  @Input() downloadUrl: string;
  @Input() partnersControl: FormArray<any>;
  @Input() isEditable: boolean;
}
