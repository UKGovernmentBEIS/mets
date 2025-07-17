import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AirImprovementAll } from '@shared/air-shared/types/air-improvement-all.type';
import { OperatorAirImprovementResponseAll } from '@shared/air-shared/types/operator-air-improvement-response-all.type';

import { AirApplicationReviewedRequestActionPayload } from 'pmrv-api';

import { AirService } from '../../../core/air.service';

@Component({
  selector: 'app-summary',
  template: `
    <app-action-task header="Review information about this improvement" [breadcrumb]="true">
      <app-air-improvement-item [airImprovement]="airImprovement" [reference]="reference"></app-air-improvement-item>
      <app-air-operator-response-item
        [reference]="reference"
        [operatorAirImprovementResponse]="operatorAirImprovementResponse$ | async"
        [attachedFiles]="operatorFiles$ | async"
        [isEditable]="false"
        [isReview]="true"></app-air-operator-response-item>
      <app-air-regulator-response-item
        [reference]="reference"
        [regulatorAirImprovementResponse]="regulatorAirImprovementResponse$ | async"
        [attachedFiles]="regulatorFiles$ | async"
        [isEditable]="false"
        [isReview]="false"></app-air-regulator-response-item>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  reference = this.route.snapshot.paramMap.get('id');
  airImprovement = this.route.snapshot.data.airImprovement as AirImprovementAll;

  airPayload$ = this.airService.payload$ as Observable<AirApplicationReviewedRequestActionPayload>;
  operatorAirImprovementResponse$ = this.airPayload$.pipe(
    map((payload) => payload?.operatorImprovementResponses?.[this.reference] as OperatorAirImprovementResponseAll),
  );
  regulatorAirImprovementResponse$ = this.airPayload$.pipe(
    map((payload) => payload?.regulatorReviewResponse?.regulatorImprovementResponses?.[this.reference]),
  );
  operatorFiles$ = this.operatorAirImprovementResponse$.pipe(
    map((payload) => (payload?.files ? this.airService.getOperatorDownloadUrlFiles(payload?.files) : [])),
  );
  regulatorFiles$ = this.regulatorAirImprovementResponse$.pipe(
    map((payload) => (payload?.files ? this.airService.getRegulatorDownloadUrlFiles(payload?.files) : [])),
  );

  constructor(
    private readonly airService: AirService,
    private readonly route: ActivatedRoute,
  ) {}
}
