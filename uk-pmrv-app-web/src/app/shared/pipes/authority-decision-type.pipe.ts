import { Pipe, PipeTransform } from '@angular/core';

import { AuthorityResponse, DoalAuthorityResponse } from 'pmrv-api';

@Pipe({ name: 'authorityDecisionType' })
export class AuthorityDecisionTypePipe implements PipeTransform {
  transform(type: DoalAuthorityResponse['type'] | AuthorityResponse['type']): string {
    switch (type) {
      case 'VALID':
        return 'Approved';
      case 'VALID_WITH_CORRECTIONS':
        return 'Approved with corrections';
      case 'INVALID':
        return 'Not approved';
      default:
        return null;
    }
  }
}
