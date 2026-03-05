import { Pipe, PipeTransform } from '@angular/core';

import { AccountStatus } from '@shared/accounts';

@Pipe({ name: 'accountStatus' })
export class AccountStatusPipe implements PipeTransform {
  transform(status?: AccountStatus): string {
    if (!status) {
      return null;
    }

    switch (status) {
      case 'AWAITING_REVOCATION':
        return 'Awaiting Revocation';
      case 'AWAITING_SURRENDER':
        return 'Awaiting surrender';
      case 'AWAITING_TRANSFER':
        return 'Awaiting transfer';
      case 'DEEMED_WITHDRAWN':
        return 'Deemed Withdrawn';
      case 'DENIED':
        return 'Denied';
      case 'LIVE':
        return 'Live';
      case 'NEW':
        return 'New';
      case 'PERMIT_REFUSED':
        return 'Permit refused';
      case 'REVOKED':
        return 'Revoked';
      case 'SURRENDERED':
        return 'Surrendered';
      case 'TRANSFERRED':
        return 'Transferred';
      case 'UNAPPROVED':
        return 'Unapproved';
      case 'CLOSED':
        return 'Closed';

      default:
        return null;
    }
  }
}
