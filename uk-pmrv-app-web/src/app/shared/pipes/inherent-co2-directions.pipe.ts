import { Pipe, PipeTransform } from '@angular/core';

import { InherentReceivingTransferringInstallation } from 'pmrv-api';

@Pipe({
  name: 'inherentCo2Directions',
})
export class InherentCo2DirectionsPipe implements PipeTransform {
  transform(value: InherentReceivingTransferringInstallation['inherentCO2Direction']): string {
    switch (value) {
      case 'EXPORTED_TO_ETS_INSTALLATION':
        return 'Exported to an ETS installation';
      case 'EXPORTED_TO_NON_ETS_CONSUMER':
        return 'Exported to a non-ETS consumer';
      case 'RECEIVED_FROM_ANOTHER_INSTALLATION':
        return 'Received from another installation';
      default:
        return '';
    }
  }
}
