import { ChangeDetectionStrategy, Component, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, switchMap } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerCorsiaRequestActionPayload, requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { AerReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-review-decision-group-summary/aer-review-decision-group-summary.component';
import { TotalEmissionAviationAerCorsia } from '@aviation/shared/components/aer-corsia/total-emission-aviation-aer-corsia.model';
import { TotalEmissionsCorsiaAerodromePairsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-aerodrome-pairs-table-template/total-emissions-corsia-aerodrome-pairs-table-template.component';
import { TotalEmissionsCorsiaSchemeYearSummaryComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-scheme-year-summary';
import { TotalEmissionsCorsiaStandardFuelsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-standard-fuels-table-template/total-emissions-corsia-standard-fuels-table-template.component';
import { TotalEmissionsCorsiaStatePairsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-state-pairs-table-template/total-emissions-corsia-state-pairs-table-template.component';
import { SharedModule } from '@shared/shared.module';

import {
  AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload,
  AviationAerCorsiaTotalEmissions,
  AviationReportingService,
  RequestActionDTO,
} from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  corsiaRequestTaskPayload: AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload;
  aviationAerCorsia: TotalEmissionAviationAerCorsia;
  totalEmissions: AviationAerCorsiaTotalEmissions;
}

@Component({
  selector: 'app-total-emissions',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    TotalEmissionsCorsiaAerodromePairsTableTemplateComponent,
    TotalEmissionsCorsiaSchemeYearSummaryComponent,
    TotalEmissionsCorsiaStandardFuelsTableTemplateComponent,
    TotalEmissionsCorsiaStatePairsTableTemplateComponent,
    AerReviewDecisionGroupSummaryComponent,
  ],
  templateUrl: './total-emissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class TotalEmissionsComponent implements OnDestroy {
  currentTab$ = new BehaviorSubject<string>(null);
  vm$: Observable<ViewModel & AerDecisionViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
    this.store.pipe(aerCorsiaQuery.selectAer),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    switchMap(([payload, regulatorViewer, aer, requestActionType]) =>
      this.aviationReportingService
        .getTotalEmissionsCorsia({
          aggregatedEmissionsData: aer.aggregatedEmissionsData,
          emissionsReductionClaim: aer.emissionsReductionClaim?.emissionsReductionClaimDetails?.totalEmissions,
          year: (payload as AerCorsiaRequestActionPayload).reportingYear,
        })
        .pipe(
          map((totalEmissions) => {
            return {
              requestActionType: requestActionType,
              pageHeader: aerHeaderTaskMap['totalEmissionsCorsia'],
              corsiaRequestTaskPayload: payload,
              aviationAerCorsia: { ...aer, reportingYear: (payload as AerCorsiaRequestActionPayload).reportingYear },
              totalEmissions: totalEmissions,
              ...getAerDecisionReview(payload, requestActionType, regulatorViewer, 'totalEmissionsCorsia', true),
            };
          }),
        ),
    ),
  );

  constructor(
    public store: RequestActionStore,
    private aviationReportingService: AviationReportingService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnDestroy(): void {
    this.currentTab$.complete();
  }

  selectedTab(selected: string) {
    this.router.navigate([], {
      relativeTo: this.route,
      preserveFragment: true,
    });
    this.currentTab$.next(selected);
  }
}
