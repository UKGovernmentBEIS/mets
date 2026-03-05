import { Pipe, PipeTransform } from '@angular/core';

import { TransferCO2, TransferN2O } from 'pmrv-api';

@Pipe({
  name: 'transferredCO2N2ODirections',
})
export class TransferredCO2N2ODirectionsPipe implements PipeTransform {
  transform(value: TransferCO2['transferDirection'] | TransferN2O['transferDirection']): string {
    switch (value) {
      case 'EXPORTED_TO_LONG_TERM_FACILITY':
        return 'Exported to a long-term geological storage related facility';
      case 'EXPORTED_FOR_PRECIPITATED_CALCIUM':
        return 'Exported out of our installation and used to produce precipitated calcium carbonate, in which the used CO2 is chemically bound';
      case 'RECEIVED_FROM_ANOTHER_INSTALLATION':
        return 'Received from another installation';
      default:
        return '';
    }
  }
}
