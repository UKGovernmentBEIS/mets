import { ChangeDetectorRef, Directive, inject, input, OnDestroy, OnInit } from '@angular/core';
import {
  ControlContainer,
  ControlValueAccessor,
  FormGroupDirective,
  NgControl,
  NgForm,
  UntypedFormControl,
} from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { FormService } from './form.service';

@Directive({
  host: {
    '[class.govuk-!-display-block]': 'govukDisplayBlock',
    '[class.govuk-form-group]': 'govukFormGroupClass',
    '[class.govuk-form-group--error]': 'govukFormGroupErrorClass',
  },
})
export abstract class FormInput implements ControlValueAccessor, OnInit, OnDestroy {
  private readonly ngControl = inject(NgControl);
  private readonly formService = inject(FormService);
  // intentional divergence from mrtm: optional because test contexts use [formControl] without [formGroup]
  private readonly container = inject(ControlContainer, { optional: true });
  private readonly cdr = inject(ChangeDetectorRef);

  readonly govukDisplayBlock = true;
  readonly govukFormGroupClass = true;
  protected readonly destroy$ = new Subject<void>();
  private isSubmitted = false;
  readonly idSuffix = input<string>('');

  protected constructor() {
    this.ngControl.valueAccessor = this;
  }

  get govukFormGroupErrorClass(): boolean {
    return this.shouldDisplayErrors;
  }

  get identifier(): string {
    const base = this.formService.getControlIdentifier(this.ngControl);
    return this.idSuffix() ? `${base}-${this.idSuffix()}` : base;
  }

  get control(): UntypedFormControl {
    return this.ngControl.control as UntypedFormControl;
  }

  get shouldDisplayErrors(): boolean {
    return this.control?.invalid && (!this.form || this.isSubmitted);
  }

  private get form(): FormGroupDirective | NgForm | null {
    return this.container &&
      (this.container.formDirective instanceof FormGroupDirective || this.container.formDirective instanceof NgForm)
      ? this.container.formDirective
      : null;
  }

  ngOnInit(): void {
    this.form?.ngSubmit.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.isSubmitted = true;
      this.cdr?.markForCheck();
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  abstract writeValue(value: any): void;

  abstract registerOnChange(onChange: (value: any) => any): void;

  abstract registerOnTouched(onBlur: () => any): void;
}
