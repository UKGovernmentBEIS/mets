import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

@Component({
  selector: 'app-not-allowed',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Response sent to regulator"> </govuk-panel>
      </div>
    </div>
    <a govukLink [routerLink]="isAviation + '/dashboard'"> Return to dashboard </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent implements OnInit {
  isAviation = this.router.url.includes('/aviation/') ? '/aviation' : '';

  constructor(private readonly router: Router, private readonly breadcrumbs: BreadcrumbService) {}

  ngOnInit(): void {
    this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
  }
}
