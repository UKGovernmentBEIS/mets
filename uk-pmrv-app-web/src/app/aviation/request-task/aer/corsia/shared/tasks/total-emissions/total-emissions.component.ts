import { ChangeDetectionStrategy, Component, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, switchMap } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { FlightDataTableComponent } from '@aviation/shared/components/aer/flight-data-table';
import { TotalEmissionAviationAerCorsia } from '@aviation/shared/components/aer-corsia/total-emission-aviation-aer-corsia.model';
import { TotalEmissionsCorsiaAerodromePairsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-aerodrome-pairs-table-template/total-emissions-corsia-aerodrome-pairs-table-template.component';
import { TotalEmissionsCorsiaSchemeYearSummaryComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-scheme-year-summary';
import { TotalEmissionsCorsiaStandardFuelsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-standard-fuels-table-template/total-emissions-corsia-standard-fuels-table-template.component';
import { TotalEmissionsCorsiaStatePairsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-state-pairs-table-template/total-emissions-corsia-state-pairs-table-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import {
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerCorsiaTotalEmissions,
  AviationReportingService,
} from 'pmrv-api';

interface ViewModel {
  heading: string;
  corsiaRequestTaskPayload: AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload;
  aviationAerCorsia: TotalEmissionAviationAerCorsia;
  totalEmissions: AviationAerCorsiaTotalEmissions;
  showDecision: boolean;
}

@Component({
  selector: 'app-total-emissions',
  standalone: true,
  imports: [
    ReturnToLinkComponent,
    SharedModule,
    FlightDataTableComponent,
    TotalEmissionsCorsiaAerodromePairsTableTemplateComponent,
    TotalEmissionsCorsiaSchemeYearSummaryComponent,
    TotalEmissionsCorsiaStandardFuelsTableTemplateComponent,
    TotalEmissionsCorsiaStatePairsTableTemplateComponent,
    AerReviewDecisionGroupComponent,
  ],
  templateUrl: './total-emissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalEmissionsComponent implements OnDestroy {
  currentTab$ = new BehaviorSubject<string>(null);
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectPayload),
    this.store.pipe(aerVerifyCorsiaQuery.selectAer),
  ]).pipe(
    switchMap(([type, payload, aer]) =>
      this.aviationReportingService
        .getTotalEmissionsCorsia({
          aggregatedEmissionsData: aer.aggregatedEmissionsData,
          emissionsReductionClaim: aer.emissionsReductionClaim?.emissionsReductionClaimDetails?.totalEmissions,
          year: (payload as AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload).reportingYear,
        })
        .pipe(
          map((totalEmissions) => {
            return {
              heading: aerReviewCorsiaHeaderTaskMap.totalEmissionsCorsia,
              corsiaRequestTaskPayload: payload,
              aviationAerCorsia: {
                ...aer,
                reportingYear: (payload as AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload)
                  .reportingYear,
              },
              totalEmissions: totalEmissions,
              showDecision: showReviewDecisionComponent.includes(type),
            };
          }),
        ),
    ),
  );

  constructor(
    private store: RequestTaskStore,
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
