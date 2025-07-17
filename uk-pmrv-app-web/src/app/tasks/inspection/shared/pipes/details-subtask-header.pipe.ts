import { Pipe, PipeTransform } from '@angular/core';

import { InspectionService } from '@tasks/inspection/core/inspection.service';

import { RequestTaskDTO } from 'pmrv-api';

@Pipe({
  name: 'detailsSubtaskHeader',
  standalone: true,
})
export class DetailsSubtaskHeaderPipe implements PipeTransform {
  constructor(private readonly inspectionService: InspectionService) {}

  transform(taskType: RequestTaskDTO['type']): string {
    switch (taskType) {
      case 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT':
      case 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW':
      case 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS':
        return 'On-site inspection details';
      case 'INSTALLATION_AUDIT_APPLICATION_SUBMIT':
      case 'INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW':
      case 'INSTALLATION_AUDIT_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS':
        return this.inspectionService.auditYear + ' audit report details';
    }
  }
}
