import { Pipe, PipeTransform } from '@angular/core';

import { SubInstallation } from 'pmrv-api';

@Pipe({
  name: 'subInstallationType',
})
export class SubInstallationTypePipe implements PipeTransform {
  transform(value: SubInstallation['subInstallationType']): string {
    switch (value) {
      case 'PRIMARY_ALUMINIUM':
        return '[Primary] Aluminium';
      case 'ADIPIC_ACID':
        return 'Adipic acid';
      case 'AMMONIA':
        return 'Ammonia';
      case 'AROMATICS':
        return 'Aromatics';
      case 'BOTTLES_AND_JARS_OF_COLOURED_GLASS':
        return 'Bottles and jars of coloured glass';
      case 'BOTTLES_AND_JARS_OF_COLOURLESS_GLASS':
        return 'Bottles and jars of colourless glass';
      case 'CARBON_BLACK':
        return 'Carbon black';
      case 'COATED_CARTON_BOARD':
        return 'Coated carton board';
      case 'COATED_FINE_PAPER':
        return 'Coated fine paper';
      case 'COKE':
        return 'Coke';
      case 'CONTINUOUS_FILAMENT_GLASS_FIBRE_PRODUCTS':
        return 'Continuous filament glass fibre products';
      case 'DOLIME':
        return 'Dolime';
      case 'DRIED_SECONDARY_GYPSUM':
        return 'Dried secondary gypsum';
      case 'EAF_CARBON_STEEL':
        return 'EAF carbon steel';
      case 'EAF_HIGH_ALLOY_STEEL':
        return 'EAF high alloy steel';
      case 'E_PVC':
        return 'E-PVC';
      case 'ETHYLENE_OXIDE_ETHYLENE_GLYCOLS':
        return 'Ethylene oxide/ ethylene glycols';
      case 'FACING_BRICKS':
        return 'Facing bricks';
      case 'FLOAT_GLASS':
        return 'Float glass';
      case 'GREY_CEMENT_CLINKER':
        return 'Grey cement clinker';
      case 'HOT_METAL':
        return 'Hot metal';
      case 'HYDROGEN':
        return 'Hydrogen';
      case 'IRON_CASTING':
        return 'Iron casting';
      case 'LIME':
        return 'Lime';
      case 'LONG_FIBRE_KRAFT_PULP':
        return 'Long fibre kraft pulp';
      case 'MINERAL_WOOL':
        return 'Mineral wool';
      case 'NEWSPRINT':
        return 'Newsprint';
      case 'NITRIC_ACID':
        return 'Nitric acid';
      case 'PAVERS':
        return 'Pavers';
      case 'PHENOL_ACETONE':
        return 'Phenol/ acetone';
      case 'PLASTER':
        return 'Plaster';
      case 'PLASTERBOARD':
        return 'Plasterboard';
      case 'PRE_BAKE_ANODE':
        return 'Pre-bake anode';
      case 'RECOVERED_PAPER_PULP':
        return 'Recovered paper pulp';
      case 'REFINERY_PRODUCTS':
        return 'Refinery products';
      case 'ROOF_TILES':
        return 'Roof tiles';
      case 'SHORT_FIBRE_KRAFT_PULP':
        return 'Short fibre kraft pulp';
      case 'SINTERED_DOLIME':
        return 'Sintered dolime';
      case 'SINTERED_ORE':
        return 'Sintered ore';
      case 'SODA_ASH':
        return 'Soda ash';
      case 'SPRAY_DRIED_POWDER':
        return 'Spray dried powder';
      case 'S_PVC':
        return 'S-PVC';
      case 'STEAM_CRACKING':
        return 'Steam cracking';
      case 'STYRENE':
        return 'Styrene';
      case 'SULPHITE_PULP_THERMO_MECHANICAL_AND_MECHANICAL_PULP':
        return 'Sulphite pulp, thermo-mechanical and mechanical pulp';
      case 'SYNTHESIS_GAS':
        return 'Synthesis gas';
      case 'TESTLINER_AND_FLUTING':
        return 'Testliner and fluting';
      case 'TISSUE':
        return 'Tissue';
      case 'UNCOATED_CARTON_BOARD':
        return 'Uncoated carton board';
      case 'UNCOATED_FINE_PAPER':
        return 'Uncoated fine paper';
      case 'VINYL_CHLORIDE_MONOMER':
        return 'Vinyl chloride monomer';
      case 'WHITE_CEMENT_CLINKER':
        return 'White cement clinker';
      case 'HEAT_BENCHMARK_CL':
        return 'Heat benchmark exposed to carbon leakage';
      case 'HEAT_BENCHMARK_NON_CL':
        return 'Heat benchmark not exposed to carbon leakage';
      case 'DISTRICT_HEATING_NON_CL':
        return 'District Heating';
      case 'FUEL_BENCHMARK_CL':
        return 'Fuel benchmark exposed to carbon leakage';
      case 'FUEL_BENCHMARK_NON_CL':
        return 'Fuel benchmark not exposed to carbon leakage';
      case 'PROCESS_EMISSIONS_CL':
        return 'Process emissions exposed to carbon leakage';
      case 'PROCESS_EMISSIONS_NON_CL':
        return 'Process emissions not exposed to carbon leakage';
      default:
        return '';
    }
  }
}
