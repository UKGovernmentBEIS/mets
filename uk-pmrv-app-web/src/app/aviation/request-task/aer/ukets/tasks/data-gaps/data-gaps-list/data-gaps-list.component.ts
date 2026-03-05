import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { map, take } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { DataGapsListTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-list-template';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { cloneDeep } from 'lodash-es';

import { AviationAerDataGap, AviationAerUkEtsAggregatedEmissionDataDetails } from 'pmrv-api';

import { DataGapsFormProvider } from '../data-gaps-form.provider';
import { calculateAffectedFlightsPercentage } from '../util/data-gaps.util';

@Component({
  selector: 'app-data-gaps-list',
  templateUrl: './data-gaps-list.component.html',
  standalone: true,
  imports: [SharedModule, RouterLinkWithHref, ReturnToLinkComponent, DataGapsListTemplateComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class DataGapsListComponent implements OnInit {
  private aggregatedEmissionsDataDetails: AviationAerUkEtsAggregatedEmissionDataDetails[];

  protected dataGaps: AviationAerDataGap[];

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: DataGapsFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  ngOnInit(): void {
    this._fetchAggregatedEmissionsDataDetails();
    this.dataGaps = cloneDeep(this.formProvider.getFormValue().dataGaps);
  }

  onRemoveDataGap(index: number) {
    if (this.dataGaps[index]) {
      this.dataGaps.splice(index, 1);
    }

    this.formProvider.dataGapsCtrl.setValue(this.dataGaps);

    if (this.dataGaps.length === 0) {
      this.onSubmit(true);
    }
  }

  onSubmit(noData: boolean = false) {
    this.formProvider.dataGapsCtrl.setValue(this.dataGaps);

    this.formProvider.affectedFlightsPercentageCtrl.setValue(
      calculateAffectedFlightsPercentage(this.aggregatedEmissionsDataDetails, this.dataGaps),
    );

    this.store.aerDelegate
      .saveAer({ dataGaps: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.aerDelegate.setDataGaps(this.formProvider.getFormValue());

        this.router.navigate(noData ? ['..'] : ['../', 'summary'], { relativeTo: this.route });
      });
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
