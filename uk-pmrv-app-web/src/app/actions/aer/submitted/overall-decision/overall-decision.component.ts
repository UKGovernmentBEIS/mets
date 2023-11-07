import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-overall-decision',
  template: `
    <app-action-task header="Overall decision" [breadcrumb]="true">
      <app-overall-decision-group [overallAssessment]="overallAssessmentInfo$ | async"></app-overall-decision-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionComponent {
  overallAssessmentInfo$ = (
    this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>
  ).pipe(map((payload) => payload.verificationReport.overallAssessment));

  constructor(private readonly aerService: AerService) {}
}
