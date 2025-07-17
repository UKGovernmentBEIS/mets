import { Pipe, PipeTransform } from '@angular/core';

import { InspectionService } from '@tasks/inspection/core/inspection.service';

import { RequestTaskDTO } from 'pmrv-api';

@Pipe({
  name: 'detailsSubtaskLinktext',
  standalone: true,
})
export class DetailsSubtaskLinktextPipe implements PipeTransform {
  constructor(private readonly inspectionService: InspectionService) {}

  transform(taskType: RequestTaskDTO['type']): string {
    switch (taskType) {
      case 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT':
        return 'Add on-site inspection details';
      case 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS':
      case 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW':
        return 'View on-site inspection details';

      case 'INSTALLATION_AUDIT_APPLICATION_SUBMIT':
        return 'Add ' + this.inspectionService.auditYear + ' audit report details';
      case 'INSTALLATION_AUDIT_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS':
      case 'INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW':
        return 'View ' + this.inspectionService.auditYear + ' audit report details';
    }
  }
}
