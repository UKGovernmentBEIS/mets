import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { GovukTableColumn } from 'govuk-components';

import {
  AerApplicationCompletedRequestActionPayload,
  AerApplicationSubmittedRequestActionPayload,
  EmissionSource,
} from 'pmrv-api';

import { AerService } from '../../core/aer.service';

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
  sourcesColumns: GovukTableColumn<EmissionSource>[] = [
    { field: 'reference', header: 'Reference', widthClass: 'govuk-!-width-one-half' },
    { field: 'description', header: 'Description', widthClass: 'govuk-!-width-one-half' },
  ];
  emissionSources$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.aer.emissionSources));

  constructor(private readonly aerService: AerService) {}
}
