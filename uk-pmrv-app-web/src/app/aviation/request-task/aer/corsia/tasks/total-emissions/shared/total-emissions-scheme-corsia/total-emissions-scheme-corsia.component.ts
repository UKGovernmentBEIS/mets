import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, map, Observable, switchMap } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { RequestTaskModule } from '@aviation/request-task/request-task.module';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TotalEmissionsCorsiaAerodromePairsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-aerodrome-pairs-table-template/total-emissions-corsia-aerodrome-pairs-table-template.component';
import { TotalEmissionsCorsiaSchemeYearSummaryComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-scheme-year-summary';
import { TotalEmissionsCorsiaStandardFuelsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-standard-fuels-table-template/total-emissions-corsia-standard-fuels-table-template.component';
import { TotalEmissionsCorsiaStatePairsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-state-pairs-table-template/total-emissions-corsia-state-pairs-table-template.component';
import { SharedModule } from '@shared/shared.module';

import {
  AviationAerCorsia,
  AviationAerCorsiaApplicationSubmitRequestTaskPayload,
  AviationAerCorsiaTotalEmissions,
  AviationReportingService,
} from 'pmrv-api';

interface ViewModel {
  aviationAerCorsia: AviationAerCorsia;
  corsiaRequestTaskPayload: AviationAerCorsiaApplicationSubmitRequestTaskPayload;
  totalEmissions: AviationAerCorsiaTotalEmissions;
}

@Component({
  selector: 'app-total-emissions-scheme-corsia',
  templateUrl: './total-emissions-scheme-corsia.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    SharedModule,
    RequestTaskModule,
    TotalEmissionsCorsiaSchemeYearSummaryComponent,
    TotalEmissionsCorsiaStandardFuelsTableTemplateComponent,
    TotalEmissionsCorsiaAerodromePairsTableTemplateComponent,
    TotalEmissionsCorsiaStatePairsTableTemplateComponent,
  ],
})
export class TotalEmissionsSchemeCorsiaComponent implements OnDestroy {
  private aviationReportingService = inject(AviationReportingService);
  private store = inject(RequestTaskStore);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  currentTab$ = new BehaviorSubject<string>(null);
  vm$: Observable<ViewModel> = this.store.pipe(aerQuery.selectPayload).pipe(
    switchMap((payload) =>
      this.aviationReportingService
        .getTotalEmissionsCorsia({
          aggregatedEmissionsData: payload?.aer?.aggregatedEmissionsData,
          emissionsReductionClaim: payload?.aer.emissionsReductionClaim?.emissionsReductionClaimDetails?.totalEmissions,
        })
        .pipe(
          map((totalEmissions) => {
            return {
              aviationAerCorsia: payload?.aer,
              corsiaRequestTaskPayload: payload,
              totalEmissions: totalEmissions,
            };
          }),
        ),
    ),
  );

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
