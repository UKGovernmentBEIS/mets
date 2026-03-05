import { ChangeDetectionStrategy, Component, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable } from 'rxjs';

import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { aerQuery } from '@aviation/request-action/aer/ukets/aer-ukets.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { AerReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-review-decision-group-summary/aer-review-decision-group-summary.component';
import { TotalEmissionsAerodromePairsTableTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-aerodrome-pairs-table-template/total-emissions-aerodrome-pairs-table-template.component';
import { TotalEmissionsDomesticFlightsTableTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-domestic-flights-table-template/total-emissions-domestic-flights-table-template.component';
import { TotalEmissionsNonDomesticFlightsTableTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-non-domestic-flights-table-template/total-emissions-non-domestic-flights-table-template.component';
import { TotalEmissionsSchemeYearSummaryComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-scheme-year-summary';
import { TotalEmissionsStandardFuelsTableTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-standard-fuels-table-template/total-emissions-standard-fuels-table-template.component';
import { TotalEmissionsSummaryTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-summary-template';
import { SharedModule } from '@shared/shared.module';

import {
  AviationAerTotalEmissions,
  AviationAerTotalEmissionsConfidentiality,
  AviationAerUkEts,
  AviationReportingService,
  RequestActionDTO,
} from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  totalEmissionsConfidentiality: AviationAerTotalEmissionsConfidentiality;
  totalEmissions$: Observable<AviationAerTotalEmissions>;
  aer: AviationAerUkEts;
}

@Component({
  selector: 'app-total-emissions.component',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    TotalEmissionsSummaryTemplateComponent,
    TotalEmissionsSchemeYearSummaryComponent,
    TotalEmissionsStandardFuelsTableTemplateComponent,
    TotalEmissionsAerodromePairsTableTemplateComponent,
    TotalEmissionsDomesticFlightsTableTemplateComponent,
    TotalEmissionsNonDomesticFlightsTableTemplateComponent,
    AerReviewDecisionGroupSummaryComponent,
  ],
  templateUrl: './total-emissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class TotalEmissionsComponent implements OnDestroy {
  currentTab$ = new BehaviorSubject<string>(null);
  vm$: Observable<ViewModel & AerDecisionViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    map(([payload, requestActionType, regulatorViewer]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['aviationAerTotalEmissionsConfidentiality'],
      totalEmissionsConfidentiality: payload.aer.aviationAerTotalEmissionsConfidentiality,
      totalEmissions$: this.aviationReportingService.getTotalEmissionsUkEts({
        aggregatedEmissionsData: payload.aer.aggregatedEmissionsData,
        saf: payload.aer.saf,
      }),
      aer: payload.aer,
      ...getAerDecisionReview(
        payload,
        requestActionType,
        regulatorViewer,
        'aviationAerTotalEmissionsConfidentiality',
        true,
      ),
    })),
  );

  constructor(
    public store: RequestActionStore,
    private aviationReportingService: AviationReportingService,
    private router: Router,
    private readonly route: ActivatedRoute,
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
