import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  contentChild,
  inject,
  input,
  TemplateRef,
  viewChild,
} from '@angular/core';
import { ControlValueAccessor } from '@angular/forms';

import { ConditionalContentDirective } from '../../directives';

@Component({
  selector: 'govuk-checkbox',
  templateUrl: './checkbox.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckboxComponent<T> implements ControlValueAccessor {
  readonly changeDetectorRef = inject(ChangeDetectorRef);

  readonly value = input<T>();
  readonly label = input<string>();
  readonly hint = input<string>();
  readonly divider = input<string>();
  readonly conditional = contentChild(ConditionalContentDirective);
  readonly conditionalTemplate = viewChild<TemplateRef<any>>('conditionalTemplate');
  readonly optionTemplate = viewChild<TemplateRef<any>>('checkboxTemplate');

  isChecked: boolean;
  index: number;
  isDisabled: boolean;
  isTouched: boolean;
  onBlur: () => any;
  onChange: (event: Event) => any;
  groupIdentifier: string;

  get identifier(): string {
    return `${this.groupIdentifier}-${this.index}`;
  }

  registerOnChange(onChange: () => any): void {
    this.onChange = (event) => {
      this.writeValue((event.target as HTMLInputElement).checked);
      onChange();
    };
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = () => {
      this.isTouched = true;
      onBlur();
    };
  }

  writeValue(value: boolean): void {
    this.isChecked = value;
    this.setConditionalDisabledState();
  }

  setDisabledState(isDisabled: boolean) {
    this.isDisabled = isDisabled;
    this.setConditionalDisabledState();
    this.changeDetectorRef.markForCheck();
  }

  private setConditionalDisabledState() {
    if (this.isChecked && !this.isDisabled) {
      this.conditional()?.enableControls();
    } else {
      this.conditional()?.disableControls();
    }
  }
}
