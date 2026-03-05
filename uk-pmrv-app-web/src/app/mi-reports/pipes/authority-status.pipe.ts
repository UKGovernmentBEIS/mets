import { Pipe, PipeTransform } from '@angular/core';

import { UserAuthorityInfoDTO } from 'pmrv-api';

@Pipe({
  name: 'authorityStatus',
})
export class AuthorityStatusPipe implements PipeTransform {
  transform(status?: UserAuthorityInfoDTO['authorityStatus']): string {
    if (!status) {
      return null;
    }

    switch (status) {
      case 'ACCEPTED':
        return 'Accepted';
      case 'ACTIVE':
        return 'Active';
      case 'DISABLED':
        return 'Disabled';
      case 'PENDING':
        return 'Pending';
      case 'TEMP_DISABLED':
        return 'Temporarily Disabled';
      case 'TEMP_DISABLED_PENDING':
        return 'Temporarily Disabled Pending';
      default:
        return null;
    }
  }
}
