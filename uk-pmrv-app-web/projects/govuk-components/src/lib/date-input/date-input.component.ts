import { DatePipe } from '@angular/common';
import { Component, DoCheck, Input, OnDestroy, OnInit, Optional, Self } from '@angular/core';
import {
  ControlContainer,
  ControlValueAccessor,
  NgControl,
  UntypedFormBuilder,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';

import { BehaviorSubject, combineLatest, filter, takeUntil } from 'rxjs';

import { GovukValidators } from '../error-message/govuk-validators';
import { FormService } from '../form/form.service';
import { FormInput } from '../form/form-input';
import { DateInputValidators } from './date-input.validators';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'div[govuk-date-input]',
  templateUrl: './date-input.component.html',
  providers: [DatePipe],
})
export class DateInputComponent extends FormInput implements ControlValueAccessor, OnInit, DoCheck, OnDestroy {
  @Input() label: string;
  @Input() hint: string;
  @Input() min: Date;
  @Input() max: Date;
  @Input() errorMessage: string;
  @Input() isRequired: boolean;

  formGroup = this.formBuilder.group(
    {
      day: [null],
      month: [null],
      year: [null],
    },
    { updateOn: 'change' },
  );

  private initialValidator: ValidatorFn;
  private touch$ = new BehaviorSubject(false);
  private min$ = new BehaviorSubject(null);
  private max$ = new BehaviorSubject(null);
  private onChange: (value: { year: number; month: number; day: number }) => void;
  private onBlur: () => any;

  constructor(
    @Self() @Optional() ngControl: NgControl,
    formService: FormService,
    private readonly datePipe: DatePipe,
    @Optional() container: ControlContainer,
    private readonly formBuilder: UntypedFormBuilder,
  ) {
    super(ngControl, formService, container);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.initialValidator = this.control.validator;
    this.control.setValidators([this.validate.bind(this)]);
    this.control.updateValueAndValidity();

    this.touch$
      .pipe(
        takeUntil(this.destroy$),
        filter((value) => value),
      )
      .subscribe(() => this.formGroup.markAllAsTouched());

    combineLatest([this.min$, this.max$])
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.control.updateValueAndValidity());

    this.formGroup.valueChanges
      .pipe(
        takeUntil(this.destroy$),
        filter(() => !!this.onChange),
      )
      .subscribe((value) => this.onChange(value));
  }

  ngDoCheck(): void {
    if (this.touch$.getValue() !== this.control.touched) {
      this.touch$.next(this.control.touched);
    }

    if (this.min$.getValue() !== this.min) {
      this.min$.next(this.min);
    }

    if (this.max$.getValue() !== this.max) {
      this.max$.next(this.max);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.unsubscribe();
  }

  hasFieldError(identifier: string): boolean {
    if (this.shouldDisplayErrors) {
      const fieldControl = this.formGroup.get(identifier);

      return (
        fieldControl.invalid ||
        (!fieldControl.value && this.control.errors?.incomplete) ||
        this.control.errors?.minDate ||
        this.control.errors?.maxDate ||
        (identifier === 'day' && (this.control.errors?.leapYear || this.control.errors?.day))
      );
    } else {
      return false;
    }
  }

  registerOnChange(onChange: (_: Date) => void): void {
    this.onChange = (values) => onChange(this.formGroup.invalid ? null : DateInputValidators.buildDate(values));
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = onBlur;
  }

  writeValue(value: Date | null): void {
    if (value) {
      this.formGroup.setValue({
        day: value.getDate(),
        month: value.getMonth() + 1,
        year: value.getFullYear(),
      });
    }
  }

  setDisabledState(isDisabled: boolean): void {
    if (isDisabled) {
      this.formGroup.disable();
    } else {
      this.formGroup.enable();
    }
  }

  onInputBlur(): void {
    if (Object.values(this.formGroup.controls).every((control) => control.touched)) {
      this.onBlur();
    }
  }

  private validate(): ValidationErrors {
    return {
      ...this.formGroup.get('day').errors,
      ...this.formGroup.get('month').errors,
      ...this.formGroup.get('year').errors,
      ...this.validateControl(),
    };
  }

  private validateControl(): ValidationErrors {
    return {
      ...(this.initialValidator ? this.initialValidator(this.control) : null),
      ...this.combinedRulesValidator(),
      ...this.beforeOrAfterDateValidator(),
    };
  }

  // basic validators
  private combinedRulesValidator(): ValidationErrors {
    const validationResults = DateInputValidators.getCombinedValidationResults(this.formGroup, this.isRequired);

    const errorMessage = validationResults?.isEmpty
      ? this.errorMessage || 'Enter a date'
      : validationResults?.isIncomplete
        ? 'Enter a full date'
        : validationResults?.isUnrealDate
          ? 'Enter a real date'
          : '';
    return {
      ...GovukValidators.builder(
        errorMessage,
        DateInputValidators.combinedRulesValidator(this.formGroup, this.isRequired),
      )(this.formGroup),
    };
  }

  // other validators
  private beforeOrAfterDateValidator(): ValidationErrors {
    const errorMessage =
      DateInputValidators.buildDate(this.formGroup.value) < this.min
        ? `This date must be the same as or after ${this.datePipe.transform(this.min, 'd MMMM y')}`
        : DateInputValidators.buildDate(this.formGroup.value) > this.max
          ? `This date must be the same as or before ${this.datePipe.transform(this.max, 'd MMMM y')}`
          : '';
    return {
      ...GovukValidators.builder(
        errorMessage,
        DateInputValidators.minMaxDateValidator(this.min, this.max),
      )(this.control),
    };
  }
}
