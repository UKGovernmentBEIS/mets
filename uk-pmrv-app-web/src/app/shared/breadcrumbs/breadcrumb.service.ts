import { Inject, Injectable } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

import { BREADCRUMB_ITEMS, BreadcrumbItem } from '@core/navigation/breadcrumbs';

@Injectable({
  providedIn: 'root',
})
export class BreadcrumbService {
  constructor(@Inject(BREADCRUMB_ITEMS) readonly breadcrumbItem$: BehaviorSubject<BreadcrumbItem[]>) {}

  show(items: BreadcrumbItem[]): void {
    this.breadcrumbItem$.next(items);
  }

  showDashboardBreadcrumb(url: string): void {
    const showAviation = url.startsWith('/aviation');
    this.breadcrumbItem$.next([
      {
        text: 'Dashboard',
        link: showAviation ? ['/aviation/dashboard'] : ['dashboard'],
      },
    ]);
  }

  clear(): void {
    this.breadcrumbItem$.next(null);
  }
}
