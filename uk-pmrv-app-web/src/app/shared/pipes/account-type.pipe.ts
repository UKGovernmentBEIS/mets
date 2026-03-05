import { Pipe, PipeTransform } from '@angular/core';

import { InstallationAccountDTO } from 'pmrv-api';

@Pipe({ name: 'accountType' })
export class AccountTypePipe implements PipeTransform {
  transform(type: InstallationAccountDTO['accountType']): string {
    switch (type) {
      case 'INSTALLATION':
        return 'Installation';
      case 'AVIATION':
        return 'Aviation';
      default:
        return null;
    }
  }
}
