import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { empQuery } from '@aviation/request-action/emp/emp.selectors';
import { EmpSubmittedViewModel, getEmpSubmittedViewModelData } from '@aviation/request-action/emp/util/emp.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import {
  FUEL_TYPES,
  FUEL_TYPES_CORSIA,
} from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import {
  AircraftTypeDetailsWithIndex,
  AircraftTypeTableComponent,
  appendIndex,
} from '@aviation/shared/components/emp/emission-sources/aircraft-type/table/aircraft-type-table.component';
import { EmissionFactorsSummaryTemplateComponent } from '@aviation/shared/components/emp/emission-sources/emission-factors-summary-template/emission-factors-summary-template.component';
import { isFuelUseMonitoringExist, isFUMM } from '@aviation/shared/components/emp/emission-sources/isFUMM';
import { MultipleMethodsSummaryTemplateComponent } from '@aviation/shared/components/emp/emission-sources/multiple-methods-summary/multiple-methods-summary.component';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { EmpVariationRegulatorLedDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-regulator-led-decision-group-summary/emp-variation-regulator-led-decision-group-summary.component';
import { EmpVariationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-review-decision-group-summary/emp-variation-review-decision-group-summary.component';
import { ServiceContactDetailsSummaryTemplateComponent } from '@aviation/shared/components/emp/service-contact-details-summary-template/service-contact-details-summary-template.component';
import { ProcedureFormSummaryComponent } from '@aviation/shared/components/procedure-form-summary';
import { SharedModule } from '@shared/shared.module';

import { EmpEmissionSources, EmpProcedureForm } from 'pmrv-api';

interface ViewModel {
  aircraftTypesInUse: AircraftTypeDetailsWithIndex[];
  aircraftTypesToBeUsed: AircraftTypeDetailsWithIndex[];
  fuelTypes: { id: string; key: string; value: string }[];
  multipleMethodsExplanation: string;
  additionalAircraftsMonitoringApproach: EmpProcedureForm;
  isFUMM: boolean;
  originalAircraftTypesInUse: AircraftTypeDetailsWithIndex[];
  originalAircraftTypesToBeUsed: AircraftTypeDetailsWithIndex[];
  originalFuelTypes: { id: string; key: string; value: string }[];
  originalMultipleMethodsExplanation: string;
  originalAdditionalAircraftsMonitoringApproach: EmpProcedureForm;
  originalIsFUMM: boolean;
  editable: boolean;
  isCorsia: boolean;
}

@Component({
  selector: 'app-emission-sources',
  standalone: true,
  imports: [
    RequestActionTaskComponent,
    EmpReviewDecisionGroupSummaryComponent,
    ServiceContactDetailsSummaryTemplateComponent,
    AircraftTypeTableComponent,
    EmissionFactorsSummaryTemplateComponent,
    MultipleMethodsSummaryTemplateComponent,
    ProcedureFormSummaryComponent,
    EmpVariationRegulatorLedDecisionGroupSummaryComponent,
    SharedModule,
    EmpVariationReviewDecisionGroupSummaryComponent,
  ],
  templateUrl: './emission-sources.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionSourcesComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & EmpSubmittedViewModel> = combineLatest([
    this.store.pipe(empQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectIsCorsia),
  ]).pipe(
    map(([payload, regulatorViewer, requestActionType, isCorsia]) => ({
      isCorsia,
      aircraftTypesInUse: payload.emissionsMonitoringPlan.emissionSources.aircraftTypes
        .map(appendIndex)
        .filter(({ isCurrentlyUsed }) => isCurrentlyUsed),
      aircraftTypesToBeUsed: payload.emissionsMonitoringPlan.emissionSources.aircraftTypes
        .map(appendIndex)
        .filter(({ isCurrentlyUsed }) => !isCurrentlyUsed),
      fuelTypes: this.getFuelTypes(payload.emissionsMonitoringPlan.emissionSources, isCorsia),
      multipleMethodsExplanation:
        payload.emissionsMonitoringPlan.emissionSources.multipleFuelConsumptionMethodsExplanation,
      additionalAircraftsMonitoringApproach:
        payload.emissionsMonitoringPlan.emissionSources.additionalAircraftMonitoringApproach,
      isFUMM: isFUMM(payload),
      originalAircraftTypesInUse:
        payload.originalEmpContainer?.emissionsMonitoringPlan.emissionSources.aircraftTypes
          .map(appendIndex)
          .filter(({ isCurrentlyUsed }) => isCurrentlyUsed) ?? [],
      originalAircraftTypesToBeUsed:
        payload.originalEmpContainer?.emissionsMonitoringPlan.emissionSources.aircraftTypes
          .map(appendIndex)
          .filter(({ isCurrentlyUsed }) => !isCurrentlyUsed) ?? [],
      originalFuelTypes: payload.originalEmpContainer
        ? this.getFuelTypes(payload.originalEmpContainer.emissionsMonitoringPlan.emissionSources, isCorsia)
        : null,
      originalMultipleMethodsExplanation:
        payload.originalEmpContainer?.emissionsMonitoringPlan.emissionSources.multipleFuelConsumptionMethodsExplanation,
      originalAdditionalAircraftsMonitoringApproach:
        payload.originalEmpContainer?.emissionsMonitoringPlan.emissionSources.additionalAircraftMonitoringApproach,
      originalIsFUMM: isFuelUseMonitoringExist(payload.originalEmpContainer?.emissionsMonitoringPlan),
      editable: false,
      ...getEmpSubmittedViewModelData(
        requestActionType,
        payload,
        regulatorViewer,
        this.store.empDelegate.baseFileAttachmentDownloadUrl,
        'emissionSources',
      ),
    })),
  );

  private getFuelTypes(
    emissionSources: EmpEmissionSources,
    isCorsia: boolean,
  ): { id: string; key: string; value: string }[] {
    return Array.from(new Set(emissionSources.aircraftTypes.map((at) => at.fuelTypes).flat())).map((ft) => {
      const fuel_types = isCorsia ? FUEL_TYPES_CORSIA : FUEL_TYPES;
      const fuelType = fuel_types.find((f) => f.value === ft);
      return {
        id: fuelType.value,
        key: fuel_types.find((f) => f.value === ft).summaryDescription,
        value: ft === 'OTHER' ? emissionSources.otherFuelExplanation : fuelType.consumption,
      };
    });
  }
}
