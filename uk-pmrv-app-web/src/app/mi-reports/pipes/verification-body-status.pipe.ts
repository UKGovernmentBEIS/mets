import { Pipe, PipeTransform } from '@angular/core';

import { VerificationBodyDTO } from 'pmrv-api';

@Pipe({
  name: 'verificationBodyStatus',
})
export class VerificationBodyStatusPipe implements PipeTransform {
  transform(status?: VerificationBodyDTO['status']): string {
    if (!status) {
      return null;
    }

    switch (status) {
      case 'ACTIVE':
        return 'Active';
      case 'PENDING':
        return 'Pending';
      case 'DISABLED':
        return 'Disabled';
      default:
        return null;
    }
  }
}
