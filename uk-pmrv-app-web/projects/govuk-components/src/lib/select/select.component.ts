import { Component, computed, contentChild, input, model } from '@angular/core';
import { ControlValueAccessor, ReactiveFormsModule } from '@angular/forms';

import { LabelDirective } from '../directives/label/label.directive';
import { ErrorMessageComponent } from '../error-message';
import { FormInput } from '../form/form-input';
import { LabelSizeType } from '../text-input/label-size.type';
import { GovukSelectOption } from './select.interface';
import { GovukSelectWidthClass } from './select.type';

/*
  eslint-disable
  @angular-eslint/prefer-on-push-component-change-detection,
  @angular-eslint/component-selector
*/
@Component({
  selector: 'div[govuk-select]',
  imports: [ReactiveFormsModule, ErrorMessageComponent],
  templateUrl: './select.component.html',
})
export class SelectComponent extends FormInput implements ControlValueAccessor {
  readonly label = input<string>();
  readonly labelSize = input<LabelSizeType>('normal');
  readonly isLabelHidden = input(false);
  readonly hint = input<string>();
  readonly widthClass = input<GovukSelectWidthClass>();
  readonly options = model<GovukSelectOption[]>();

  readonly templateLabel = contentChild(LabelDirective);

  readonly currentLabelSize = computed(() => {
    switch (this.labelSize()) {
      case 'small':
        return 'govuk-label govuk-label--s';
      case 'medium':
        return 'govuk-label govuk-label--m';
      case 'large':
        return 'govuk-label govuk-label--l';
      default:
        return 'govuk-label';
    }
  });

  constructor() {
    super();
  }

  writeValue(): void {}

  registerOnChange(): void {}

  registerOnTouched(): void {}
}
