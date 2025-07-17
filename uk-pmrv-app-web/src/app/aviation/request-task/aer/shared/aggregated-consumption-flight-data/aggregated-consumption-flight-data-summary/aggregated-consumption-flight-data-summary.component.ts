import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, first, map, Observable, startWith, switchMap } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { AggregatedConsumptionFlightDataFormProvider } from '@aviation/request-task/aer/shared/aggregated-consumption-flight-data/aggregated-consumption-flight-data-form.provider';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { calculateAffectedFlightsPercentage } from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template-corsia/data-gaps.util';
import { FlightDataTableComponent } from '@aviation/shared/components/aer/flight-data-table/flight-data-table.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import {
  AviationAerCorsiaAggregatedEmissionsData,
  AviationAerCorsiaDataGaps,
  AviationAerCorsiaDataGapsDetails,
  AviationAerUkEtsAggregatedEmissionsData,
  AviationReportingService,
} from 'pmrv-api';

interface ViewModel {
  data: AviationAerUkEtsAggregatedEmissionsData | AviationAerCorsiaAggregatedEmissionsData;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
  isCorsia: boolean;
}

@Component({
  selector: 'app-aggregated-consumption-flight-data-summary',
  templateUrl: './aggregated-consumption-flight-data-summary.component.html',
  imports: [SharedModule, ReturnToLinkComponent, FlightDataTableComponent, AerReviewDecisionGroupComponent],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AggregatedConsumptionFlightDataSummaryComponent implements OnInit, OnDestroy {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerQuery.selectStatusForTask('aggregatedEmissionsData')),
    this.form.get('aggregatedEmissionDataDetails').statusChanges.pipe(
      startWith(this.form.get('aggregatedEmissionDataDetails').status),
      filter((status) => status === 'VALID' || status === 'INVALID'),
    ),
    this.store.pipe(aerQuery.selectIsCorsia),
  ]).pipe(
    map(([type, isEditable, taskStatus, formStatus, isCorsia]) => {
      const isFormValid = formStatus === 'VALID';
      return {
        data: isFormValid ? this.formProvider.getFormValue() : null,
        pageHeader: getSummaryHeaderForTaskType(type, 'aggregatedEmissionsData'),
        isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showDecision: showReviewDecisionComponent.includes(type),
        isCorsia: isCorsia,
      };
    }),
  );

  private reportingYear: number;

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: AggregatedConsumptionFlightDataFormProvider,
    private store: RequestTaskStore,
    private backLinkService: BackLinkService,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private aviationReportingService: AviationReportingService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
    this.setAerYear();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onSubmit() {
    if (this.form?.valid) {
      const payload = {
        aggregatedEmissionsData: {
          aggregatedEmissionDataDetails: this.form.get('aggregatedEmissionDataDetails').value,
        },
      };

      combineLatest([
        this.store.pipe(first(), aerQuery.selectAer),
        this.store.pipe(first(), requestTaskQuery.selectRequestTaskItem),
        this.aviationReportingService.getTotalEmissionsCorsia({
          aggregatedEmissionsData: this.form.value,
          year: this.reportingYear,
        }),
      ])
        .pipe(
          switchMap(([aer, requestTaskItem, emissions]) => {
            const isAviation = requestTaskItem.requestInfo.type === 'AVIATION_AER_CORSIA';
            const offsetFlightsNumber = emissions.offsetFlightsNumber;

            if (isAviation && (aer?.dataGaps as AviationAerCorsiaDataGaps)?.dataGapsDetails?.dataGaps) {
              const newAffectedFlightsPercentage = calculateAffectedFlightsPercentage(
                offsetFlightsNumber,
                (aer.dataGaps as AviationAerCorsiaDataGaps).dataGapsDetails.dataGaps,
              );

              //this is placed here instead of the side effects because it need a request to get offsetFlightsNumber and our side effects are not async
              this.store.aerDelegate.setDataGaps({
                ...aer.dataGaps,
                dataGapsDetails: {
                  ...aer.dataGaps['dataGapsDetails'],
                  affectedFlightsPercentage: newAffectedFlightsPercentage,
                } as AviationAerCorsiaDataGapsDetails,
              });
            }

            return this.store.aerDelegate.saveAer(payload, 'complete').pipe(this.pendingRequestService.trackRequest());
          }),
        )
        .subscribe(() => {
          this.router.navigate(['../../..'], { relativeTo: this.route });
        });
    }
  }

  private setAerYear(): void {
    this.store.pipe(first(), aerQuery.selectAerYear).subscribe((year) => {
      this.reportingYear = year;
    });
  }
}
