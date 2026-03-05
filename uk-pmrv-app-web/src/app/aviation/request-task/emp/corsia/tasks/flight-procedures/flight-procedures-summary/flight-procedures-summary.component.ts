import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { empCorsiaQuery } from '@aviation/request-task/emp/shared/emp-corsia.selectors';
import { EmpReviewDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-review-decision-group/emp-review-decision-group.component';
import { EmpVariationRegulatorLedDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group.component';
import { EmpVariationReviewDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import { variationSubmitRegulatorLedRequestTaskTypes } from '@aviation/request-task/emp/shared/util/emp.util';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import {
  getSubtaskSummaryValues,
  getSummaryHeaderForTaskType,
  showReviewDecisionComponent,
  showVariationRegLedDecisionComponent,
  showVariationReviewDecisionComponent,
} from '@aviation/request-task/util';
import { FlightProceduresDataTableComponent } from '@aviation/shared/components/emp/flight-procedures-data-table';
import { ProcedureFormSummaryComponent } from '@aviation/shared/components/procedure-form-summary';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { ViewModel } from '../flight-procedures.interface';
import { FlightProceduresFormProvider } from '../flight-procedures-form.provider';

@Component({
  selector: 'app-flight-procedures-summary',
  templateUrl: './flight-procedures-summary.component.html',
  standalone: true,
  styles: `
    .header-container {
      display: flex;
      align-items: center;
    }
    .change-link {
      margin-left: 68.5%;
      margin-top: -4.6rem;
      font-size: 1.1875rem;
      line-height: 1.3157894737;
    }
  `,
  imports: [
    SharedModule,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    FlightProceduresDataTableComponent,
    EmpReviewDecisionGroupComponent,
    EmpVariationReviewDecisionGroupComponent,
    EmpVariationRegulatorLedDecisionGroupComponent,
    ProcedureFormSummaryComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FlightProceduresSummaryComponent {
  protected form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empCorsiaQuery.selectStatusForTask('flightAndAircraftProcedures')),
    this.store.pipe(empCorsiaQuery.selectOriginalEmpContainer),
  ]).pipe(
    map(([type, isEditable, taskStatus, originalEmpContainer]) => ({
      pageHeader: getSummaryHeaderForTaskType(type, 'flightAndAircraftProcedures'),
      isEditable,
      data: getSubtaskSummaryValues(this.formProvider.form),
      showDecision: showReviewDecisionComponent.includes(type),
      hideSubmit:
        !isEditable ||
        ['complete', 'cannot start yet'].includes(taskStatus) ||
        variationSubmitRegulatorLedRequestTaskTypes.includes(type),
      showDiff: !!originalEmpContainer,
      showVariationDecision: showVariationReviewDecisionComponent.includes(type),
      showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
      originalData: originalEmpContainer?.emissionsMonitoringPlan?.flightAndAircraftProcedures,
    })),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: FlightProceduresFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  onSubmit() {
    if (this.form.invalid) return;

    this.store.empCorsiaDelegate
      .saveEmp({ flightAndAircraftProcedures: this.formProvider.getFormValue() }, 'complete')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empCorsiaDelegate.setFlightProcedures(this.formProvider.getFormValue());
        this.router.navigate(['../../../..'], { relativeTo: this.route, replaceUrl: true });
      });
  }
}
