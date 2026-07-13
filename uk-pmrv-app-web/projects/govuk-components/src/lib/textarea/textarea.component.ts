import { AfterViewInit, Component, computed, contentChild, input } from '@angular/core';
import { ControlValueAccessor, ReactiveFormsModule } from '@angular/forms';

import { distinctUntilChanged, takeUntil, tap } from 'rxjs';

import { LabelDirective } from '../directives';
import { ErrorMessageComponent } from '../error-message/error-message.component';
import { FormInput } from '../form/form-input';
import { SafeHtmlPipe } from '../pipes/safe-html.pipe';
import { LabelSizeType } from './label-size.type';

/*
  eslint-disable
  @angular-eslint/prefer-on-push-component-change-detection,
  @typescript-eslint/no-empty-function
*/
@Component({
  selector: 'div[govuk-textarea]',
  imports: [ErrorMessageComponent, ReactiveFormsModule, SafeHtmlPipe],
  templateUrl: './textarea.component.html',
})
export class TextareaComponent extends FormInput implements ControlValueAccessor, AfterViewInit {
  private static readonly WARNING_PERCENTAGE = 0.9;

  readonly hint = input<string>();
  readonly rows = input('5');
  readonly maxLength = input<number>();
  readonly isLabelHidden = input(false);
  readonly label = input<string>();
  readonly labelSize = input<LabelSizeType>('normal');

  readonly templateLabel = contentChild(LabelDirective);

  onBlur: (_: any) => any;

  constructor() {
    super();
  }

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
    return length > this.maxLength();
  }

  approachesMaxLength(length: number): boolean {
    return !this.exceedsMaxLength(length) && length >= this.maxLength() * TextareaComponent.WARNING_PERCENTAGE;
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
