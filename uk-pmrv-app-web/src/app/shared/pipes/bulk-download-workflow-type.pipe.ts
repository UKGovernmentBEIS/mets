import { Pipe, PipeTransform } from '@angular/core';

import { RequestDetailsDTO } from 'pmrv-api';

@Pipe({ name: 'bulkDownloadWorkflowType' })
export class BulkDownloadWorkflowTypePipe implements PipeTransform {
  transform(type: RequestDetailsDTO['requestType']): string {
    switch (type) {
      case 'ALR':
        return 'Activity Level Report';
      case 'BDR':
        return 'Baseline Data Report';
      case 'WASTE_QDR':
        return 'Waste Voluntary Quarterly report';

      default:
        return null;
    }
  }
}
