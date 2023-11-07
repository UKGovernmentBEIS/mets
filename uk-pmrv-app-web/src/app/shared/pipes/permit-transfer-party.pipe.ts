import { Pipe, PipeTransform } from '@angular/core';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

@Pipe({
  name: 'transferParty',
})
export class PermitTransferPartyPipe implements PipeTransform {
  transform(
    value:
      | PermitTransferAApplicationRequestTaskPayload['aerLiable']
      | PermitTransferAApplicationRequestTaskPayload['payer'],
  ) {
    switch (value) {
      case 'RECEIVER':
        return 'Receiver';
      case 'TRANSFERER':
        return 'Transferer';
    }
  }
}
