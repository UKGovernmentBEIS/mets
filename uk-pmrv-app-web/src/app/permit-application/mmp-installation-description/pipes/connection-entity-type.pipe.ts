import { Pipe, PipeTransform } from '@angular/core';

import { InstallationConnection } from 'pmrv-api';

@Pipe({ name: 'entityType' })
export class ConnectionEntityTypePipe implements PipeTransform {
  transform(value: InstallationConnection['entityType']): string {
    switch (value) {
      case 'ETS_INSTALLATION':
        return 'Installation covered by ETS';
      case 'NON_ETS_INSTALLATION':
        return 'Installation outside ETS';
      case 'NITRIC_ACID_PRODUCTION':
        return 'Installation producing nitric acid';
      case 'HEAT_DISTRIBUTION_NETWORK':
        return 'Heat distribution network';
      default:
        return '';
    }
  }
}
