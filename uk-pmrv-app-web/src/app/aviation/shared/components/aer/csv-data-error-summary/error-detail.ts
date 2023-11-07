export interface NestedMessageValidationErrorsArray {
  path: string;
  type: string;
  message?: string;
  columns?: string[];
  rows?: any[];
  controls?: NestedMessageValidationErrorsArray[];
}
