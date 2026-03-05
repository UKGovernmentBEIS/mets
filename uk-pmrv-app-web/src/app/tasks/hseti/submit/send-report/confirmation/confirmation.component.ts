import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { SharedModule } from '@shared/shared.module';
import { HseTiService } from '@tasks/hseti/core';

@Component({
  selector: 'app-hseti-send-report-confirmation',
  standalone: true,
  imports: [SharedModule, RouterLink],
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Sent to regulator for review">
          Your reference number
          <br />
          {{ hseTiService.requestId }}
        </govuk-panel>
      </div>
    </div>

    <a govukLink routerLink="/dashboard">Return to: Dashboard</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HseTiSendReportConfirmationComponent implements OnInit {
  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly breadcrumbs: BreadcrumbService,
    readonly hseTiService: HseTiService,
  ) {}

  ngOnInit(): void {
    this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
  }
}
