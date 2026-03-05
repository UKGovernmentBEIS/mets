import { ChangeDetectionStrategy, Component, Inject, Input, OnChanges } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

import { BREADCRUMB_ITEMS, BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { DestroySubject } from '@core/services/destroy-subject.service';

@Component({
  selector: 'app-non-compliance-task',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <govuk-notification-banner *ngIf="notification" type="success">
          <h1 class="govuk-notification-banner__heading">Details updated</h1>
        </govuk-notification-banner>
        <ng-content></ng-content>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class NonComplianceTaskComponent implements OnChanges {
  @Input() notification: any;
  @Input() breadcrumb: BreadcrumbItem[] | true;

  lastBreadcrumb: BreadcrumbItem;

  constructor(@Inject(BREADCRUMB_ITEMS) private readonly breadcrumbs: BehaviorSubject<BreadcrumbItem[]>) {}

  ngOnChanges(): void {
    if (Array.isArray(this.breadcrumb)) {
      if (this.breadcrumb.length > 1) {
        this.lastBreadcrumb = this.breadcrumb[this.breadcrumb.length - 2];
      } else {
        this.lastBreadcrumb = this.breadcrumb[0];
      }
    } else if (this.breadcrumbs.getValue()?.length > 1) {
      const breadcrumbs = this.breadcrumbs.getValue();
      this.lastBreadcrumb = breadcrumbs[breadcrumbs.length - 2];
    } else {
      this.lastBreadcrumb = { text: 'Dashboard', link: ['/dashboard'] };
    }
  }
}
