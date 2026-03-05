import { DecimalPipe } from '@angular/common';
import {
  AfterViewInit,
  Component,
  ContentChild,
  ElementRef,
  Input,
  OnInit,
  Optional,
  Renderer2,
  Self,
  ViewChild,
} from '@angular/core';
import { ControlContainer, ControlValueAccessor, NgControl } from '@angular/forms';

import { distinctUntilChanged, takeUntil, tap } from 'rxjs';

import BigNumber from 'bignumber.js';

import { LabelDirective } from '../directives';
import { GovukValidators } from '../error-message/govuk-validators';
import { FormService } from '../form/form.service';
import { FormInput } from '../form/form-input';
import { LabelSizeType } from './label-size.type';
import { GovukTextWidthClass, HTMLInputType } from './text-input.type';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'div[govuk-text-input]',
  templateUrl: './text-input.component.html',
  providers: [DecimalPipe],
})
export class TextInputComponent extends FormInput implements ControlValueAccessor, OnInit, AfterViewInit {
  @Input() hint: string;
  @Input() inputType: HTMLInputType = 'text';
  @Input() autoComplete = 'on';
  @Input() inputMode: string;
  @Input() spellCheck: boolean;
  @Input() numberFormat: string;
  @Input() widthClass: GovukTextWidthClass = 'govuk-!-width-full';
  @Input() prefix?: string;
  @Input() suffix?: string;
  @Input() isLabelHidden = true;
  @ContentChild(LabelDirective) templateLabel: LabelDirective;
  @ViewChild('input') input: ElementRef<HTMLInputElement>;
  currentLabel = 'Insert text';
  currentLabelSize = 'govuk-label';
  disabled: boolean;
  onChange: (_: any) => any;
  onBlur: (_: any) => any;

  constructor(
    @Self() @Optional() ngControl: NgControl,
    formService: FormService,
    private readonly decimalPipe: DecimalPipe,
    private readonly renderer: Renderer2,
    @Optional() container: ControlContainer,
  ) {
    super(ngControl, formService, container);
  }

  @Input() set label(label: string) {
    this.currentLabel = label;
    this.isLabelHidden = false;
  }

  @Input() set labelSize(size: LabelSizeType) {
    switch (size) {
      case 'small':
        this.currentLabelSize = 'govuk-label govuk-label--s';
        break;
      case 'medium':
        this.currentLabelSize = 'govuk-label govuk-label--m';
        break;
      case 'large':
        this.currentLabelSize = 'govuk-label govuk-label--l';
        break;
      default:
        this.currentLabelSize = 'govuk-label';
        break;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    if (this.inputType === 'number' || this.inputType === 'big-number') {
      const notNanValidator =
        this.inputType === 'number'
          ? GovukValidators.notNaN('Enter a numerical value')
          : GovukValidators.notNaBigNumber('Enter a numerical value');
      this.control.addValidators(notNanValidator);
      this.control.updateValueAndValidity();
    }
  }

  ngAfterViewInit(): void {
    this.writeValue(this.control.value);
    this.control.valueChanges
      .pipe(
        distinctUntilChanged((prev, curr) => prev === curr),
        tap((value) => this.handleInputValue(value)),
        takeUntil(this.destroy$),
      )
      .subscribe();
  }

  writeValue(value: any): void {
    if (this.input) {
      this.renderer.setProperty(
        this.input.nativeElement,
        'value',
        this.input.nativeElement === document.activeElement
          ? value
          : this.numberFormat && this.inputType !== 'big-number' && !Number.isNaN(Number(value))
            ? this.decimalPipe.transform(value, this.numberFormat)
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
    switch (this.inputType) {
      case 'number':
        if (this.numberFormat) {
          this.renderer.setProperty(this.input.nativeElement, 'value', this.control.value);
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

  private handleInputValue(value: string | number | BigNumber) {
    switch (this.inputType) {
      case 'number':
        if (typeof value === 'number') {
          // has already been handled
          return;
        }
        if (value === null) {
          return;
        } else if ((value as string).trim() === '') {
          this.control.setValue(null);
        } else {
          const valueAsString = value as string;
          const parts = valueAsString.split('.');
          if (parts.length > 1 && parts[1].charAt(parts[1].length - 1) === '0') {
            return;
          }

          const valueAsNumber = Number(value);
          if (isNaN(valueAsNumber) || (value as string).charAt((value as string).length - 1) === '.') {
            return;
          }

          this.control.setValue(valueAsNumber);

          if (this.input.nativeElement !== document.activeElement) {
            this.renderer.setProperty(
              this.input.nativeElement,
              'value',
              this.numberFormat ? this.decimalPipe.transform(value as string, this.numberFormat) : value,
            );
          }
        }
        break;
      case 'big-number':
        if (BigNumber.isBigNumber(value)) {
          // has already been handled
          return;
        }
        if (value === null) {
          return;
        } else if ((value as string).trim() === '') {
          this.control.setValue(null);
        } else {
          const valueAsString = value as string;
          if (valueAsString.charAt(valueAsString.length - 1) === '.') {
            return;
          }

          const parts = valueAsString.split('.');
          if (parts.length > 1 && parts[1].charAt(parts[1].length - 1) === '0') {
            return;
          }

          const valueAsBigNumber = new BigNumber(value);
          if (valueAsBigNumber.isNaN()) {
            return;
          }
          this.control.setValue(valueAsBigNumber);

          if (this.input.nativeElement !== document.activeElement) {
            this.renderer.setProperty(this.input.nativeElement, 'value', value);
          }
        }
        break;
      case 'text':
        this.control.setValue(value ? ((value as string).trim() === '' ? null : (value as string).trim()) : value, {
          emitEvent: false,
          emitViewToModelChange: false,
          emitModelToViewChange: false,
        });
    }
  }
}
