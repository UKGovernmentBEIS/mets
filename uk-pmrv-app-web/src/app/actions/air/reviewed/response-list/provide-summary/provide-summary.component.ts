import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AirApplicationReviewedRequestActionPayload } from 'pmrv-api';

import { AirService } from '../../../core/air.service';

@Component({
  selector: 'app-provide-summary',
  template: `
    <app-action-task header="Review summary" [breadcrumb]="true">
      <app-air-regulator-provide-summary
        [regulatorAirReviewResponse]="regulatorReviewResponse$ | async"
        [isEditable]="false"
        [isReview]="true"></app-air-regulator-provide-summary>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProvideSummaryComponent {
  regulatorReviewResponse$ = (this.airService.payload$ as Observable<AirApplicationReviewedRequestActionPayload>).pipe(
    map((payload) => payload?.regulatorReviewResponse),
  );

  constructor(private readonly airService: AirService) {}
}
