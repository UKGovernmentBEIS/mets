import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-regulated-activities',
  template: `
    <app-action-task header="Regulated activities carried out at the installation" [breadcrumb]="true">
      <ng-container *ngFor="let activity of activities$ | async | regulatedActivitiesSort">
        <app-aer-regulated-activities-summary-template
          [activity]="activity"
        ></app-aer-regulated-activities-summary-template>
      </ng-container>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatedActivitiesComponent {
  activities$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.aer.regulatedActivities));

  constructor(private readonly aerService: AerService) {}
}
