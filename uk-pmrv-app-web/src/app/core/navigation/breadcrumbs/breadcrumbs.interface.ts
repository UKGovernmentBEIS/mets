import { Data } from '@angular/router';

export interface BreadcrumbItem {
  text: string;
  link?: any[];
  queryParams?: any;
  fragment?: string;
}

export type RouteBreadcrumb =
  | string
  | boolean
  | ((data: Data) => string)
  | { resolveText: (data: Data) => string; skipLink?: boolean | ((data: Data) => boolean) };
