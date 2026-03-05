import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

@Component({
  selector: 'app-aer-task-review',
  template: `
    <govuk-notification-banner *ngIf="notification" type="success">
      <h1 class="govuk-notification-banner__heading">Details updated</h1>
    </govuk-notification-banner>
    <app-page-heading>{{ heading }}</app-page-heading>
    <ng-content></ng-content>
    <a govukLink [routerLink]="returnToLink">Return to: {{ linkText }}</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AerTaskReviewComponent implements OnInit {
  @Input() notification: any;
  @Input() heading: string;
  @Input() breadcrumb: BreadcrumbItem[] | true;
  @Input() returnToLink = '..';
  @Input() linkText;

  constructor(private readonly breadcrumbs: BreadcrumbService) {}

  ngOnInit(): void {
    const breadcrumbs = this.breadcrumbs.breadcrumbItem$.getValue();
    if (!this.linkText && breadcrumbs?.length > 0) {
      let lastBreadcrumb = breadcrumbs[breadcrumbs.length - 1];
      lastBreadcrumb = lastBreadcrumb.link == null ? breadcrumbs[breadcrumbs.length - 2] : lastBreadcrumb;
      this.linkText = lastBreadcrumb.text;
    }
  }
}
