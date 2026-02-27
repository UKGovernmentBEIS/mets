import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'app-has-subsidiary-companies-template',
  imports: [GovukComponentsModule, SharedModule],
  templateUrl: './has-subsidiary-companies.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HasSubsidiaryCompaniesTemplateComponent {
  @Input() form: FormGroup;
  @Output() readonly submitForm = new EventEmitter<FormGroup<any>>();
}
