import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';

import { map } from 'rxjs';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { SubInstallation } from 'pmrv-api';

export function mmpSubInstallationStatus(state: PermitApplicationState): TaskItemStatus {
  const productBenchmarks = state.permit?.monitoringMethodologyPlans?.digitizedPlan?.subInstallations?.filter((x) =>
    isProductBenchmark(x.subInstallationType),
  );

  const fallbackApproaches = state.permit?.monitoringMethodologyPlans?.digitizedPlan?.subInstallations?.filter((x) =>
    isFallbackApproach(x.subInstallationType),
  );
  return (state.competentAuthority !== 'OPRED' &&
    (productBenchmarks?.length > 0 || fallbackApproaches?.length > 0) &&
    productBenchmarks?.every(
      (x, index) => state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Product_Benchmark']?.[index],
    ) &&
    fallbackApproaches.every(
      (x, index) => state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Fallback_Approach']?.[index],
    )) ||
    (state.competentAuthority === 'OPRED' &&
      fallbackApproaches?.length > 0 &&
      fallbackApproaches.every(
        (x, index) => state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Fallback_Approach']?.[index],
      ))
    ? 'complete'
    : productBenchmarks?.length > 0 || fallbackApproaches?.length > 0
      ? 'in progress'
      : 'not started';
}

export function mmpSubInstallationProductBenchmarkStatus(state: PermitApplicationState, index: number): TaskItemStatus {
  const productBenchmarks = state.permit?.monitoringMethodologyPlans?.digitizedPlan?.subInstallations?.filter((x) =>
    isProductBenchmark(x.subInstallationType),
  );
  return state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Product_Benchmark']?.[index]
    ? 'complete'
    : productBenchmarks?.[index]?.subInstallationNo || productBenchmarks?.[index]?.subInstallationType
      ? 'in progress'
      : 'not started';
}

export function mmpSubInstallationFallbackApproachStatus(state: PermitApplicationState, index: number): TaskItemStatus {
  const fallbackApproaches = state.permit?.monitoringMethodologyPlans?.digitizedPlan?.subInstallations?.filter((x) =>
    isFallbackApproach(x.subInstallationType),
  );
  return state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Fallback_Approach']?.[index]
    ? 'complete'
    : fallbackApproaches?.[index]?.subInstallationNo || fallbackApproaches?.[index]?.subInstallationType
      ? 'in progress'
      : 'not started';
}

export const resolveImportedMeasureableHeatFlowsBackLink: ResolveFn<any> = (route: ActivatedRouteSnapshot) => {
  const subInstallationNo = route.paramMap?.get('subInstallationNo');
  return inject(PermitApplicationStore)
    .getTask('monitoringMethodologyPlans')
    .pipe(
      map((monitoringMethodologyPlans) => {
        return subInstallationNo || subInstallationNo === '0'
          ? monitoringMethodologyPlans?.digitizedPlan?.subInstallations?.find(
              (x) => x.subInstallationNo == subInstallationNo,
            )?.subInstallationType
          : monitoringMethodologyPlans?.digitizedPlan?.subInstallations?.[
              monitoringMethodologyPlans?.digitizedPlan?.subInstallations.length - 1
            ]?.subInstallationType;
      }),
      map((subInstallationType) => {
        return exchangeabilityTypes.includes(subInstallationType) ? '../exchangeability' : '../annual-production-level';
      }),
    );
};

export const resolveDirectlyAttributableEmissionsFABackLink: ResolveFn<any> = (route: ActivatedRouteSnapshot) => {
  return inject(PermitApplicationStore)
    .getTask('monitoringMethodologyPlans')
    .pipe(
      map((monitoringMethodologyPlans) => {
        const subInstallationNo = route.paramMap?.get('subInstallationNo');
        return subInstallationNo || subInstallationNo === '0'
          ? monitoringMethodologyPlans?.digitizedPlan?.subInstallations?.find(
              (x) => x.subInstallationNo == subInstallationNo,
            )?.subInstallationType
          : monitoringMethodologyPlans?.digitizedPlan?.subInstallations?.[
              monitoringMethodologyPlans?.digitizedPlan?.subInstallations.length - 1
            ]?.subInstallationType;
      }),
      map((subInstallationType) => {
        const template = `../annual-activity-levels`;
        const heatTemplate = `${template}-heat`;
        const fuelTemplate = `${template}-fuel`;

        const backlinks = {
          HEAT_BENCHMARK_CL: heatTemplate,
          HEAT_BENCHMARK_NON_CL: heatTemplate,
          DISTRICT_HEATING_NON_CL: heatTemplate,
          FUEL_BENCHMARK_CL: fuelTemplate,
          FUEL_BENCHMARK_NON_CL: fuelTemplate,
        };

        return backlinks[subInstallationType] ?? '../../../';
      }),
    );
};

export function isProductBenchmark(type: SubInstallation['subInstallationType']): boolean {
  if (!fallbackApproachTypes.includes(type)) {
    return true;
  }
  return false;
}

export function isFallbackApproach(type: SubInstallation['subInstallationType']): boolean {
  if (fallbackApproachTypes.includes(type)) {
    return true;
  }
  return false;
}

export function isProductBenchmarkComplete(productBenchmark: SubInstallation): boolean {
  const fuelAndElectricityExchangeability = exchangeabilityTypes.includes(productBenchmark?.subInstallationType)
    ? productBenchmark?.fuelAndElectricityExchangeability
    : true;

  // TODO: We should use a better way for this piece of code
  const specialRefineryProducts =
    productBenchmark?.subInstallationType === 'REFINERY_PRODUCTS' ? productBenchmark?.specialProduct : true;
  const specialLime = productBenchmark?.subInstallationType === 'LIME' ? productBenchmark?.specialProduct : true;
  const specialDolime = productBenchmark?.subInstallationType === 'DOLIME' ? productBenchmark?.specialProduct : true;
  const specialSteamCracking =
    productBenchmark?.subInstallationType === 'STEAM_CRACKING' ? productBenchmark?.specialProduct : true;
  const specialAromatics =
    productBenchmark?.subInstallationType === 'AROMATICS' ? productBenchmark?.specialProduct : true;
  const specialHydrogen =
    productBenchmark?.subInstallationType === 'HYDROGEN' ? productBenchmark?.specialProduct : true;
  const specialSynthesisGas =
    productBenchmark?.subInstallationType === 'SYNTHESIS_GAS' ? productBenchmark?.specialProduct : true;
  const specialEthyleneOxideGlycols =
    productBenchmark?.subInstallationType === 'ETHYLENE_OXIDE_ETHYLENE_GLYCOLS'
      ? productBenchmark?.specialProduct
      : true;
  const specialVinylChlorideMonomer =
    productBenchmark?.subInstallationType === 'VINYL_CHLORIDE_MONOMER' ? productBenchmark?.specialProduct : true;

  if (
    productBenchmark &&
    productBenchmark?.subInstallationType &&
    productBenchmark?.description &&
    productBenchmark?.annualLevel &&
    productBenchmark?.importedMeasurableHeatFlow &&
    productBenchmark?.directlyAttributableEmissions &&
    productBenchmark?.fuelInputAndRelevantEmissionFactor &&
    productBenchmark?.importedExportedMeasurableHeat &&
    productBenchmark?.wasteGasBalance &&
    fuelAndElectricityExchangeability &&
    specialRefineryProducts &&
    specialLime &&
    specialDolime &&
    specialSteamCracking &&
    specialAromatics &&
    specialHydrogen &&
    specialSynthesisGas &&
    specialEthyleneOxideGlycols &&
    specialVinylChlorideMonomer
  ) {
    return true;
  }
  return false;
}

export function isFallbackApproachComplete(fallbackApproach: SubInstallation): boolean {
  const directlyAttributableEmissions =
    fallbackApproach?.subInstallationType === 'HEAT_BENCHMARK_CL' ||
    fallbackApproach?.subInstallationType === 'HEAT_BENCHMARK_NON_CL' ||
    fallbackApproach?.subInstallationType === 'DISTRICT_HEATING_NON_CL' ||
    fallbackApproach?.subInstallationType === 'FUEL_BENCHMARK_CL' ||
    fallbackApproach?.subInstallationType === 'FUEL_BENCHMARK_NON_CL'
      ? fallbackApproach?.directlyAttributableEmissions
      : true;

  const fuelInputAndRelevantEmissionFactor =
    fallbackApproach?.subInstallationType === 'HEAT_BENCHMARK_CL' ||
    fallbackApproach?.subInstallationType === 'HEAT_BENCHMARK_NON_CL' ||
    fallbackApproach?.subInstallationType === 'DISTRICT_HEATING_NON_CL' ||
    fallbackApproach?.subInstallationType === 'FUEL_BENCHMARK_CL' ||
    fallbackApproach?.subInstallationType === 'FUEL_BENCHMARK_NON_CL'
      ? fallbackApproach?.fuelInputAndRelevantEmissionFactor
      : true;

  const measurableHeatProduced =
    fallbackApproach?.subInstallationType === 'HEAT_BENCHMARK_CL' ||
    fallbackApproach?.subInstallationType === 'HEAT_BENCHMARK_NON_CL' ||
    fallbackApproach?.subInstallationType === 'DISTRICT_HEATING_NON_CL'
      ? fallbackApproach?.measurableHeat?.measurableHeatProduced
      : true;

  const measurableHeatImported =
    fallbackApproach?.subInstallationType === 'HEAT_BENCHMARK_CL' ||
    fallbackApproach?.subInstallationType === 'HEAT_BENCHMARK_NON_CL' ||
    fallbackApproach?.subInstallationType === 'DISTRICT_HEATING_NON_CL'
      ? fallbackApproach?.measurableHeat?.measurableHeatImported
      : true;

  const measurableHeatExported =
    fallbackApproach?.subInstallationType === 'FUEL_BENCHMARK_CL' ||
    fallbackApproach?.subInstallationType === 'FUEL_BENCHMARK_NON_CL'
      ? fallbackApproach?.measurableHeat?.measurableHeatExported
      : true;

  if (
    fallbackApproach &&
    fallbackApproach?.subInstallationType &&
    fallbackApproach?.description &&
    fallbackApproach?.annualLevel &&
    directlyAttributableEmissions &&
    fuelInputAndRelevantEmissionFactor &&
    measurableHeatProduced &&
    measurableHeatImported &&
    measurableHeatExported
  ) {
    return true;
  }
  return false;
}

export const fallbackApproachTypes: SubInstallation['subInstallationType'][] = [
  'HEAT_BENCHMARK_CL',
  'HEAT_BENCHMARK_NON_CL',
  'DISTRICT_HEATING_NON_CL',
  'FUEL_BENCHMARK_CL',
  'FUEL_BENCHMARK_NON_CL',
  'PROCESS_EMISSIONS_CL',
  'PROCESS_EMISSIONS_NON_CL',
];

export const productBenchmarkTypes: SubInstallation['subInstallationType'][] = [
  'PRIMARY_ALUMINIUM',
  'ADIPIC_ACID',
  'AMMONIA',
  'AROMATICS',
  'BOTTLES_AND_JARS_OF_COLOURED_GLASS',
  'BOTTLES_AND_JARS_OF_COLOURLESS_GLASS',
  'CARBON_BLACK',
  'COATED_CARTON_BOARD',
  'COATED_FINE_PAPER',
  'COKE',
  'CONTINUOUS_FILAMENT_GLASS_FIBRE_PRODUCTS',
  'DOLIME',
  'DRIED_SECONDARY_GYPSUM',
  'EAF_CARBON_STEEL',
  'EAF_HIGH_ALLOY_STEEL',
  'E_PVC',
  'ETHYLENE_OXIDE_ETHYLENE_GLYCOLS',
  'FACING_BRICKS',
  'FLOAT_GLASS',
  'GREY_CEMENT_CLINKER',
  'HOT_METAL',
  'HYDROGEN',
  'IRON_CASTING',
  'LIME',
  'LONG_FIBRE_KRAFT_PULP',
  'MINERAL_WOOL',
  'NEWSPRINT',
  'NITRIC_ACID',
  'PAVERS',
  'PHENOL_ACETONE',
  'PLASTER',
  'PLASTERBOARD',
  'PRE_BAKE_ANODE',
  'RECOVERED_PAPER_PULP',
  'REFINERY_PRODUCTS',
  'ROOF_TILES',
  'SHORT_FIBRE_KRAFT_PULP',
  'SINTERED_DOLIME',
  'SINTERED_ORE',
  'SODA_ASH',
  'SPRAY_DRIED_POWDER',
  'S_PVC',
  'STEAM_CRACKING',
  'STYRENE',
  'SULPHITE_PULP_THERMO_MECHANICAL_AND_MECHANICAL_PULP',
  'SYNTHESIS_GAS',
  'TESTLINER_AND_FLUTING',
  'TISSUE',
  'UNCOATED_CARTON_BOARD',
  'UNCOATED_FINE_PAPER',
  'VINYL_CHLORIDE_MONOMER',
  'WHITE_CEMENT_CLINKER',
];

export const exchangeabilityTypes = [
  'AMMONIA',
  'AROMATICS',
  'CARBON_BLACK',
  'EAF_CARBON_STEEL',
  'EAF_HIGH_ALLOY_STEEL',
  'ETHYLENE_OXIDE_ETHYLENE_GLYCOLS',
  'HYDROGEN',
  'IRON_CASTING',
  'MINERAL_WOOL',
  'REFINERY_PRODUCTS',
  'STEAM_CRACKING',
  'STYRENE',
  'SYNTHESIS_GAS',
];
