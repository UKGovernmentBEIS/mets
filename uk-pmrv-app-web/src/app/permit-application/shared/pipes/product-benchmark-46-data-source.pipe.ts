import { Pipe, PipeTransform } from '@angular/core';

import { ImportedExportedAmountsDataSource } from 'pmrv-api';

@Pipe({
  name: 'productBenchmark46DataSource',
})
export class ProductBenchmark46DataSourcePipe implements PipeTransform {
  transform(value: ImportedExportedAmountsDataSource['energyContent']): string {
    switch (value) {
      case 'CALCULATION_METHOD_MONITORING_PLAN':
        return '4.6.(a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012';
      case 'LABORATORY_ANALYSES_SECTION_61':
        return '4.6.(b) Laboratory analyses in accordance with section 6.1 of  Annex VII (FAR)';
      case 'SIMPLIFIED_LABORATORY_ANALYSES_SECTION_62':
        return '4.6.(c) Simplified laboratory analyses in accordance with section 6.2 of Annex VII (FAR)';
      case 'CONSTANT_VALUES_STANDARD_SUPPLIER':
        return '4.6.(d) Constant values based on one of the following data sources: standard factors, literature values, values specified and guaranteed by the supplier';
      case 'CONSTANT_VALUES_SCIENTIFIC_EVIDENCE':
        return '4.6.(e) Constant values based on one of the following data sources: standard/stoichiometric factors, analysis-based values, other values based on scientific evidence';
      default:
        return '';
    }
  }
}
