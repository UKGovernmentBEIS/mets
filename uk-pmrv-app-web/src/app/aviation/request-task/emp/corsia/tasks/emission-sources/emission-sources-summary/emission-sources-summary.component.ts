import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { combineLatest, map, mergeMap, of } from 'rxjs';

import { empCorsiaQuery } from '@aviation/request-task/emp/shared/emp-corsia.selectors';
import { EmpReviewDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-review-decision-group/emp-review-decision-group.component';
import { EmpVariationRegulatorLedDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group.component';
import { EmpVariationReviewDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import {
  issuanceReviewRequestTaskTypes,
  variationOperatorLedReviewRequestTaskTypes,
  variationSubmitRegulatorLedRequestTaskTypes,
} from '@aviation/request-task/emp/shared/util/emp.util';
import { EmpRequestTaskPayloadCorsia, requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import {
  getSummaryHeaderForTaskType,
  showReviewDecisionComponent,
  showVariationRegLedDecisionComponent,
  showVariationReviewDecisionComponent,
} from '@aviation/request-task/util';
import { FUEL_TYPES_CORSIA } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import {
  AircraftTypeTableComponent,
  appendIndex,
} from '@aviation/shared/components/emp/emission-sources/aircraft-type/table/aircraft-type-table.component';
import { isFUMM } from '@aviation/shared/components/emp/emission-sources/isFUMM';
import { MultipleMethodsSummaryTemplateComponent } from '@aviation/shared/components/emp/emission-sources/multiple-methods-summary/multiple-methods-summary.component';
import { ProcedureFormSummaryComponent } from '@aviation/shared/components/procedure-form-summary';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { EmissionFactorsSummaryComponent } from '../emission-factors-summary/emission-factors-summary.component';
import { EmissionSourcesFormModelCorsia } from '../emission-sources-form.model';

@Component({
  selector: 'app-emission-sources-summary',
  templateUrl: './emission-sources-summary.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    SharedModule,
    AircraftTypeTableComponent,
    EmissionFactorsSummaryComponent,
    MultipleMethodsSummaryTemplateComponent,
    ProcedureFormSummaryComponent,
    ReturnToLinkComponent,
    EmpReviewDecisionGroupComponent,
    EmpVariationReviewDecisionGroupComponent,
    RouterModule,
    EmpVariationRegulatorLedDecisionGroupComponent,
  ],
})
export class EmissionSourcesSummaryComponent {
  private pendingRequestService = inject(PendingRequestService);

  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private store = inject(RequestTaskStore);
  protected form = inject<FormGroup<EmissionSourcesFormModelCorsia>>(TASK_FORM_PROVIDER);

  aircraftTypes = this.form.getRawValue().aircraftTypes.map(appendIndex);

  multipleMethodsExplanation = this.form.getRawValue().multipleFuelConsumptionMethodsExplanation;
  isFUMM = isFUMM(this.store.getValue().requestTaskItem?.requestTask?.payload as EmpRequestTaskPayloadCorsia);

  vm$ = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empCorsiaQuery.selectStatusForTask('emissionSources')),
    this.store.pipe(empCorsiaQuery.selectOriginalEmpContainer),
  ]).pipe(
    map(([type, isEditable, taskStatus, originalEmpContainer]) => ({
      pageHeader: getSummaryHeaderForTaskType(type, 'emissionSources'),
      hideSubmit:
        !isEditable ||
        ['complete', 'cannot start yet'].includes(taskStatus) ||
        variationSubmitRegulatorLedRequestTaskTypes.includes(type) ||
        variationOperatorLedReviewRequestTaskTypes.includes(type) ||
        issuanceReviewRequestTaskTypes.includes(type),
      editable: isEditable,
      showDecision: showReviewDecisionComponent.includes(type),
      showVariationDecision: showVariationReviewDecisionComponent.includes(type),
      showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
      showDiff: !!originalEmpContainer,
      originalAircraftTypesInUse:
        originalEmpContainer?.emissionsMonitoringPlan?.emissionSources.aircraftTypes.map(appendIndex),
      originalFuelTypes: Array.from(
        new Set(
          originalEmpContainer?.emissionsMonitoringPlan?.emissionSources.aircraftTypes.map((at) => at.fuelTypes).flat(),
        ),
      ).map((ft) => {
        const fuelType = FUEL_TYPES_CORSIA.find((f) => f.value === ft);

        return {
          id: fuelType.value,
          key: FUEL_TYPES_CORSIA.find((f) => f.value === ft).summaryDescription,
          value: fuelType.consumption,
        };
      }),
      originalMultipleMethodsExplanation:
        originalEmpContainer?.emissionsMonitoringPlan?.emissionSources?.multipleFuelConsumptionMethodsExplanation,
    })),
  );

  addAircraftType() {
    this.router.navigate(['../aircraft-type', 'add'], {
      queryParams: { change: 'true' },
      relativeTo: this.route,
    });
  }

  onSubmit() {
    if (this.form.valid) {
      this.store.empCorsiaDelegate
        .saveEmp({ emissionSources: this.form.getRawValue() }, 'complete')
        .pipe(
          mergeMap(() => {
            return of(this.pendingRequestService.trackRequest());
          }),
        )
        .subscribe(() => {
          this.router.navigate(['../../../../'], { relativeTo: this.route });
        });
    }
  }

  get fuelTypes(): { id: string; key: string; value: string }[] {
    return Array.from(
      new Set(
        this.form.controls.aircraftTypes
          .getRawValue()
          .map((at) => at.fuelTypes)
          .flat(),
      ),
    ).map((ft) => {
      const fuelType = FUEL_TYPES_CORSIA.find((f) => f.value === ft);

      return {
        id: fuelType.value,
        key: FUEL_TYPES_CORSIA.find((f) => f.value === ft).summaryDescription,
        value: fuelType.consumption,
      };
    });
  }
}
