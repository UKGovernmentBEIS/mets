import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { GovukTableColumn } from 'govuk-components';

import {
  AerApplicationCompletedRequestActionPayload,
  AerApplicationSubmittedRequestActionPayload,
  EmissionPoint,
} from 'pmrv-api';

import { AerService } from '../../core/aer.service';

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
  pointsColumns: GovukTableColumn<EmissionPoint>[] = [
    { field: 'reference', header: 'Reference', widthClass: 'govuk-!-width-one-half' },
    { field: 'description', header: 'Description', widthClass: 'govuk-!-width-one-half' },
  ];
  emissionPoints$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.aer.emissionPoints));

  constructor(private readonly aerService: AerService) {}
}
