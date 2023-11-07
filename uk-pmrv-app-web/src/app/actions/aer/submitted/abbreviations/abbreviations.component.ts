import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-abbreviations',
  template: `
    <app-action-task header="Abbreviations and definitions" [breadcrumb]="true">
      <app-abbreviations-summary-template [data]="abbreviations$ | async"></app-abbreviations-summary-template>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AbbreviationsComponent {
  abbreviations$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.aer.abbreviations));

  constructor(private readonly aerService: AerService) {}
}
