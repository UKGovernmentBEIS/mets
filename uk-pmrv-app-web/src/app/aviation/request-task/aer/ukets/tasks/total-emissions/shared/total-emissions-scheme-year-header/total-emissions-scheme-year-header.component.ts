import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, Observable } from 'rxjs';

import { TotalEmissionsAerodromePairsTableComponent } from '@aviation/request-task/aer/ukets/tasks/total-emissions/table/total-emissions-aerodrome-pairs-table/total-emissions-aerodrome-pairs-table.component';
import { TotalEmissionsDomesticFlightsTableComponent } from '@aviation/request-task/aer/ukets/tasks/total-emissions/table/total-emissions-domestic-flights-table';
import { TotalEmissionsNonDomesticFlightsTableComponent } from '@aviation/request-task/aer/ukets/tasks/total-emissions/table/total-emissions-non-domestic-flights-table';
import { TotalEmissionsStandardFuelsTableComponent } from '@aviation/request-task/aer/ukets/tasks/total-emissions/table/total-emissions-standard-fuels-table';
import { RequestTaskModule } from '@aviation/request-task/request-task.module';
import { AerRequestTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { TotalEmissionsSchemeYearSummaryComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-scheme-year-summary';
import { SharedModule } from '@shared/shared.module';

import { AviationAerEmissionsCalculationDTO, AviationAerTotalEmissions, AviationReportingService } from 'pmrv-api';

@Component({
  selector: 'app-total-emissions-scheme-year-header',
  imports: [
    SharedModule,
    RequestTaskModule,
    TotalEmissionsStandardFuelsTableComponent,
    TotalEmissionsAerodromePairsTableComponent,
    TotalEmissionsDomesticFlightsTableComponent,
    TotalEmissionsNonDomesticFlightsTableComponent,
    TotalEmissionsSchemeYearSummaryComponent,
  ],
  templateUrl: './total-emissions-scheme-year-header.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalEmissionsSchemeYearHeaderComponent implements OnInit, OnDestroy {
  constructor(
    private aviationReportingService: AviationReportingService,
    private requestTaskStore: RequestTaskStore,
    private router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  aviationAerEmissionsCalculationDTO: AviationAerEmissionsCalculationDTO;
  totalEmissions$: Observable<AviationAerTotalEmissions>;
  currentTab$ = new BehaviorSubject<string>(null);

  ngOnInit() {
    const state = this.requestTaskStore.getState();
    const payload = state.requestTaskItem?.requestTask?.payload as AerRequestTaskPayload;
    const emissionData = payload.aer.aggregatedEmissionsData;
    const safData = payload.aer.saf;
    this.aviationAerEmissionsCalculationDTO = {
      aggregatedEmissionsData: emissionData,
      saf: safData,
    };
    this.totalEmissions$ = this.aviationReportingService.getTotalEmissionsUkEts(
      this.aviationAerEmissionsCalculationDTO,
    );
  }

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
