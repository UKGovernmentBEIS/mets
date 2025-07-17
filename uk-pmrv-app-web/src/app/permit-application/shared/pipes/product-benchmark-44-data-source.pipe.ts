import { Pipe, PipeTransform } from '@angular/core';

import { QuantityProductDataSource } from 'pmrv-api';

@Pipe({
  name: 'productBenchmark44DataSource',
})
export class ProductBenchmark44DataSourcePipe implements PipeTransform {
  transform(value: QuantityProductDataSource['quantityProduct']): string {
    switch (value) {
      case 'METHOD_MONITORING_PLAN':
        return '4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012';
      case 'LEGAL_METROLOGICAL_CONTROL':
        return '4.4.(b) Readings of measuring instruments subject to national legal metrological control or measuring instruments compliant with the requirements of the Directive 2014/31/EU or Directive 2014/32/EU for direct determination of a data set';
      case 'OPERATOR_CONTROL_NOT_POINT_B':
        return '4.4.(c) Readings of measuring instruments under the operators control for direct determination of a data set not falling under point b';
      case 'NOT_OPERATOR_CONTROL_NOT_POINT_B':
        return '4.4.(d) Readings of measuring instruments not under the operators control for direct determination of a data set not falling under point b';
      case 'INDIRECT_DETERMINATION':
        return '4.4.(e) Readings of measuring instruments for indirect determination of a data set, provided that an appropriate correlation between the measurement and the data set in question is established in line with section 3.4 of this Annex';
      case 'OTHER_METHODS':
        return '4.4.(f) Other methods, in particular for historical data or where no other data source can be identified by the operator as available';
      default:
        return '';
    }
  }
}
