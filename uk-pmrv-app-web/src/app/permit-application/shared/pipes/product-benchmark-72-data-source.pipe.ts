import { Pipe, PipeTransform } from '@angular/core';

import { ImportedExportedMeasurableHeatEnergyFlowDataSource } from 'pmrv-api';

@Pipe({
  name: 'productBenchmark72DataSource',
})
export class ProductBenchmark72DataSourcePipe implements PipeTransform {
  transform(value: ImportedExportedMeasurableHeatEnergyFlowDataSource['netMeasurableHeatFlows']): string {
    switch (value) {
      case 'MEASUREMENTS':
        return '7.2. Method 1: Using measurements';
      case 'DOCUMENTATION':
        return '7.2. Method 2: Using documentation';
      case 'PROXY_MEASURED_EFFICIENCY':
        return '7.2. Method 3: Calculation of a proxy based on measured efficiency';
      case 'PROXY_REFERENCE_EFFICIENCY':
        return '7.2. Method 4: Calculating a proxy based on the reference efficiency';
      default:
        return '';
    }
  }
}
