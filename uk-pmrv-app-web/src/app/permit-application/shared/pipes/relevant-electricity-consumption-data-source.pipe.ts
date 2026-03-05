import { Pipe, PipeTransform } from '@angular/core';

import { FuelAndElectricityExchangeabilityEnergyFlowDataSource } from 'pmrv-api';

@Pipe({
  name: 'relevantElectricityConsumptionDataSource',
})
export class RelevantElectricityConsumptionDataSourcePipe implements PipeTransform {
  transform(value: FuelAndElectricityExchangeabilityEnergyFlowDataSource['relevantElectricityConsumption']): string {
    switch (value) {
      case 'LEGAL_METROLOGICAL_CONTROL_READING':
        return '4.5. (a) Readings of measuring instruments subject to national legal metrological control or measuring instruments compliant with the requirements of the Directive 2014/31/EU or Directive 2014/32/EU';
      case 'OPERATOR_CONTROL_DIRECT_READING_NOT_A':
        return "4.5. (b) Readings of measuring instruments under the operator's control for direct determination of a data set not falling under point a";
      case 'NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A':
        return "4.5. (c) Readings of measuring instruments not under the operator's control for direct determination of a data set not falling under point a";
      case 'INDIRECT_DETERMINATION_READING':
        return '4.5. (d) Readings of measuring instruments for indirect determination of a data set, provided that an appropriate correlation between the measurement and the data set in question is established in line with section 3.4 of Annex VII (FAR)';
      case 'PROXY_CALCULATION_METHOD':
        return '4.5. (e) Calculation of a proxy for the determining net amounts of measurable heat in accordance with Method 3 of section 7.2 in Annex VII (FAR)';
      case 'OTHER_METHODS':
        return '4.5. (f) Other methods, in particular for historical data or where no other data source can be identified by the operator as available';
      default:
        return '';
    }
  }
}
