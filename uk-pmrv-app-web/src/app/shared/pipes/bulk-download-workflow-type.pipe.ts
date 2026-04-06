import { Pipe, PipeTransform } from '@angular/core';

import { RequestDetailsDTO } from 'pmrv-api';

@Pipe({ name: 'bulkDownloadWorkflowType', standalone: false })
export class BulkDownloadWorkflowTypePipe implements PipeTransform {
  transform(type: RequestDetailsDTO['requestType']): string {
    switch (type) {
      case 'ALR':
        return 'Activity Level Report';
      case 'BDRS2':
        return 'Stage 2 baseline data report';
      case 'WASTE_QDR':
        return 'Waste Voluntary Quarterly report';

      default:
        return null;
    }
  }
}
