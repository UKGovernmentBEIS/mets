import { FormControl, ValidatorFn } from '@angular/forms';

/**
 * Validates provided length to actual fileName length
 */
export function fileNameLengthValidator(maxLength: number, message: string): ValidatorFn {
  return (control: FormControl): { [key: string]: any } | null => {
    const file = control.value;

    if (file instanceof File) {
      const fileName = file.name;
      if (fileName.length > maxLength) {
        return { fileNameTooLong: { message } };
      }
    }

    return null;
  };
}
