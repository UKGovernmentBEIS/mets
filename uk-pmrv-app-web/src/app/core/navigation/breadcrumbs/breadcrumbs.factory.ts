import { InjectionToken } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

import { BreadcrumbItem } from '@core/navigation/breadcrumbs/breadcrumbs.interface';

export const BREADCRUMB_ITEMS = new InjectionToken<BehaviorSubject<BreadcrumbItem[]>>('Breadcrumb items', {
  providedIn: 'root',
  factory: () => new BehaviorSubject(null),
});
