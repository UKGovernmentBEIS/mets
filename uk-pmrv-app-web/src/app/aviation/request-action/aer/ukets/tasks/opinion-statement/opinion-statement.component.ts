/* eslint-disable @angular-eslint/component-max-inline-declarations */
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable, tap } from 'rxjs';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { AerMonitoringPlanVersionsComponent } from '@aviation/shared/components/aer/monitoring-plan-versions';
import OpinionStatementChangesNotCoveredInEMPSummaryTemplateComponent from '@aviation/shared/components/aer-verify/opinion-statement/changes-not-covered-in-emp/opinion-statement-changes-not-covered-in-emp-summary-template.component';
import OpinionStatementEmissionDetailsSummaryTemplateComponent from '@aviation/shared/components/aer-verify/opinion-statement/emission-details/opinion-statement-emission-details-summary-template.compmonent';
import OpinionStatementSiteVerificationSummaryTemplateComponent from '@aviation/shared/components/aer-verify/opinion-statement/site-verification/opinion-statement-site-verification-summary-template.component';
import OpinionStatementTotalEmissionsSummaryTemplateComponent from '@aviation/shared/components/aer-verify/opinion-statement/total-emissions/opinion-statement-total-emissions-summary-template.component';
import { FUEL_TYPES } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import { SharedModule } from '@shared/shared.module';

import { AviationAerMonitoringPlanChanges, AviationAerOpinionStatement, RequestActionDTO } from 'pmrv-api';

import { aerQuery } from '../../aer-ukets.selectors';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  opinionStatement: AviationAerOpinionStatement;
  totalEmissionsProvided: string;
  aerMonitoringPlanChanges: AviationAerMonitoringPlanChanges;
}

@Component({
  selector: 'app-opinion-statement',
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <!-- Emission details -->
      <app-opinion-statement-emission-details-summary-template
        [fuelTypes]="fuelTypes"
        [monitoringApproachType]="vm.opinionStatement.monitoringApproachType"
      ></app-opinion-statement-emission-details-summary-template>

      <!-- Total emissions -->
      <app-opinion-statement-total-emissions-summary-template
        [totalEmissionsProvided]="vm.totalEmissionsProvided"
        [emissionsCorrect]="vm.opinionStatement.emissionsCorrect"
        [manuallyProvidedEmissions]="vm.opinionStatement.manuallyProvidedEmissions"
      ></app-opinion-statement-total-emissions-summary-template>

      <!-- Monitoring plan -->
      <app-aer-monitoring-plan-versions [planVersions]="[]"></app-aer-monitoring-plan-versions>

      <!-- Changes not covered in EMP -->
      <app-opinion-statement-changes-not-covered-in-emp-summary-template
        [aerMonitoringPlanChanges]="vm.aerMonitoringPlanChanges"
        [additionalChangesNotCovered]="vm.opinionStatement.additionalChangesNotCovered"
        [additionalChangesNotCoveredDetails]="vm.opinionStatement.additionalChangesNotCoveredDetails"
      ></app-opinion-statement-changes-not-covered-in-emp-summary-template>

      <!-- Site verification -->
      <app-opinion-statement-site-verification-summary-template
        [siteVisit]="vm.opinionStatement.siteVisit"
      ></app-opinion-statement-site-verification-summary-template>
    </app-request-action-task>
  `,
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    AerMonitoringPlanVersionsComponent,
    OpinionStatementEmissionDetailsSummaryTemplateComponent,
    OpinionStatementTotalEmissionsSummaryTemplateComponent,
    OpinionStatementChangesNotCoveredInEMPSummaryTemplateComponent,
    OpinionStatementSiteVerificationSummaryTemplateComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OpinionStatementComponent {
  protected fuelTypes: { id: string; key: string; value: string }[] = [];

  constructor(private store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => {
      return {
        requestActionType,
        pageHeader: 'Opinion statement',
        opinionStatement: {
          ...payload.verificationReport.opinionStatement,
        },
        aerMonitoringPlanChanges: payload.aer?.aerMonitoringPlanChanges,
        totalEmissionsProvided: payload.totalEmissionsProvided,
      };
    }),
    tap((data) => {
      this.fuelTypes = this.getFuelTypes(data.opinionStatement.fuelTypes);
    }),
  );

  private getFuelTypes(
    fuelTypes: AviationAerOpinionStatement['fuelTypes'],
  ): { id: string; key: string; value: string }[] {
    return fuelTypes.map((ft) => {
      return {
        id: ft,
        key: FUEL_TYPES.find((f) => f.value === ft).summaryDescription,
        value: FUEL_TYPES.find((f) => f.value === ft).consumption,
      };
    });
  }
}
