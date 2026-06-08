import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { SharedModule } from '@shared/shared.module';

import { WasteQdrReturnLinkComponent } from '../waste-qdr-return-link/waste-qdr-return-link.component';

@Component({
  selector: 'app-waste-qdr-task-common',
  imports: [RouterModule, SharedModule, WasteQdrReturnLinkComponent],
  template: `
    <govuk-notification-banner *ngIf="notification" type="success">
      <h1 class="govuk-notification-banner__heading">Details updated</h1>
    </govuk-notification-banner>
    <app-page-heading *ngIf="heading" [caption]="caption">{{ heading }}</app-page-heading>
    <ng-content></ng-content>
    <app-waste-qdr-return-link [returnLink]="returnLink" [title]="returnLinkTitle"></app-waste-qdr-return-link>
  `,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WasteQdrTaskReviewComponent implements OnInit {
  @Input() notification: any;
  @Input() heading: string;
  @Input() caption: string;
  @Input() breadcrumb: BreadcrumbItem[] | true;
  @Input() returnLink = '..';
  @Input() returnLinkTitle: string;

  constructor(private readonly breadcrumbs: BreadcrumbService) {}

  ngOnInit(): void {
    const breadcrumbs = this.breadcrumbs.breadcrumbItem$.getValue();
    if (!this.returnLinkTitle && breadcrumbs?.length > 0) {
      let lastBreadcrumb = breadcrumbs[breadcrumbs.length - 1];
      lastBreadcrumb = lastBreadcrumb.link == null ? breadcrumbs[breadcrumbs.length - 2] : lastBreadcrumb;
      this.returnLinkTitle = lastBreadcrumb.text;
    }
  }
}
