import { NgFor, NgForOf, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, mergeMap, Observable, of } from 'rxjs';

import { empQuery } from '@aviation/request-task/emp/shared/emp.selectors';
import { EmpReviewDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-review-decision-group/emp-review-decision-group.component';
import { EmpVariationRegulatorLedDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group.component';
import { EmpVariationReviewDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import {
  getSubtaskSummaryValues,
  getSummaryHeaderForTaskType,
  showReviewDecisionComponent,
  showVariationRegLedDecisionComponent,
  showVariationReviewDecisionComponent,
} from '@aviation/request-task/util';
import { ProcedureFormSummaryComponent } from '@aviation/shared/components/procedure-form-summary';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { EmpFlightAndAircraftProcedures } from 'pmrv-api';

import { FlightProceduresFormProvider } from '../flight-procedures-form.provider';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  data: EmpFlightAndAircraftProcedures;
  hideSubmit: boolean;
  showDecision: boolean;
  showVariationDecision: boolean;
  showVariationRegLedDecision: boolean;
  showDiff: boolean;
  originalData: EmpFlightAndAircraftProcedures;
}
@Component({
  selector: 'app-flight-procedures-summary',
  templateUrl: './flight-procedures-summary.component.html',
  standalone: true,
  imports: [
    SharedModule,
    NgFor,
    NgIf,
    NgForOf,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    EmpReviewDecisionGroupComponent,
    EmpVariationReviewDecisionGroupComponent,
    ProcedureFormSummaryComponent,
    EmpVariationRegulatorLedDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FlightProceduresSummaryComponent {
  private store = inject(RequestTaskStore);
  private formProvider = inject<FlightProceduresFormProvider>(TASK_FORM_PROVIDER);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);

  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empQuery.selectStatusForTask('flightAndAircraftProcedures')),
    this.store.pipe(empQuery.selectOriginalEmpContainer),
  ]).pipe(
    map(([type, isEditable, taskStatus, originalEmpContainer]) => ({
      pageHeader: getSummaryHeaderForTaskType(type, 'flightAndAircraftProcedures'),
      isEditable,
      data: getSubtaskSummaryValues(this.formProvider.form),
      hideSubmit:
        !isEditable ||
        ['complete', 'cannot start yet'].includes(taskStatus) ||
        ['EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT'].includes(type),
      showDecision: showReviewDecisionComponent.includes(type),
      showDiff: !!originalEmpContainer,
      showVariationDecision: showVariationReviewDecisionComponent.includes(type),
      showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
      originalData: originalEmpContainer?.emissionsMonitoringPlan?.flightAndAircraftProcedures,
    })),
  );

  onSubmit() {
    if (this.form.valid) {
      this.store.empUkEtsDelegate
        .saveEmp({ flightAndAircraftProcedures: this.formProvider.getFormValue() }, 'complete')
        .pipe(
          mergeMap(() => {
            return of(this.pendingRequestService.trackRequest());
          }),
        )
        .subscribe(() => {
          this.router.navigate(['../../../../'], { relativeTo: this.route, queryParams: { change: null } });
        });
    }
  }
}
