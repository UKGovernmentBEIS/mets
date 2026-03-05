import { Pipe, PipeTransform } from '@angular/core';

import { SubInstallation } from 'pmrv-api';

@Pipe({
  name: 'carbonLeakage',
})
export class CarbonLeakagePipe implements PipeTransform {
  transform(value: SubInstallation['subInstallationType']): string {
    switch (value) {
      case 'PRIMARY_ALUMINIUM':
      case 'ADIPIC_ACID':
      case 'AMMONIA':
      case 'AROMATICS':
      case 'BOTTLES_AND_JARS_OF_COLOURED_GLASS':
      case 'BOTTLES_AND_JARS_OF_COLOURLESS_GLASS':
      case 'CARBON_BLACK':
      case 'COATED_CARTON_BOARD':
      case 'COATED_FINE_PAPER':
      case 'COKE':
      case 'CONTINUOUS_FILAMENT_GLASS_FIBRE_PRODUCTS':
      case 'DOLIME':
      case 'DRIED_SECONDARY_GYPSUM':
      case 'EAF_CARBON_STEEL':
      case 'EAF_HIGH_ALLOY_STEEL':
      case 'E_PVC':
      case 'ETHYLENE_OXIDE_ETHYLENE_GLYCOLS':
      case 'FACING_BRICKS':
      case 'FLOAT_GLASS':
      case 'GREY_CEMENT_CLINKER':
      case 'HOT_METAL':
      case 'HYDROGEN':
      case 'IRON_CASTING':
      case 'LIME':
      case 'LONG_FIBRE_KRAFT_PULP':
      case 'MINERAL_WOOL':
      case 'NEWSPRINT':
      case 'NITRIC_ACID':
      case 'PAVERS':
      case 'PHENOL_ACETONE':
      case 'PLASTER':
      case 'PRE_BAKE_ANODE':
      case 'RECOVERED_PAPER_PULP':
      case 'REFINERY_PRODUCTS':
      case 'ROOF_TILES':
      case 'SHORT_FIBRE_KRAFT_PULP':
      case 'SINTERED_DOLIME':
      case 'SINTERED_ORE':
      case 'SODA_ASH':
      case 'SPRAY_DRIED_POWDER':
      case 'S_PVC':
      case 'STEAM_CRACKING':
      case 'STYRENE':
      case 'SULPHITE_PULP_THERMO_MECHANICAL_AND_MECHANICAL_PULP':
      case 'SYNTHESIS_GAS':
      case 'TESTLINER_AND_FLUTING':
      case 'TISSUE':
      case 'UNCOATED_CARTON_BOARD':
      case 'UNCOATED_FINE_PAPER':
      case 'VINYL_CHLORIDE_MONOMER':
      case 'WHITE_CEMENT_CLINKER':
      case 'HEAT_BENCHMARK_CL':
      case 'FUEL_BENCHMARK_CL':
      case 'PROCESS_EMISSIONS_CL':
        return 'Exposed';

      case 'PLASTERBOARD':
      case 'HEAT_BENCHMARK_NON_CL':
      case 'DISTRICT_HEATING_NON_CL':
      case 'FUEL_BENCHMARK_NON_CL':
      case 'PROCESS_EMISSIONS_NON_CL':
        return 'Not exposed';

      default:
        return '';
    }
  }
}
