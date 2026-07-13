import { ValidationErrors } from '@angular/forms';

export interface NestedMessageValidationErrors {
  self: ValidationErrors;
  controls: Record<string, NestedMessageValidationErrors>;
  path: string;
}

export type FlatSummaryError = Omit<NestedMessageValidationErrors, 'controls'>;
