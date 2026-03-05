/* eslint-disable @angular-eslint/component-max-inline-declarations */
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable, tap } from 'rxjs';

import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { AerVerificationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-verification-review-decision-group-summary/aer-verification-review-decision-group-summary.component';
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
  templateUrl: './opinion-statement.component.html',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    AerMonitoringPlanVersionsComponent,
    OpinionStatementEmissionDetailsSummaryTemplateComponent,
    OpinionStatementTotalEmissionsSummaryTemplateComponent,
    OpinionStatementChangesNotCoveredInEMPSummaryTemplateComponent,
    OpinionStatementSiteVerificationSummaryTemplateComponent,
    AerVerificationReviewDecisionGroupSummaryComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OpinionStatementComponent {
  protected fuelTypes: { id: string; key: string; value: string }[] = [];

  constructor(private store: RequestActionStore) {}

  requestActionPayload$ = this.store.pipe(aerQuery.selectRequestActionPayload);

  aerMonitoringPlanVersions$ = this.requestActionPayload$.pipe(
    map((actionPayload) => actionPayload.aerMonitoringPlanVersions),
  );

  vm$: Observable<ViewModel & AerDecisionViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    map(([payload, requestActionType, regulatorViewer]) => {
      return {
        requestActionType,
        pageHeader: aerVerifyHeaderTaskMap['opinionStatement'],
        opinionStatement: {
          ...payload.verificationReport.opinionStatement,
        },
        aerMonitoringPlanChanges: payload.aer?.aerMonitoringPlanChanges,
        totalEmissionsProvided:
          requestActionType === 'AVIATION_AER_UKETS_APPLICATION_COMPLETED'
            ? payload?.submittedEmissions?.aviationAerTotalEmissions?.totalEmissions
            : payload.totalEmissionsProvided,
        ...getAerDecisionReview(payload, requestActionType, regulatorViewer, 'opinionStatement', false),
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
