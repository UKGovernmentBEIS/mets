import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { SharedModule } from '@shared/shared.module';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

@Component({
  selector: 'app-bdrs2-complete-confirmation',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule, RouterLink],
  template: `
    <div class="govuk-grid-row">
      <govuk-panel title="Task completed"></govuk-panel>
      <a govukLink routerLink="/dashboard">Return to dashboard</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrS2CompleteConfirmationComponent implements OnInit {
  constructor(
    readonly breadcrumbService: BreadcrumbService,
    private readonly router: Router,
  ) {}
  ngOnInit(): void {
    this.breadcrumbService.showDashboardBreadcrumb(this.router.url);
  }
}
