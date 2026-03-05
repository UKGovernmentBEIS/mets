import { Pipe, PipeTransform } from '@angular/core';

import { InstallationConnection } from 'pmrv-api';

@Pipe({ name: 'flowDirectionType' })
export class ConnectionFlowDirectionTypePipe implements PipeTransform {
  transform(value: InstallationConnection['flowDirection']): string {
    switch (value) {
      case 'IMPORT':
        return 'Import: something entering the boundaries of your installation';
      case 'EXPORT':
        return 'Export: something leaving the boundaries of your installation';
      default:
        return '';
    }
  }
}
