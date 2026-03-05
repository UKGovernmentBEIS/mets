import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule, GovukSelectOption } from 'govuk-components';

@Component({
  selector: 'app-operator-details-operating-license-template',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReactiveFormsModule],
  templateUrl: './operator-details-operating-license-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class OperatorDetailsOperatingLicenseTemplateComponent {
  @Input() form: FormGroup<any>;
  @Input() issuingAuthorityOptions: GovukSelectOption<string>[];
  @Output() readonly submitForm = new EventEmitter<FormGroup<any>>();
}
