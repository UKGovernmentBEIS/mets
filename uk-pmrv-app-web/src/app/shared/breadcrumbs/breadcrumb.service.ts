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

  addToLastBreadcrumbAndShow(urlPart: string | string[]): void {
    urlPart = Array.isArray(urlPart) ? [...urlPart] : [urlPart];
    const breadcrumbs = this.breadcrumbItem$.getValue();
    if (breadcrumbs && breadcrumbs.length) {
      const lastBreadcrumb = breadcrumbs[breadcrumbs.length - 1];
      if (lastBreadcrumb.link) {
        this.show([
          ...breadcrumbs.slice(0, breadcrumbs.length - 1),
          { ...lastBreadcrumb, link: [...lastBreadcrumb.link, ...urlPart] },
        ]);
      } else if (breadcrumbs.length > 1) {
        const lastBreadcrumbWithLink = breadcrumbs[breadcrumbs.length - 2];
        this.show([
          ...breadcrumbs.slice(0, breadcrumbs.length - 2),
          { ...lastBreadcrumbWithLink, link: [...lastBreadcrumbWithLink.link, ...urlPart] },
          lastBreadcrumb,
        ]);
      }
    }
  }

  cutLastBreadcrumbWithLinkandShow() {
    const breadcrumbs = this.breadcrumbItem$.getValue();
    if (breadcrumbs && breadcrumbs.length) {
      const lastBreadcrumb = breadcrumbs[breadcrumbs.length - 1];
      if (lastBreadcrumb.link) {
        this.show([...breadcrumbs.slice(0, breadcrumbs.length - 1)]);
      } else if (breadcrumbs.length > 1) {
        this.show([...breadcrumbs.slice(0, breadcrumbs.length - 2), lastBreadcrumb]);
      }
    }
  }
}
