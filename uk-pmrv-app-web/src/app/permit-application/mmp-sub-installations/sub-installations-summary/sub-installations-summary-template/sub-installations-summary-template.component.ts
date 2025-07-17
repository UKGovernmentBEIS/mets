import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { FuelInputDataSourcePB, SpecialProduct, SubInstallation } from 'pmrv-api';

interface ViewModel {
  dataSourcesKey: string;
  supportingFiles: string[];
  annualSupportingFiles: string[];
  exchangeabilitySupportingFiles: string[];
  importedMeasurableHeatFlowSupportingFiles: string[];
  directlyAttributableEmissionsSupportingFiles: string[];
  fuelInputAndRelevantEmissionFactorSupportingFiles: string[];
  importedExportedMeasurableHeatSupportingFiles: string[];
  wasteGasBalanceSupportingFiles: string[];
  specialProductSupportingFiles: string[];
}

@Component({
  selector: 'app-sub-installations-summary-template',
  templateUrl: './sub-installations-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubInstallationsSummaryTemplateComponent {
  @Input() showOriginal = false;
  @Input() isPreview: boolean;

  readonly vm$: Observable<ViewModel> = combineLatest([this.store, this.route.paramMap]).pipe(
    map(([state, paramMap]) => {
      const subInstallationNo = +paramMap.get('subInstallationNo');
      const originalPermitContainerSubInstallation =
        (
          state as any
        )?.originalPermitContainer?.permit?.monitoringMethodologyPlans?.digitizedPlan?.subInstallations?.find(
          (x) => +x.subInstallationNo === subInstallationNo,
        ) ?? null;

      const subInstallation =
        state.permit.monitoringMethodologyPlans?.digitizedPlan?.subInstallations?.find(
          (x) => +x.subInstallationNo === subInstallationNo,
        ) ?? null;

      return this.showOriginal
        ? {
            dataSourcesKey:
              originalPermitContainerSubInstallation?.specialProduct?.specialProductType === 'REFINERY_PRODUCTS'
                ? 'refineryProductsDataSources'
                : 'dataSources',
            supportingFiles: originalPermitContainerSubInstallation?.supportingFiles ?? [],
            annualSupportingFiles: originalPermitContainerSubInstallation?.annualLevel?.supportingFiles ?? [],
            exchangeabilitySupportingFiles:
              originalPermitContainerSubInstallation?.fuelAndElectricityExchangeability?.supportingFiles ?? [],
            importedMeasurableHeatFlowSupportingFiles:
              originalPermitContainerSubInstallation?.importedMeasurableHeatFlow?.supportingFiles ?? [],
            directlyAttributableEmissionsSupportingFiles:
              originalPermitContainerSubInstallation?.directlyAttributableEmissions?.supportingFiles ?? [],
            fuelInputAndRelevantEmissionFactorSupportingFiles:
              originalPermitContainerSubInstallation?.fuelInputAndRelevantEmissionFactor?.supportingFiles ?? [],
            importedExportedMeasurableHeatSupportingFiles:
              originalPermitContainerSubInstallation?.importedExportedMeasurableHeat?.supportingFiles ?? [],
            wasteGasBalanceSupportingFiles:
              originalPermitContainerSubInstallation?.wasteGasBalance?.supportingFiles ?? [],
            specialProductSupportingFiles:
              originalPermitContainerSubInstallation?.specialProduct?.supportingFiles ?? [],
          }
        : {
            dataSourcesKey:
              subInstallation?.specialProduct?.specialProductType === 'REFINERY_PRODUCTS'
                ? 'refineryProductsDataSources'
                : 'dataSources',
            supportingFiles: subInstallation?.supportingFiles ?? [],
            annualSupportingFiles: subInstallation?.annualLevel?.supportingFiles ?? [],
            exchangeabilitySupportingFiles: subInstallation?.fuelAndElectricityExchangeability?.supportingFiles ?? [],
            importedMeasurableHeatFlowSupportingFiles:
              subInstallation?.importedMeasurableHeatFlow?.supportingFiles ?? [],
            directlyAttributableEmissionsSupportingFiles:
              subInstallation?.directlyAttributableEmissions?.supportingFiles ?? [],
            fuelInputAndRelevantEmissionFactorSupportingFiles:
              subInstallation?.fuelInputAndRelevantEmissionFactor?.supportingFiles ?? [],
            importedExportedMeasurableHeatSupportingFiles:
              subInstallation?.importedExportedMeasurableHeat?.supportingFiles ?? [],
            wasteGasBalanceSupportingFiles: subInstallation?.wasteGasBalance?.supportingFiles ?? [],
            specialProductSupportingFiles: subInstallation?.specialProduct?.supportingFiles ?? [],
          };
    }),
  );

  subInstallationNo$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo')));

  subInstallationDataSources(subInstallation: SubInstallation): FuelInputDataSourcePB[] {
    return (subInstallation?.fuelInputAndRelevantEmissionFactor?.['dataSources'] as FuelInputDataSourcePB[]) || [];
  }

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  routerLink = (spType: SpecialProduct['specialProductType']): string =>
    `../calculation-${spType.toLowerCase().replace(/_/g, '-')}`;
}
