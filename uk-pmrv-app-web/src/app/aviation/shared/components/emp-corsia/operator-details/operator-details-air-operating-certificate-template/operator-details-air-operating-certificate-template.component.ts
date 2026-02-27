import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule, GovukSelectOption } from 'govuk-components';

@Component({
  selector: 'app-operator-details-air-operating-certificate-template',
  imports: [GovukComponentsModule, SharedModule, ReactiveFormsModule],
  standalone: true,
  templateUrl: './operator-details-air-operating-certificate-template.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorDetailsAirOperatingCertificateTemplateComponent {
  @Input() form: FormGroup<any>;
  @Input() issuingAuthorityOptions: GovukSelectOption<string>[];
  @Input() downloadUrl: string;
  @Output()
  readonly submitForm = new EventEmitter<FormGroup<any>>();
}
