import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-installation-description-summary',
  standalone: false,
  template: `
    <app-permit-task
      [notification]="notification"
      [breadcrumb]="true"
      reviewGroupTitle="Installation details"
      reviewGroupUrl="details">
      <app-page-heading caption="Installation details">Description of the installation</app-page-heading>
      <app-summary-header
        [changeRoute]="(store.isEditable$ | async) === true ? '..' : undefined"
        class="govuk-heading-m">
        <span class="govuk-visually-hidden">List of installation categories</span>
      </app-summary-header>
      <app-description-summary-template></app-description-summary-template>
      <app-list-return-link reviewGroupTitle="Installation details" reviewGroupUrl="details"></app-list-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InstallationDescriptionSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}
}
