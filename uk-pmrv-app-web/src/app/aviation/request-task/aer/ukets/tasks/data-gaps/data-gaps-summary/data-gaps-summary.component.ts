import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable, take, tap } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { DataGapsListTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-list-template';
import { DataGapsSummaryTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerDataGap, AviationAerDataGaps, AviationAerUkEtsAggregatedEmissionDataDetails } from 'pmrv-api';

import { AerReviewDecisionGroupComponent } from '../../../aer-review-decision-group/aer-review-decision-group.component';
import { DataGapsSummaryViewModel } from '../data-gaps.interface';
import { DataGapsFormProvider } from '../data-gaps-form.provider';
import { calculateAffectedFlightsPercentage } from '../util/data-gaps.util';

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
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class DataGapsSummaryComponent {
  private aggregatedEmissionsDataDetails: AviationAerUkEtsAggregatedEmissionDataDetails[];

  protected dataGaps: AviationAerDataGaps;
  protected isDataGapRemoved = false;

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: DataGapsFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
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
        dataGaps: {
          exist: this.formProvider.getFormValue().exist,
          dataGaps: this.formProvider.getFormValue()?.dataGaps,
          affectedFlightsPercentage: this.formProvider.getFormValue()?.affectedFlightsPercentage,
        },
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
    tap((data) => {
      this.dataGaps = data.dataGaps;
      this._fetchAggregatedEmissionsDataDetails();
    }),
  );

  onRemoveDataGap(index: number) {
    const data = [...this.dataGaps?.dataGaps];

    if (data[index]) {
      data.splice(index, 1);
    }

    this._updateFormProviderAndDataGaps(data);

    if (data.length === 0) {
      this.onSubmit(true);
    }
  }

  onSubmit(noData: boolean = false) {
    if (this.dataGaps?.dataGaps) this._updateFormProviderAndDataGaps([...this.dataGaps.dataGaps]);

    this.store.aerDelegate
      .saveAer({ dataGaps: this.formProvider.getFormValue() }, noData ? 'in progress' : 'complete')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.aerDelegate.setDataGaps(this.formProvider.getFormValue());

        this.router.navigate(noData ? ['..'] : ['../../..'], { relativeTo: this.route, replaceUrl: true });
      });
  }

  private _updateFormProviderAndDataGaps(dataGaps: AviationAerDataGap[]) {
    this.isDataGapRemoved = true;
    this.dataGaps.dataGaps = dataGaps;

    this.formProvider.dataGapsCtrl.setValue(dataGaps);

    this.formProvider.affectedFlightsPercentageCtrl.setValue(
      calculateAffectedFlightsPercentage(this.aggregatedEmissionsDataDetails, dataGaps),
    );

    this.dataGaps.affectedFlightsPercentage = calculateAffectedFlightsPercentage(
      this.aggregatedEmissionsDataDetails,
      dataGaps,
    );
  }

  private _fetchAggregatedEmissionsDataDetails() {
    this.store
      .pipe(aerQuery.selectAer)
      .pipe(
        take(1),
        map((aer) => aer.aggregatedEmissionsData?.aggregatedEmissionDataDetails),
      )
      .subscribe((data) => {
        this.aggregatedEmissionsDataDetails = data;
      });
  }
}
