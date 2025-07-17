import { Pipe, PipeTransform } from '@angular/core';

import { InstallationConnection } from 'pmrv-api';

@Pipe({ name: 'connectionType' })
export class ConnectionTypePipe implements PipeTransform {
  transform(value: InstallationConnection['connectionType']): string {
    switch (value) {
      case 'MEASURABLE_HEAT':
        return 'Measurable heat';
      case 'WASTE_GAS':
        return 'Waste gas';
      case 'TRANSFERRED_CO2_FOR_USE_IN_YOUR_INSTALLATION_CCU':
        return 'Transferred CO2 for use in your installation (CCU)';
      case 'TRANSFERRED_CO2_FOR_STORAGE':
        return 'Transferred CO2 for geological storage (CCS)';
      case 'INTERMEDIATE_PRODUCTS_COVERED_BY_PRODUCT_BENCHMARKS':
        return 'Intermediate products covered by product benchmarks';
      default:
        return '';
    }
  }
}
