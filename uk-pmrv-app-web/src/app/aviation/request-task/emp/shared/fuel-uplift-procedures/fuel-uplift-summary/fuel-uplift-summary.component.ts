import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { EmpVariationRegulatorLedDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import {
  getSummaryHeaderForTaskType,
  showReviewDecisionComponent,
  showVariationRegLedDecisionComponent,
  showVariationReviewDecisionComponent,
} from '@aviation/request-task/util';
import { FuelUpliftSummaryTemplateComponent } from '@aviation/shared/components/emp/fuel-uplift-summary-template/fuel-uplift-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmpFuelUpliftMethodProcedures } from 'pmrv-api';

import { empQuery } from '../../emp.selectors';
import { EmpReviewDecisionGroupComponent } from '../../emp-review-decision-group/emp-review-decision-group.component';
import { EmpVariationReviewDecisionGroupComponent } from '../../emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import {
  issuanceReviewRequestTaskTypes,
  variationOperatorLedReviewRequestTaskTypes,
  variationSubmitRegulatorLedRequestTaskTypes,
} from '../../util/emp.util';
import { FuelUpliftProceduresFormProvider } from '../fuel-uplift-procedures-form.provider';

interface ViewModel {
  data: EmpFuelUpliftMethodProcedures | null;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
  showVariationDecision: boolean;
  showVariationRegLedDecision: boolean;
  showDiff: boolean;
  originalData: EmpFuelUpliftMethodProcedures | null;
}

@Component({
  selector: 'app-fuel-uplift-summary',
  templateUrl: './fuel-uplift-summary.component.html',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    ReturnToLinkComponent,
    EmpReviewDecisionGroupComponent,
    EmpVariationReviewDecisionGroupComponent,
    FuelUpliftSummaryTemplateComponent,
    EmpVariationRegulatorLedDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FuelUpliftSummaryComponent {
  form = this.formProvider.form;
  isCorsia$ = this.store.pipe(empQuery.selectIsCorsia);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empQuery.selectStatusForTask('fuelUpliftMethodProcedures')),
    this.store.pipe(empQuery.selectOriginalEmpContainer),
  ]).pipe(
    map(([type, isEditable, taskStatus, originalEmpContainer]) => {
      return {
        data: this.form.valid ? this.formProvider.getFormValue() : null,
        pageHeader: getSummaryHeaderForTaskType(type, 'fuelUpliftMethodProcedures'),
        isEditable,
        hideSubmit:
          !isEditable ||
          ['complete', 'cannot start yet'].includes(taskStatus) ||
          variationSubmitRegulatorLedRequestTaskTypes.includes(type) ||
          variationOperatorLedReviewRequestTaskTypes.includes(type) ||
          issuanceReviewRequestTaskTypes.includes(type),
        showDecision: showReviewDecisionComponent.includes(type),
        showVariationDecision: showVariationReviewDecisionComponent.includes(type),
        showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
        showDiff: !!originalEmpContainer,
        originalData: originalEmpContainer?.emissionsMonitoringPlan?.fuelUpliftMethodProcedures,
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: FuelUpliftProceduresFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    if (this.form?.valid) {
      this.store.empDelegate
        .saveEmp({ fuelUpliftMethodProcedures: this.formProvider.getFormValue() }, 'complete')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../../../..'], { relativeTo: this.route, queryParams: { change: null } });
        });
    }
  }
}
