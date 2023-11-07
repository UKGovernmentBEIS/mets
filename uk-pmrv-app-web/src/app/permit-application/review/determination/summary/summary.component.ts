import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

@Component({
  selector: 'app-summary',
  template: `
    <app-permit-task [breadcrumb]="true" [notification]="notification">
      <app-page-heading>{{ determinationHeader }}</app-page-heading>

      <ng-container *ngIf="isVariation$ | async; else issuanceSummary">
        <app-permit-variation-determination-summary-details
          [changePerStage]="true"
          cssClass="summary-list--edge-border"
        ></app-permit-variation-determination-summary-details>
      </ng-container>
      <ng-template #issuanceSummary>
        <app-permit-application-determination-summary-details
          [changePerStage]="true"
          cssClass="summary-list--edge-border"
        ></app-permit-application-determination-summary-details>
      </ng-template>

      <a govukLink routerLink="../..">Return to: {{ determinationHeader }}</a>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  determinationHeader = this.store.getDeterminationHeader();
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  isVariation$ = this.store.pipe(map((state) => state.requestType === 'PERMIT_VARIATION'));

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}
}
