import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

import { OperatorDetailsFlightIdentificationTypePipe } from '@aviation/shared/pipes/operator-details-flight-identification-type.pipe';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'app-operator-details-flight-identification-template',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReactiveFormsModule, OperatorDetailsFlightIdentificationTypePipe],
  templateUrl: './operator-details-flight-identification-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class OperatorDetailsFlightIdentificationTemplateComponent {
  @Input() form: FormGroup<any>;
  @Output() readonly submitForm = new EventEmitter<FormGroup<any>>();
}
