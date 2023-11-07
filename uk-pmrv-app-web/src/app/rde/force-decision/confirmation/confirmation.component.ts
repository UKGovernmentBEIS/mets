import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { RdeStore } from '../../store/rde.store';

@Component({
  selector: 'app-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Extension request {{ decision === 'ACCEPTED' ? 'approved' : 'rejected' }}"> </govuk-panel>
      </div>
    </div>
    <div class="govuk-!-margin-top-6">
      <ng-container *ngIf="decision === 'ACCEPTED'">
        <p class="govuk-body">
          The determination deadline has been updated to
          {{ (store | async)?.rdeResponsePayload?.proposedDueDate | govukDate }}
        </p>
      </ng-container>
    </div>
    <a govukLink [routerLink]="isAviation + '/dashboard'"> Return to dashboard </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  decision = this.router.getCurrentNavigation().extras?.state?.decision;
  isAviation = this.router.url.includes('/aviation/') ? '/aviation' : '';

  constructor(private readonly router: Router, readonly store: RdeStore) {}
}
