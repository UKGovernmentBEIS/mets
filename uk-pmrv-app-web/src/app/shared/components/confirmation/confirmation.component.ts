import { ChangeDetectionStrategy, Component, inject, Input, OnInit, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

@Component({
  selector: 'app-confirmation-shared',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel class="pre-wrap" [title]="title">
          {{ titleContent }}
          <div *ngIf="titleContentId" style="font-weight: bold;">{{ titleContentId }}</div>
        </govuk-panel>
        <ng-container
          *ngTemplateOutlet="
            whatHappensNextTemplate ? whatHappensNextTemplate : defaultWhatHappensNextTemplate
          "></ng-container>
        <ng-template #defaultWhatHappensNextTemplate></ng-template>
        <a govukLink [routerLink]="returnToLink">Return to dashboard</a>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationSharedComponent implements OnInit {
  @Input() title: string;
  @Input() titleContent: string;
  @Input() titleContentId: string;
  @Input() whatHappensNextTemplate: TemplateRef<any>;
  @Input() returnToLink = '/dashboard';

  protected readonly router = inject(Router);
  protected readonly breadcrumbs = inject(BreadcrumbService);

  ngOnInit(): void {
    this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
  }
}
