import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { RouterLinkWithHref } from '@angular/router';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'app-has-subsidiary-companies-template',
  templateUrl: './has-subsidiary-companies.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [GovukComponentsModule, SharedModule, RouterLinkWithHref, ReturnToLinkComponent],
})
export class HasSubsidiaryCompaniesTemplateComponent {
  @Input() form: FormGroup;
  @Output() readonly submitForm = new EventEmitter<FormGroup<any>>();
}
