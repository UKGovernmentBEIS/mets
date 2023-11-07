import { Pipe, PipeTransform } from '@angular/core';

import { InherentReceivingTransferringInstallation } from 'pmrv-api';

@Pipe({
  name: 'inherentCo2Instruments',
})
export class InherentCo2InstrumentsPipe implements PipeTransform {
  transform(value: InherentReceivingTransferringInstallation['measurementInstrumentOwnerTypes'][number]): string {
    switch (value) {
      case 'INSTRUMENTS_BELONGING_TO_YOUR_INSTALLATION':
        return 'Instruments belonging to your installation';
      case 'INSTRUMENTS_BELONGING_TO_THE_OTHER_INSTALLATION':
        return 'Instruments belonging to the other installation';
      default:
        return '';
    }
  }
}
