import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { SharedModule } from '@shared/shared.module';
import { BdrService } from '@tasks/bdr/shared';

@Component({
  selector: 'app-bdr-send-report-confirmation',
  standalone: true,
  imports: [SharedModule, RouterLink],
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Sent to {{ sendTo }} for review">
          Your reference number
          <br />
          {{ bdrService.requestId }}
        </govuk-panel>
      </div>
    </div>

    <a govukLink routerLink="/dashboard">Return to: Dashboard</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrSendReportConfirmationComponent implements OnInit {
  sendTo = this.route.snapshot.queryParamMap.get('sendTo');

  constructor(
    readonly bdrService: BdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly breadcrumbs: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
  }
}
