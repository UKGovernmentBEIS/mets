import { ChangeDetectionStrategy, Component, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, switchMap } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { TotalEmissionsCorsiaAerodromePairsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-aerodrome-pairs-table-template/total-emissions-corsia-aerodrome-pairs-table-template.component';
import { TotalEmissionsCorsiaSchemeYearSummaryComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-scheme-year-summary';
import { TotalEmissionsCorsiaStandardFuelsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-standard-fuels-table-template/total-emissions-corsia-standard-fuels-table-template.component';
import { TotalEmissionsCorsiaStatePairsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-state-pairs-table-template/total-emissions-corsia-state-pairs-table-template.component';
import { SharedModule } from '@shared/shared.module';

import {
  AviationAerCorsia,
  AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload,
  AviationAerCorsiaTotalEmissions,
  AviationReportingService,
  RequestActionDTO,
} from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  corsiaRequestTaskPayload: AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload;
  aviationAerCorsia: AviationAerCorsia;
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
  ],
  templateUrl: './total-emissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class TotalEmissionsComponent implements OnDestroy {
  currentTab$ = new BehaviorSubject<string>(null);
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectRequestActionPayload),
    this.store.pipe(aerCorsiaQuery.selectAer),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    switchMap(([payload, aer, requestActionType]) =>
      this.aviationReportingService
        .getTotalEmissionsCorsia({
          aggregatedEmissionsData: aer.aggregatedEmissionsData,
          emissionsReductionClaim: aer.emissionsReductionClaim?.emissionsReductionClaimDetails?.totalEmissions,
        })
        .pipe(
          map((totalEmissions) => {
            return {
              requestActionType: requestActionType,
              pageHeader: aerHeaderTaskMap['totalEmissionsCorsia'],
              corsiaRequestTaskPayload: payload,
              aviationAerCorsia: aer,
              totalEmissions: totalEmissions,
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
