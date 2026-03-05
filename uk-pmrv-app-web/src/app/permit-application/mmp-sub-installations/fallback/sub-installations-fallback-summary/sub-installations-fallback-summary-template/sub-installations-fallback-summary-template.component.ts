import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { AnnualLevel, FuelInputDataSourceFA, SubInstallation } from 'pmrv-api';

interface ViewModel {
  supportingFiles: string[];
  annualSupportingFiles: string[];
  directlyAttributableEmissionsSupportingFiles: string[];
  fuelInputAndRelevantEmissionFactorSupportingFiles: string[];
  measurableHeatProducedSupportingFiles: string[];
  measurableHeatImportedSupportingFiles: string[];
  measurableHeatExportedSupportingFiles: string[];
}

@Component({
  selector: 'app-sub-installations-fallback-summary-template',
  templateUrl: './sub-installations-fallback-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubInstallationsFallbackSummaryTemplateComponent {
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
            supportingFiles: originalPermitContainerSubInstallation?.supportingFiles ?? [],
            annualSupportingFiles: originalPermitContainerSubInstallation?.annualLevel?.supportingFiles ?? [],
            directlyAttributableEmissionsSupportingFiles:
              originalPermitContainerSubInstallation?.directlyAttributableEmissions?.supportingFiles ?? [],
            fuelInputAndRelevantEmissionFactorSupportingFiles:
              originalPermitContainerSubInstallation?.fuelInputAndRelevantEmissionFactor?.supportingFiles ?? [],
            measurableHeatProducedSupportingFiles:
              originalPermitContainerSubInstallation?.measurableHeat?.measurableHeatProduced?.supportingFiles ?? [],
            measurableHeatImportedSupportingFiles:
              originalPermitContainerSubInstallation?.measurableHeat?.measurableHeatImported?.supportingFiles ?? [],
            measurableHeatExportedSupportingFiles:
              originalPermitContainerSubInstallation?.measurableHeat?.measurableHeatImported?.supportingFiles ?? [],
          }
        : {
            supportingFiles: subInstallation?.supportingFiles ?? [],
            annualSupportingFiles: subInstallation?.annualLevel?.supportingFiles ?? [],
            directlyAttributableEmissionsSupportingFiles:
              subInstallation?.directlyAttributableEmissions?.supportingFiles ?? [],
            fuelInputAndRelevantEmissionFactorSupportingFiles:
              subInstallation?.fuelInputAndRelevantEmissionFactor?.supportingFiles ?? [],
            measurableHeatProducedSupportingFiles:
              subInstallation?.measurableHeat?.measurableHeatProduced?.supportingFiles ?? [],
            measurableHeatImportedSupportingFiles:
              subInstallation?.measurableHeat?.measurableHeatImported?.supportingFiles ?? [],
            measurableHeatExportedSupportingFiles:
              subInstallation?.measurableHeat?.measurableHeatExported?.supportingFiles ?? [],
          };
    }),
  );

  subInstallationNo$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo')));

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  subInstallationDataSources(subInstallation: SubInstallation): FuelInputDataSourceFA[] {
    return (subInstallation?.fuelInputAndRelevantEmissionFactor?.['dataSources'] as FuelInputDataSourceFA[]) || [];
  }

  routerLinkAnnual = (spType: AnnualLevel['annualLevelType']): string => {
    let result = '';
    switch (spType) {
      case 'ACTIVITY_FUEL':
        result = '../annual-activity-levels-fuel';
        break;
      case 'ACTIVITY_HEAT':
        result = '../annual-activity-levels-heat';
        break;
      case 'ACTIVITY_PROCESS':
        result = '../annual-activity-levels-process';
        break;
    }
    return result;
  };
}
