import { FormControl, ValidatorFn } from '@angular/forms';

/**
 * Validates provided extensions and mimetypes, to file extensions and file mimetypes accordingly
 * @example
 * fileExtensionValidator(['csv'], ['text/csv', 'application/vnd.ms-excel'], 'Only CSV files are accepted')
 */
export function fileExtensionValidator(
  allowedExtensions: string[],
  allowedMimeTypes: string[],
  message: string,
): ValidatorFn {
  return (control: FormControl): { [key: string]: any } | null => {
    const file = control.value;
    if (file) {
      const extension = file.name.split('.').pop().toLowerCase();
      const mimeType = file.type;
      if (!allowedExtensions.includes(extension) || !allowedMimeTypes.includes(mimeType)) {
        return { extensionNotAllowed: { message } };
      }
    }
    return null;
  };
}
