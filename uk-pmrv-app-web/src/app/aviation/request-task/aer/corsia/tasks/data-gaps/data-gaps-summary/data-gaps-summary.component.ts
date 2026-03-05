import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap, takeUntil } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType } from '@aviation/request-task/util';
import { DataGapsListTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-list-template';
import {
  calculateAffectedFlightsDataGaps,
  calculateAffectedFlightsPercentage,
} from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template-corsia/data-gaps.util';
import { DataGapsSummaryTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template-corsia/data-gaps-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaDataGaps, AviationAerDataGap, AviationReportingService } from 'pmrv-api';

import { DataGapsFormProvider } from '../data-gaps-form.provider';

export interface DataGapsSummaryViewModel {
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
}

@Component({
  selector: 'app-data-gaps-summary',
  templateUrl: './data-gaps-summary.component.html',
  standalone: true,
  imports: [
    SharedModule,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    DataGapsListTemplateComponent,
    DataGapsSummaryTemplateComponent,
    AerReviewDecisionGroupComponent,
  ],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class DataGapsSummaryComponent implements OnInit {
  protected dataGaps: AviationAerCorsiaDataGaps;
  affectedFlightsDataGaps;
  protected isDataGapRemoved = false;
  offsetFlightsNumber;

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: DataGapsFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
    private aviationReportingService: AviationReportingService,
    private readonly destroy$: DestroySubject,
  ) {}

  vm$: Observable<DataGapsSummaryViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerQuery.selectStatusForTask('dataGaps')),
  ]).pipe(
    map(([type, isEditable, taskStatus]) => {
      return {
        pageHeader: getSummaryHeaderForTaskType(type, 'dataGaps'),
        isEditable,

        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
      };
    }),
  );

  ngOnInit(): void {
    this.dataGaps = this.formProvider.getFormValue();
    this.affectedFlightsDataGaps = calculateAffectedFlightsDataGaps(this.dataGaps?.dataGapsDetails?.dataGaps ?? []);
    this.store
      .pipe(first(), aerQuery.selectPayload)
      .pipe(
        switchMap((payload) => {
          return this.aviationReportingService.getTotalEmissionsCorsia({
            aggregatedEmissionsData: payload.aer.aggregatedEmissionsData,
            year: payload.reportingYear,
          });
        }),
      )
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => (this.offsetFlightsNumber = data.offsetFlightsNumber));
  }

  onRemoveDataGap(index: number) {
    const data = [...(this.dataGaps?.dataGapsDetails?.dataGaps ?? [])];

    if (data[index]) {
      data.splice(index, 1);
    }

    this._updateFormProviderAndDataGaps(data);

    if (data.length === 0) {
      this.onSubmit(true);
    }
  }

  onSubmit(noData: boolean = false) {
    if (this.dataGaps?.dataGapsDetails?.dataGaps)
      this._updateFormProviderAndDataGaps([...(this.dataGaps?.dataGapsDetails?.dataGaps ?? [])]);

    this.store.aerDelegate
      .saveAer({ dataGaps: this.formProvider.getFormValue() }, noData ? 'in progress' : 'complete')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.router.navigate(noData ? ['..'] : ['../../..'], { relativeTo: this.route, replaceUrl: true });
      });
  }

  private _updateFormProviderAndDataGaps(dataGaps: AviationAerDataGap[]) {
    this.isDataGapRemoved = true;
    this.dataGaps.dataGapsDetails = { ...this.dataGaps.dataGapsDetails, dataGaps };

    this.formProvider.dataGapsCtrl.setValue(dataGaps);

    this.formProvider.dataGapsDetailsCtrl.controls.affectedFlightsPercentage.setValue(
      calculateAffectedFlightsPercentage(this.offsetFlightsNumber, dataGaps),
    );

    this.dataGaps.dataGapsDetails.affectedFlightsPercentage = calculateAffectedFlightsPercentage(
      this.offsetFlightsNumber,
      dataGaps,
    );

    this.affectedFlightsDataGaps = calculateAffectedFlightsDataGaps(this.dataGaps.dataGapsDetails.dataGaps);
  }
}
