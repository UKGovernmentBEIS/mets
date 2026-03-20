import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';

@Component({
  selector: 'app-bdrs2-send-report-confirmation',
  imports: [SharedModule, RouterLink],
  standalone: true,
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Sent to {{ sendTo }} for review">
          Your reference number
          <br />
          {{ bdrs2Service.requestId }}
        </govuk-panel>

        <h3 class="govuk-heading-m">What happens next</h3>

        <p class="govuk-body">
          Your application has been submitted and will be reviewed by your
          {{ sendTo === 'verifier' ? 'Verifier' : 'Regulator' }}
        </p>
      </div>
    </div>

    <a govukLink routerLink="/dashboard">Return to: Dashboard</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Bdrs2SendReportConfirmationComponent implements OnInit {
  sendTo = this.route.snapshot.queryParamMap.get('sendTo');

  constructor(
    readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly breadcrumbs: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
  }
}
