import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AirImprovementAll } from '@shared/air-shared/types/air-improvement-all.type';
import { OperatorAirImprovementResponseAll } from '@shared/air-shared/types/operator-air-improvement-response-all.type';
import { AirService } from '@tasks/air/shared/services/air.service';
import { AirImprovementResponseService } from '@tasks/air/shared/services/air-improvement-response.service';

import { AirApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  reference = this.route.snapshot.paramMap.get('id');
  airImprovement = this.route.snapshot.data.airImprovement as AirImprovementAll;
  isEditable$ = this.airService.isEditable$;
  airPayload$ = this.airService.payload$ as Observable<AirApplicationSubmitRequestTaskPayload>;
  operatorAirImprovementResponse$ = this.airPayload$.pipe(
    map((payload) => payload?.operatorImprovementResponses[this.reference] as OperatorAirImprovementResponseAll),
  );
  documentFiles$ = this.operatorAirImprovementResponse$.pipe(
    map((payload) => (payload?.files ? this.airService.getOperatorDownloadUrlFiles(payload?.files) : [])),
  );
  resolvedChangeLink$ = this.operatorAirImprovementResponse$.pipe(
    map((payload) => '../' + this.airImprovementResponseService.mapResponseTypeToPath(payload.type)),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly airService: AirService,
    private readonly airImprovementResponseService: AirImprovementResponseService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    this.airService.payload$
      .pipe(
        first(),
        switchMap((payload) => {
          return this.airService.postAirTaskSave({
            operatorImprovementResponses: payload?.operatorImprovementResponses,
            airSectionsCompleted: {
              ...payload?.airSectionsCompleted,
              [this.reference]: true,
            },
          });
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
