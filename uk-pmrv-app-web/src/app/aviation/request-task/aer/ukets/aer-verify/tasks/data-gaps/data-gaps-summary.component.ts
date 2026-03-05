import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable, take, tap } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { DataGapsFormProvider } from '@aviation/request-task/aer/ukets/tasks/data-gaps/data-gaps-form.provider';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType } from '@aviation/request-task/util';
import { DataGapsListTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-list-template';
import { DataGapsSummaryTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerDataGaps, AviationAerUkEtsAggregatedEmissionDataDetails } from 'pmrv-api';

interface DataGapsSummaryViewModel {
  pageHeader: string;
  dataGaps: AviationAerDataGaps;
}

@Component({
  selector: 'app-data-gaps-summary',
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.pageHeader }}</app-page-heading>

      <ng-container *ngIf="dataGaps as dataGaps">
        <h2 class="govuk-heading-m">Summary</h2>

        <app-data-gaps-summary-template [data]="dataGaps"></app-data-gaps-summary-template>
      </ng-container>

      <ng-container *ngIf="dataGaps.exist">
        <app-data-gaps-list-template [dataGaps]="dataGaps.dataGaps"></app-data-gaps-list-template>
      </ng-container>
    </ng-container>

    <app-return-to-link></app-return-to-link>
  `,
  standalone: true,
  imports: [
    SharedModule,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    DataGapsListTemplateComponent,
    DataGapsSummaryTemplateComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class DataGapsSummaryComponent {
  private aggregatedEmissionsDataDetails: AviationAerUkEtsAggregatedEmissionDataDetails[];

  protected dataGaps: AviationAerDataGaps;
  protected isDataGapRemoved = false;

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: DataGapsFormProvider,
    private store: RequestTaskStore,
  ) {}

  vm$: Observable<DataGapsSummaryViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
  ]).pipe(
    map(([type]) => {
      return {
        pageHeader: getSummaryHeaderForTaskType(type, 'dataGaps'),
        dataGaps: {
          exist: this.formProvider.getFormValue().exist,
          dataGaps: this.formProvider.getFormValue()?.dataGaps,
          affectedFlightsPercentage: this.formProvider.getFormValue()?.affectedFlightsPercentage,
        },
      };
    }),
    tap((data) => {
      this.dataGaps = data.dataGaps;
      this._fetchAggregatedEmissionsDataDetails();
    }),
  );

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
