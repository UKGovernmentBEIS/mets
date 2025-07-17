import { AfterViewInit, Component, Input, Optional, Self } from '@angular/core';
import { ControlContainer, ControlValueAccessor, NgControl } from '@angular/forms';

import { distinctUntilChanged, takeUntil, tap } from 'rxjs';

import { FormService } from '../form/form.service';
import { FormInput } from '../form/form-input';
import { LabelSizeType } from './label-size.type';

/*
  eslint-disable
  @angular-eslint/prefer-on-push-component-change-detection,
  @typescript-eslint/no-empty-function
*/
@Component({
  selector: 'div[govuk-textarea]',
  templateUrl: './textarea.component.html',
})
export class TextareaComponent extends FormInput implements ControlValueAccessor, AfterViewInit {
  private static readonly WARNING_PERCENTAGE = 0.99;

  @Input() hint: string;
  @Input() rows = '5';
  @Input() maxLength: number;
  @Input() isLabelHidden = true;
  currentLabel = 'Insert text details';
  currentLabelSize = 'govuk-label';
  onBlur: (_: any) => any;

  constructor(
    @Self() @Optional() ngControl: NgControl,
    formService: FormService,
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

  writeValue(): void {}

  registerOnChange(): void {}

  registerOnTouched(onBlur: any): void {
    this.onBlur = onBlur;
  }

  setDisabledState(): void {}

  getInputValue(event: Event): string {
    return (event.target as HTMLTextAreaElement).value;
  }

  handleBlur(value: string): void {
    this.onBlur(value);
  }

  exceedsMaxLength(length: number): boolean {
    return length > this.maxLength;
  }

  approachesMaxLength(length: number): boolean {
    return !this.exceedsMaxLength(length) && length >= this.maxLength * TextareaComponent.WARNING_PERCENTAGE;
  }

  ngAfterViewInit(): void {
    this.control.valueChanges
      .pipe(
        distinctUntilChanged((prev, curr) => prev === curr),
        tap((value) => {
          const trimmedValue = value ? (value.trim() === '' ? null : value.trim()) : value;
          this.control.setValue(trimmedValue, {
            emitEvent: false,
            emitViewToModelChange: false,
            emitModelToViewChange: false,
          });
        }),
        takeUntil(this.destroy$),
      )
      .subscribe();
  }
}
