import { NgTemplateOutlet } from '@angular/common';
import { AfterContentInit, Component, contentChildren, input } from '@angular/core';
import { ControlValueAccessor } from '@angular/forms';

import { ErrorMessageComponent } from '../error-message';
import { FieldsetDirective, FieldsetHintDirective, LegendDirective, LegendSizeType } from '../fieldset';
import { FormInput } from '../form/form-input';
import { CheckboxComponent } from './checkbox/checkbox.component';
/*
  eslint-disable
  @angular-eslint/prefer-on-push-component-change-detection,
  @angular-eslint/component-selector
 */
@Component({
  selector: 'div[govuk-checkboxes]',
  imports: [ErrorMessageComponent, NgTemplateOutlet, LegendDirective, FieldsetHintDirective, FieldsetDirective],
  templateUrl: './checkboxes.component.html',
})
export class CheckboxesComponent<T> extends FormInput implements AfterContentInit, ControlValueAccessor {
  readonly label = input<string>();
  readonly labelSize = input<LegendSizeType>('normal');
  readonly isLabelHidden = input(false);
  readonly hint = input<string>();
  readonly size = input<'small'>();

  readonly options = contentChildren(CheckboxComponent);

  private onBlur: () => any;
  private onChange: (value: T[]) => void;
  private currentValue: T[] = [];

  constructor() {
    super();
  }

  ngAfterContentInit(): void {
    this.options().forEach((option, index) => {
      option.groupIdentifier = this.identifier;
      option.index = index;
      option.registerOnChange(() => {
        this.currentValue = this.options()
          .filter((option) => option.isChecked)
          .map((option) => option.value());
        this.onChange(this.currentValue);
      });
      option.registerOnTouched(() => this.onInputBlur());
      option.changeDetectorRef.markForCheck();
    });

    this.writeValue(this.control.value);
    this.setDisabledState(this.control.disabled);
  }

  writeValue(value: T[]): void {
    this.currentValue = value;
    this.options()?.forEach((option) => option.writeValue(value?.includes(option.value()) ?? false));
  }

  registerOnChange(fn: (value: T[]) => void) {
    this.onChange = fn;
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = onBlur;
  }

  setDisabledState(isDisabled: boolean): void {
    this.options()?.forEach((option) => option.setDisabledState(isDisabled));
  }

  onInputBlur(): void {
    const options = this.options();
    if (!options || Array.from(options).every((option) => option.isTouched)) {
      this.onBlur();
    }
  }
}
