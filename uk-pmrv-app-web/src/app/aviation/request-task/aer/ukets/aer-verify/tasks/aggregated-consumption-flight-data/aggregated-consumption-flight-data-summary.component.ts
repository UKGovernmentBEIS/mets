import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { RouterLink, RouterLinkWithHref } from '@angular/router';

import { combineLatest, filter, map, Observable, startWith } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { AggregatedConsumptionFlightDataFormProvider } from '@aviation/request-task/aer/shared/aggregated-consumption-flight-data/aggregated-consumption-flight-data-form.provider';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType } from '@aviation/request-task/util';
import { FlightDataTableComponent } from '@aviation/shared/components/aer/flight-data-table/flight-data-table.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerCorsiaAggregatedEmissionsData, AviationAerUkEtsAggregatedEmissionsData } from 'pmrv-api';

interface ViewModel {
  data: AviationAerUkEtsAggregatedEmissionsData | AviationAerCorsiaAggregatedEmissionsData;
  pageHeader: string;
  isCorsia: boolean;
}

@Component({
  selector: 'app-aggregated-consumption-flight-data-summary',
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.pageHeader }}</app-page-heading>

      <ng-container *ngIf="vm.data">
        <div class="header-container">
          <h2 class="govuk-heading-m govuk-!-width-two-thirds">Aggregated Emissions Data uploaded data</h2>
        </div>

        <app-flight-data-table
          [headingText]="'File uploaded'"
          [emissionDataDetails]="vm.data.aggregatedEmissionDataDetails"
          [isCorsia]="vm.isCorsia"></app-flight-data-table>
      </ng-container>
    </ng-container>

    <app-return-to-link></app-return-to-link>
  `,
  styles: `
    .header-container {
      display: flex;
      align-items: center;
    }

    .change-link {
      margin-left: auto;
    }
  `,
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgFor,
    NgIf,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    FlightDataTableComponent,
    RouterLink,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AggregatedConsumptionFlightDataSummaryComponent {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.form.get('aggregatedEmissionDataDetails').statusChanges.pipe(
      startWith(this.form.get('aggregatedEmissionDataDetails').status),
      filter((status) => status === 'VALID' || status === 'INVALID'),
    ),
    this.store.pipe(aerQuery.selectIsCorsia),
  ]).pipe(
    map(([type, formStatus, isCorsia]) => {
      const isFormValid = formStatus === 'VALID';

      return {
        data: isFormValid ? this.formProvider.getFormValue() : null,
        pageHeader: getSummaryHeaderForTaskType(type, 'aggregatedEmissionsData'),
        isCorsia: isCorsia,
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: AggregatedConsumptionFlightDataFormProvider,
    private store: RequestTaskStore,
  ) {}
}
