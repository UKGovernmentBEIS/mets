import { GovukWidthClass } from '../types/govuk-width-class.type';

export interface GovukTableColumn<T = any> {
  header: string;
  field: keyof T;
  widthClass?: GovukWidthClass | string;
  isSortable?: boolean;
  isHeader?: boolean;
  isNumeric?: boolean;
  hiddenHeader?: boolean;
}

export interface SortEvent<T = any> {
  column: GovukTableColumn<T>['field'];
  direction: 'ascending' | 'descending';
}
