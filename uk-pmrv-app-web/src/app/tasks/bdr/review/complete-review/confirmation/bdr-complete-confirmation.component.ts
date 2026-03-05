import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { SharedModule } from '@shared/shared.module';
import { BdrService, BdrTaskSharedModule } from '@tasks/bdr/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

@Component({
  selector: 'app-bdr-complete-confirmation',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule, RouterLink],
  template: `
    <div class="govuk-grid-row">
      <govuk-panel title="Task completed"></govuk-panel>
      <a govukLink routerLink="/dashboard">Return to dashboard</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrCompleteConfirmationComponent implements OnInit {
  constructor(
    readonly bdrService: BdrService,
    readonly breadcrumbService: BreadcrumbService,
    private readonly router: Router,
  ) {}
  ngOnInit(): void {
    this.breadcrumbService.showDashboardBreadcrumb(this.router.url);
  }
}
