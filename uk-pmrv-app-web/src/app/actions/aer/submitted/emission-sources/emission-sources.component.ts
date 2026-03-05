import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';
import { sourcesColumns } from './emission-sources';

@Component({
  selector: 'app-emission-sources',
  template: `
    <app-action-task header="Emission sources" [breadcrumb]="true">
      <govuk-table [columns]="sourcesColumns" [data]="emissionSources$ | async"></govuk-table>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionSourcesComponent {
  sourcesColumns = sourcesColumns;

  emissionSources$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.aer.emissionSources));

  constructor(private readonly aerService: AerService) {}
}
