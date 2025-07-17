import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { AerCorsiaRequestTaskPayload, requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { DataGapsListTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-list-template';
import { calculateAffectedFlightsPercentage } from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template-corsia/data-gaps.util';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { cloneDeep } from 'lodash-es';

import { AviationAerDataGap, AviationReportingService } from 'pmrv-api';

import { DataGapsFormProvider } from '../data-gaps-form.provider';

@Component({
  selector: 'app-data-gaps-list',
  templateUrl: './data-gaps-list.component.html',
  standalone: true,
  imports: [SharedModule, RouterLinkWithHref, ReturnToLinkComponent, DataGapsListTemplateComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export default class DataGapsListComponent implements OnInit {
  protected dataGaps: AviationAerDataGap[];

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: DataGapsFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
    private aviationReportingService: AviationReportingService,
  ) {}

  ngOnInit(): void {
    this.dataGaps = cloneDeep(this.formProvider.getFormValue()?.dataGapsDetails?.dataGaps);
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

    this.store
      .pipe(first(), requestTaskQuery.selectRequestTaskItem)
      .pipe(
        switchMap((requestTaskItem) => {
          const payload = requestTaskItem?.requestTask?.payload as AerCorsiaRequestTaskPayload;
          return this.aviationReportingService.getTotalEmissionsCorsia({
            aggregatedEmissionsData: payload.aer.aggregatedEmissionsData,
            year: payload.reportingYear,
          });
        }),
        switchMap((emissions) => {
          this.formProvider.dataGapsDetailsCtrl.controls.affectedFlightsPercentage.setValue(
            calculateAffectedFlightsPercentage(emissions.offsetFlightsNumber, this.dataGaps),
          );

          return this.store.aerDelegate
            .saveAer({ dataGaps: this.formProvider.getFormValue() }, 'in progress')
            .pipe(this.pendingRequestService.trackRequest());
        }),
      )
      .subscribe(() => {
        this.router.navigate(noData ? ['..'] : ['../', 'summary'], { relativeTo: this.route });
      });
  }
}
