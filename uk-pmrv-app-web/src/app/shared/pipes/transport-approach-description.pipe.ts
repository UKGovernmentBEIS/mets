import { Pipe, PipeTransform } from '@angular/core';

import { TransferredCO2AndN2OMonitoringApproach } from 'pmrv-api';

@Pipe({
  name: 'transportApproachDescription',
})
export class TransportApproachDescriptionPipe implements PipeTransform {
  transform(value: TransferredCO2AndN2OMonitoringApproach['monitoringTransportNetworkApproach']): string {
    return value ? value.replace(/_/g, ' ') : value;
  }
}
