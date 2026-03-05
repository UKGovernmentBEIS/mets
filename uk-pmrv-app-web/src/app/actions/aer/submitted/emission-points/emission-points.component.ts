import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';
import { pointsColumns } from './emission-points';

@Component({
  selector: 'app-emission-points',
  template: `
    <app-action-task header="Emission points" [breadcrumb]="true">
      <govuk-table [columns]="pointsColumns" [data]="emissionPoints$ | async"></govuk-table>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionPointsComponent {
  pointsColumns = pointsColumns;
  emissionPoints$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.aer.emissionPoints));

  constructor(private readonly aerService: AerService) {}
}
