import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { VirApplicationReviewedRequestActionPayload } from 'pmrv-api';

import { VirService } from '../../../core/vir.service';

@Component({
  selector: 'app-report-summary',
  template: `
    <app-action-task header="Create summary" [breadcrumb]="true">
      <app-regulator-create-summary
        [regulatorReviewResponse]="regulatorReviewResponse$ | async"
        [isEditable]="false"
        [isReview]="true"
      ></app-regulator-create-summary>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportSummaryComponent {
  regulatorReviewResponse$ = (this.virService.payload$ as Observable<VirApplicationReviewedRequestActionPayload>).pipe(
    map((payload) => payload?.regulatorReviewResponse),
  );

  constructor(private readonly virService: VirService) {}
}
