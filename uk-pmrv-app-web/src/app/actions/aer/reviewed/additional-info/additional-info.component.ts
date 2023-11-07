import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-additional-info',
  template: `
    <app-action-task header="Additional information" [breadcrumb]="true">
      <app-additional-info-group
        [aerData]="aerData$ | async"
        [additionalDocumentFiles]="additionalDocumentFiles$ | async"
      ></app-additional-info-group>
      <app-review-group-decision-summary [decisionData]="decisionData$ | async"></app-review-group-decision-summary>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalInfoComponent {
  payload$ = this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>;
  aerData$ = this.payload$.pipe(map((payload) => payload.aer));
  additionalDocumentFiles$ = this.payload$.pipe(
    map((payload) =>
      payload.aer.additionalDocuments.exist
        ? this.aerService.getDownloadUrlFiles(payload.aer.additionalDocuments.documents)
        : [],
    ),
  );
  decisionData$ = combineLatest([this.payload$, this.route.data]).pipe(
    map(([payload, data]) => payload.reviewGroupDecisions[data.groupKey]),
  );

  constructor(private readonly aerService: AerService, private readonly route: ActivatedRoute) {}
}
