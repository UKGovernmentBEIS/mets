import { DecimalPipe, NgTemplateOutlet } from '@angular/common';
import {
  AfterViewInit,
  Component,
  computed,
  contentChild,
  ElementRef,
  inject,
  input,
  NO_ERRORS_SCHEMA,
  OnInit,
  Renderer2,
  viewChild,
} from '@angular/core';
import { ControlValueAccessor } from '@angular/forms';

import { distinctUntilChanged, takeUntil, tap } from 'rxjs';

import { LabelDirective } from '../directives';
import { ErrorMessageComponent } from '../error-message/error-message.component';
import { GovukValidators } from '../error-message/govuk-validators';
import { FormInput } from '../form/form-input';
import { GovukWidthClass } from '../types';
import { LabelSizeType } from './label-size.type';
import { HTMLInputType } from './text-input.type';

/*
 eslint-disable
 @angular-eslint/prefer-on-push-component-change-detection,
 @angular-eslint/component-selector
 */
@Component({
  selector: 'div[govuk-text-input]',
  imports: [ErrorMessageComponent, NgTemplateOutlet],
  templateUrl: './text-input.component.html',
  providers: [DecimalPipe],
  schemas: [NO_ERRORS_SCHEMA],
})
export class TextInputComponent extends FormInput implements ControlValueAccessor, OnInit, AfterViewInit {
  private readonly decimalPipe = inject(DecimalPipe);
  private readonly renderer = inject(Renderer2);

  readonly label = input<string>();
  readonly labelSize = input<LabelSizeType>('normal');
  readonly isLabelHidden = input(false);
  readonly hint = input<string>();
  readonly widthClass = input<GovukWidthClass>('govuk-!-width-full');
  readonly inputType = input<HTMLInputType>('text');
  readonly autoComplete = input('on');
  readonly inputMode = input<string>();
  readonly spellCheck = input<boolean>();
  readonly numberFormat = input<string>();
  readonly prefix = input<string>();
  readonly suffix = input<string>();
  readonly maxLength = input<number>();

  readonly templateLabel = contentChild(LabelDirective);
  readonly input = viewChild<ElementRef<HTMLInputElement>>('input');

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

  disabled: boolean;
  onChange: (_: any) => any;
  onBlur: (_: any) => any;

  constructor() {
    super();
  }

  override ngOnInit(): void {
    super.ngOnInit();
    if (this.inputType() === 'number') {
      const notNanValidator = GovukValidators.notNaN('Enter a numerical value');
      this.control.addValidators(notNanValidator);
      this.control.updateValueAndValidity();
    }
  }

  ngAfterViewInit(): void {
    this.writeValue(this.control.value);
    this.control.valueChanges
      .pipe(
        distinctUntilChanged((prev, curr) => {
          const previousValue = this.inputType() === 'number' ? Number(prev) : prev;
          const currentValue = this.inputType() === 'number' ? Number(curr) : curr;
          return previousValue === currentValue;
        }),
        tap((value) => this.handleInputValue(value)),
        takeUntil(this.destroy$),
      )
      .subscribe();
  }

  writeValue(value: any): void {
    const inputValue = this.input();
    if (inputValue) {
      const numberFormat = this.numberFormat();
      this.renderer.setProperty(
        inputValue.nativeElement,
        'value',
        inputValue.nativeElement === document.activeElement
          ? value
          : numberFormat && !Number.isNaN(Number(value))
            ? this.decimalPipe.transform(value, numberFormat)
            : value,
      );
    }
  }

  registerOnChange(onChange: any): void {
    this.onChange = onChange;
  }

  registerOnTouched(onBlur: any): void {
    this.onBlur = onBlur;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  getInputValue(event: Event): string {
    return (event.target as HTMLInputElement).value;
  }

  onFocus(): void {
    switch (this.inputType()) {
      case 'number':
        if (this.numberFormat()) {
          this.renderer.setProperty(this.input().nativeElement, 'value', this.control.value);
        }
        break;
    }
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.handleBlur(this.getInputValue(event));
    }
  }

  handleBlur(value?: string): void {
    this.onBlur(value);
  }

  private handleInputValue(value: string) {
    switch (this.inputType()) {
      case 'number':
        if (value === null) {
          break;
        } else if (value === '') {
          this.control.setValue(null);
        } else if (!isNaN(Number(value))) {
          this.control.setValue(Number(value));

          const inputValue = this.input();
          if (inputValue.nativeElement !== document.activeElement) {
            const numberFormat = this.numberFormat();
            this.renderer.setProperty(
              inputValue.nativeElement,
              'value',
              numberFormat ? this.decimalPipe.transform(value, numberFormat) : value,
            );
          }
        }
        break;
      case 'text':
        this.control.setValue(value ? (value.trim() === '' ? null : value.trim()) : value, {
          emitEvent: false,
          emitViewToModelChange: false,
          emitModelToViewChange: false,
        });
    }
  }
}
