import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'app-operator-details-name-template',
  imports: [GovukComponentsModule, SharedModule, ReactiveFormsModule],
  standalone: true,
  templateUrl: './operator-details-name-template.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorDetailsNameTemplateComponent {
  @Input() form: FormGroup<any>;
  @Output() readonly submitForm = new EventEmitter<FormGroup<any>>();
}
