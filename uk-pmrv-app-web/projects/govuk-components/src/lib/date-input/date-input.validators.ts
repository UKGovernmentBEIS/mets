import { AbstractControl, UntypedFormGroup, ValidatorFn } from '@angular/forms';

// @dynamic
export class DateInputValidators {
  static dateFieldValidator(identifier: string, min: number, max: number): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null =>
      control.value && (control.value < min || control.value > max) ? { [identifier]: true } : null;
  }

  static minMaxDateValidator(min: Date, max: Date): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null =>
      control.value && min && control.value < min
        ? { minDate: true }
        : control.value && max && control.value > max
        ? { maxDate: true }
        : null;
  }

  static dateIncompleteValidator: ValidatorFn = (fg: UntypedFormGroup) => {
    const day = fg.get('day').value;
    const month = fg.get('month').value;
    const year = fg.get('year').value;
    return (day || month || year) && (!year || !month || !day) ? { incomplete: true } : null;
  };

  static incorrectDayValidator: ValidatorFn = (fg: UntypedFormGroup) => {
    const day = fg.get('day').value;
    const month = fg.get('month').value;

    return (Number(day) > 29 && Number(month) === 2) ||
      (DateInputValidators.isShortMonth(Number(month)) && Number(day) > 30) ||
      Number(day) > 31
      ? { day: true }
      : null;
  };

  static isLeapYear(year: number): boolean {
    // eslint-disable-next-line no-bitwise
    return !(year & 3 || (!(year % 25) && year & 15));
  }

  static isShortMonth(month: number): boolean {
    return month === 2 || month === 4 || month === 6 || month === 9 || month === 11;
  }

  static buildDate({ year, month, day }): Date | null {
    return !year || !month || !day ? null : new Date(Date.UTC(Number(year), Number(month) - 1, Number(day)));
  }

  static combinedRulesValidator = (fg: UntypedFormGroup, isRequired = false) => {
    return (fg: UntypedFormGroup) => {
      return this.getCombinedValidationResults(fg, isRequired);
    };
  };

  static getCombinedValidationResults(fg: UntypedFormGroup, isRequired) {
    return isRequired && this.isEmpty(fg)
      ? { isEmpty: true }
      : this.isIncomplete(fg)
      ? { isIncomplete: true }
      : this.isUnrealDate(fg) && !this.isEmpty(fg)
      ? { isUnrealDate: true }
      : null;
  }

  static isEmpty(fg: UntypedFormGroup): boolean {
    const day = fg.get('day').value;
    const month = fg.get('month').value;
    const year = fg.get('year').value;
    return !day && !month && !year;
  }

  static isIncomplete(fg: UntypedFormGroup): boolean {
    const day = fg.get('day').value;
    const month = fg.get('month').value;
    const year = fg.get('year').value;
    return (day || month || year) && (!year || !month || !day);
  }

  static isUnrealDate(fg: UntypedFormGroup): boolean {
    const day = fg.get('day').value;
    const month = fg.get('month').value;
    const year = fg.get('year').value;

    const isBetweenTheAllowedValues = (value, min, max) => {
      return /^\d+$/.test(value) && value >= min && value <= max;
    };
    const isNotCorrectLeapYearDate = () => {
      return Number(day) === 29 && Number(month) === 2 && !DateInputValidators.isLeapYear(Number(year));
    };

    const isIncorrectDay =
      (Number(day) > 29 && Number(month) === 2) ||
      (DateInputValidators.isShortMonth(Number(month)) && Number(day) > 30) ||
      Number(day) > 31;

    return (
      !isBetweenTheAllowedValues(day, 1, 31) ||
      !isBetweenTheAllowedValues(month, 1, 12) ||
      !isBetweenTheAllowedValues(year, 1900, 2100) ||
      isNotCorrectLeapYearDate() ||
      isIncorrectDay
    );
  }
}
