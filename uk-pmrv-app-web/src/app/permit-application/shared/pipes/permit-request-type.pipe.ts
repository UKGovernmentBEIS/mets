import { Pipe, PipeTransform } from '@angular/core';

import { RequestInfoDTO } from 'pmrv-api';

@Pipe({
  name: 'permitRequestType',
})
export class PermitRequestTypePipe implements PipeTransform {
  transform(value: RequestInfoDTO['type']): string {
    switch (value) {
      case 'PERMIT_ISSUANCE':
        return 'permit';
      case 'PERMIT_VARIATION':
        return 'permit variation';
      case 'PERMIT_TRANSFER_B':
        return 'permit transfer';
    }
  }
}
