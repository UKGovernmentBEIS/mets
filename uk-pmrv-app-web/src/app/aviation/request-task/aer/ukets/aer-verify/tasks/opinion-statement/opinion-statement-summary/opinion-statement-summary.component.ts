import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable, tap } from 'rxjs';

import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { AerMonitoringPlanVersionsComponent } from '@aviation/shared/components/aer/monitoring-plan-versions';
import OpinionStatementChangesNotCoveredInEMPSummaryTemplateComponent from '@aviation/shared/components/aer-verify/opinion-statement/changes-not-covered-in-emp/opinion-statement-changes-not-covered-in-emp-summary-template.component';
import OpinionStatementEmissionDetailsSummaryTemplateComponent from '@aviation/shared/components/aer-verify/opinion-statement/emission-details/opinion-statement-emission-details-summary-template.compmonent';
import OpinionStatementSiteVerificationSummaryTemplateComponent from '@aviation/shared/components/aer-verify/opinion-statement/site-verification/opinion-statement-site-verification-summary-template.component';
import OpinionStatementTotalEmissionsSummaryTemplateComponent from '@aviation/shared/components/aer-verify/opinion-statement/total-emissions/opinion-statement-total-emissions-summary-template.component';
import { FUEL_TYPES } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerOpinionStatement, AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import { aerVerifyQuery } from '../../../aer-verify.selector';
import { OpinionStatementFormProvider } from '../opinion-statement-form.provider';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  opinionStatement: AviationAerOpinionStatement;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-opinion-statement-summary',
  templateUrl: './opinion-statement-summary.component.html',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    RouterLinkWithHref,
    AerMonitoringPlanVersionsComponent,
    OpinionStatementEmissionDetailsSummaryTemplateComponent,
    OpinionStatementTotalEmissionsSummaryTemplateComponent,
    OpinionStatementChangesNotCoveredInEMPSummaryTemplateComponent,
    OpinionStatementSiteVerificationSummaryTemplateComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OpinionStatementSummaryComponent {
  protected fuelTypes: { id: string; key: string; value: string }[] = [];

  protected totalEmissionsProvided = (
    this.store.getState().requestTaskItem.requestTask
      .payload as AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload
  ).totalEmissionsProvided;

  protected aerMonitoringPlanChanges = (
    this.store.getState().requestTaskItem.requestTask
      .payload as AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload
  ).aer.aerMonitoringPlanChanges;

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: OpinionStatementFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerVerifyQuery.selectStatusForTask('opinionStatement')),
  ]).pipe(
    map(([type, isEditable, taskStatus]) => {
      return {
        pageHeader: getSummaryHeaderForTaskType(type, 'opinionStatement'),
        isEditable,
        opinionStatement: this.formProvider.getFormValue(),
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
    tap((data) => {
      this.fuelTypes = this.getFuelTypes(data.opinionStatement.fuelTypes);
    }),
  );

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyStoreDelegate)
      .saveAerVerify({ opinionStatement: this.formProvider.getFormValue() }, 'complete')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyStoreDelegate).setOpinionStatement(this.formProvider.getFormValue());
        this.router.navigate(['../../..'], { relativeTo: this.route, replaceUrl: true });
      });
  }

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
