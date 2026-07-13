import { NgTemplateOutlet } from '@angular/common';
import { AfterContentChecked, AfterContentInit, Component, contentChildren, input } from '@angular/core';
import { ControlValueAccessor } from '@angular/forms';

import { ErrorMessageComponent } from '../error-message/error-message.component';
import { LegendSizeType } from '../fieldset';
import { FieldsetDirective } from '../fieldset/fieldset.directive';
import { FieldsetHintDirective } from '../fieldset/fieldset-hint.directive';
import { LegendDirective } from '../fieldset/legend.directive';
import { FormInput } from '../form/form-input';
import { GovukSpacingUnit } from '../types';
import { RadioOptionComponent } from './radio-option/radio-option.component';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'div[govuk-radio]',
  imports: [FieldsetDirective, LegendDirective, FieldsetHintDirective, ErrorMessageComponent, NgTemplateOutlet],
  templateUrl: './radio.component.html',
})
export class RadioComponent<T>
  extends FormInput
  implements AfterContentInit, AfterContentChecked, ControlValueAccessor
{
  readonly legend = input<string>();
  readonly hint = input<string>();
  readonly radioSize = input<'medium' | 'large'>('large');
  readonly isInline = input(false);
  readonly legendSize = input<LegendSizeType>('normal');
  readonly legendBottomSpacing = input<GovukSpacingUnit>(3);
  readonly options = contentChildren(RadioOptionComponent);
  private onChange: (_: T) => any;
  private onBlur: () => any;
  private isDisabled: boolean;

  constructor() {
    super();
  }

  ngAfterContentChecked(): void {
    this.options().forEach((option, index) => {
      option.index = index;
      option.groupIdentifier = this.identifier;
      option.registerOnChange(this.onChange);
    });
    this.registerOnTouched(this.onBlur);
  }

  ngAfterContentInit() {
    this.setDisabledState(this.isDisabled);
    this.writeValue(this.control.value);
  }

  writeValue(value: T): void {
    this.options()?.forEach((option) => option.writeValue(value));
  }

  registerOnChange(onChange: (_: T) => any): void {
    this.onChange = (option) => {
      this.writeValue(option);
      onChange(option);
    };
    this.options()?.forEach((option) => option.registerOnChange(this.onChange));
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = onBlur;
    this.options()?.forEach((option) => option.registerOnTouched(this.onBlur));
  }

  setDisabledState(isDisabled: boolean) {
    this.isDisabled = isDisabled;
    this.options()?.forEach((option) => option.setDisabledState(isDisabled));
  }
}
