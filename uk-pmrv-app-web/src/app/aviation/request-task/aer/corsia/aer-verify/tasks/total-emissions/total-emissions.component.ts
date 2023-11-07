import { ChangeDetectionStrategy, Component, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, switchMap } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { RequestTaskStore } from '@aviation/request-task/store';
import { FlightDataTableComponent } from '@aviation/shared/components/aer/flight-data-table';
import { TotalEmissionsCorsiaAerodromePairsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-aerodrome-pairs-table-template/total-emissions-corsia-aerodrome-pairs-table-template.component';
import { TotalEmissionsCorsiaSchemeYearSummaryComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-scheme-year-summary';
import { TotalEmissionsCorsiaStandardFuelsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-standard-fuels-table-template/total-emissions-corsia-standard-fuels-table-template.component';
import { TotalEmissionsCorsiaStatePairsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-state-pairs-table-template/total-emissions-corsia-state-pairs-table-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import {
  AviationAerCorsia,
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerCorsiaTotalEmissions,
  AviationReportingService,
} from 'pmrv-api';

interface ViewModel {
  heading: string;
  corsiaRequestTaskPayload: AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload;
  aviationAerCorsia: AviationAerCorsia;
  totalEmissions: AviationAerCorsiaTotalEmissions;
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
  ],
  templateUrl: './total-emissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalEmissionsComponent implements OnDestroy {
  currentTab$ = new BehaviorSubject<string>(null);
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerVerifyCorsiaQuery.selectPayload),
    this.store.pipe(aerVerifyCorsiaQuery.selectAer),
  ]).pipe(
    switchMap(([payload, aer]) =>
      this.aviationReportingService
        .getTotalEmissionsCorsia({
          aggregatedEmissionsData: aer.aggregatedEmissionsData,
          emissionsReductionClaim: aer.emissionsReductionClaim?.emissionsReductionClaimDetails?.totalEmissions,
        })
        .pipe(
          map((totalEmissions) => {
            return {
              heading: aerHeaderTaskMap['totalEmissionsCorsia'],
              corsiaRequestTaskPayload: payload,
              aviationAerCorsia: aer,
              totalEmissions: totalEmissions,
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
