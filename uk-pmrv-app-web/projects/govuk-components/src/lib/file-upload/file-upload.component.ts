import { Component, computed, inject, input } from '@angular/core';
import { ControlValueAccessor, NgControl, UntypedFormControl } from '@angular/forms';

import { ErrorMessageComponent } from '../error-message/error-message.component';
import { FormService } from '../form/form.service';
import { LabelSizeType } from '../text-input/label-size.type';

/*
  eslint-disable
  @typescript-eslint/no-unused-vars,
  @angular-eslint/component-selector,
  @angular-eslint/prefer-on-push-component-change-detection,
*/
@Component({
  selector: 'div[govukFileUpload],govuk-file-upload',
  imports: [ErrorMessageComponent],
  templateUrl: './file-upload.component.html',
  host: {
    '[class.govuk-!-display-block]': 'govukDisplayBlock',
    '[class.govuk-form-group]': 'govukFormGroupClass',
    '[class.govuk-form-group--error]': 'govukFormGroupErrorClass',
  },
})
export class FileUploadComponent implements ControlValueAccessor {
  ngControl = inject(NgControl, { self: true, optional: true })!;
  private formService = inject(FormService);

  readonly label = input<string>();
  readonly isLabelHidden = input(false);
  readonly labelSize = input<LabelSizeType>('normal');
  readonly accepted = input<string>();
  readonly isMultiple = input<boolean>();

  readonly govukDisplayBlock = true;
  readonly govukFormGroupClass = true;

  get govukFormGroupErrorClass(): boolean {
    return this.control?.invalid && this.control?.touched;
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

  constructor() {
    const ngControl = this.ngControl;

    ngControl.valueAccessor = this;
  }

  get control(): UntypedFormControl {
    return this.ngControl.control as UntypedFormControl;
  }

  get isTouched(): boolean {
    return this.control.touched;
  }

  get identifier(): string {
    return this.formService.getControlIdentifier(this.ngControl);
  }

  onChange(event: Event): void {
    this.control.patchValue((event?.target as HTMLInputElement)?.files);
  }

  onBlur(): void {
    this.control.markAsTouched();
  }

  writeValue(_: any): void {}

  registerOnChange(_: any): void {}

  registerOnTouched(_: any): void {}
}
