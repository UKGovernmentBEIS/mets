import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { RouterLink, RouterLinkWithHref } from '@angular/router';

import { combineLatest, filter, map, Observable, startWith } from 'rxjs';

import { AircraftTypesDataFormProvider } from '@aviation/request-task/aer/shared/aircraft-types-data/aircraft-types-data-form.provider';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType } from '@aviation/request-task/util';
import { AircraftTypesDataTableComponent } from '@aviation/shared/components/aer/aircraft-types-table/aircraft-types-data-table.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerAircraftData } from 'pmrv-api';

interface ViewModel {
  data: AviationAerAircraftData;
  pageHeader: string;
}

@Component({
  selector: 'app-aircraft-types-data-summary',
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.pageHeader }}</app-page-heading>

      <ng-container *ngIf="vm.data">
        <div class="header-container">
          <h2 class="govuk-heading-m govuk-!-width-two-thirds">Aircraft Data uploaded data</h2>
        </div>

        <app-aircraft-types-data-table
          [headingText]="'File uploaded'"
          [aviationAerAircraftDataDetails]="vm.data.aviationAerAircraftDataDetails"
        ></app-aircraft-types-data-table>
      </ng-container>
    </ng-container>

    <app-return-to-link></app-return-to-link>
  `,
  styles: [
    `
      .header-container {
        display: flex;
        align-items: center;
      }

      .change-link {
        margin-left: auto;
      }
    `,
  ],
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgFor,
    NgIf,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    AircraftTypesDataTableComponent,
    RouterLink,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AircraftTypesDataSummaryComponent {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.form.get('aviationAerAircraftDataDetails').statusChanges.pipe(
      startWith(this.form.get('aviationAerAircraftDataDetails').status),
      filter((status) => status === 'VALID' || status === 'INVALID'),
    ),
  ]).pipe(
    map(([type, formStatus]) => {
      const isFormValid = formStatus === 'VALID';

      return {
        data: isFormValid ? this.formProvider.getFormValue() : null,
        pageHeader: getSummaryHeaderForTaskType(type, 'aviationAerAircraftData'),
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: AircraftTypesDataFormProvider,
    private store: RequestTaskStore,
  ) {}
}
