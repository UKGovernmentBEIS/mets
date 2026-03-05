import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AirImprovementAll } from '@shared/air-shared/types/air-improvement-all.type';
import { OperatorAirImprovementResponseAll } from '@shared/air-shared/types/operator-air-improvement-response-all.type';

import { AirApplicationRespondedToRegulatorCommentsRequestActionPayload } from 'pmrv-api';

import { AirService } from '../core/air.service';

@Component({
  selector: 'app-responded',
  templateUrl: './responded.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RespondedComponent {
  airPayload$ = this.airService.payload$ as Observable<AirApplicationRespondedToRegulatorCommentsRequestActionPayload>;
  reference$ = this.airPayload$.pipe(map((payload) => payload?.reference.toString()));
  airImprovement$ = this.airPayload$.pipe(map((payload) => payload?.airImprovement as AirImprovementAll));
  operatorAirImprovementResponse$ = this.airPayload$.pipe(
    map((payload) => payload?.operatorImprovementResponse as OperatorAirImprovementResponseAll),
  );
  operatorFiles$ = this.operatorAirImprovementResponse$.pipe(
    map((payload) => (payload?.files ? this.airService.getOperatorDownloadUrlFiles(payload?.files) : [])),
  );
  regulatorAirImprovementResponse$ = this.airPayload$.pipe(map((payload) => payload?.regulatorImprovementResponse));
  regulatorFiles$ = this.regulatorAirImprovementResponse$.pipe(
    map((payload) => (payload?.files ? this.airService.getRegulatorDownloadUrlFiles(payload?.files) : [])),
  );
  operatorAirImprovementFollowUpResponse$ = this.airPayload$.pipe(
    map((payload) => payload?.operatorImprovementFollowUpResponse),
  );
  operatorFollowupFiles$ = this.operatorAirImprovementFollowUpResponse$.pipe(
    map((payload) => (payload?.files ? this.airService.getOperatorDownloadUrlFiles(payload?.files) : [])),
  );

  constructor(
    private readonly airService: AirService,
    private readonly route: ActivatedRoute,
  ) {}
}
