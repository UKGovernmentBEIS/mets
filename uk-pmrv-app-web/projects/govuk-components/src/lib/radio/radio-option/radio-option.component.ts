import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  contentChild,
  effect,
  forwardRef,
  input,
  TemplateRef,
  viewChild,
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

import { ConditionalContentDirective } from '../../directives';

@Component({
  selector: 'govuk-radio-option',
  imports: [],
  templateUrl: './radio-option.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => RadioOptionComponent),
      multi: true,
    },
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RadioOptionComponent<T> implements ControlValueAccessor {
  readonly value = input<T>();
  readonly label = input<string>();
  readonly hint = input<string>();
  readonly divider = input<boolean>();
  readonly disable = input<boolean>();

  readonly conditional = contentChild(ConditionalContentDirective);
  readonly conditionalTemplate = viewChild<TemplateRef<any>>('conditionalTemplate');
  readonly optionTemplate = viewChild<TemplateRef<any>>('optionTemplate');
  isChecked: boolean;
  index: number;
  isDisabled: boolean;
  onChange: (_: T) => any;
  onBlur: () => any;
  groupIdentifier: string;

  constructor(private readonly changeDetectorRef: ChangeDetectorRef) {
    effect(() => {
      const value = this.disable();
      this.isDisabled = value;
    });
  }

  get identifier(): string {
    return `${this.groupIdentifier}-option${this.index}`;
  }

  registerOnChange(onChange: (_: T) => any): void {
    this.onChange = onChange;
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = onBlur;
  }

  writeValue(newValue: T): void {
    this.isChecked = newValue === this.value();
    this.setConditionalDisabledState();
    this.changeDetectorRef.detectChanges();
  }

  setDisabledState(isDisabled: boolean) {
    this.isDisabled = isDisabled || this.disable();
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
