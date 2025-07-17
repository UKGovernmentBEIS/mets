import { InspectionService } from '@tasks/inspection/core/inspection.service';

import { RequestTaskDTO } from 'pmrv-api';

import { DetailsSubtaskLinktextPipe } from './details-subtask-linktext.pipe';

describe('DetailsSubtaskLinktextPipe', () => {
  let pipe: DetailsSubtaskLinktextPipe;
  let inspectionService: InspectionService;

  beforeEach(() => {
    inspectionService = { auditYear: '2023' } as unknown as InspectionService;
    pipe = new DetailsSubtaskLinktextPipe(inspectionService);
  });

  it('should return "Add on-site inspection details" for INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT', () => {
    const result = pipe.transform('INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT' as RequestTaskDTO['type']);
    expect(result).toBe('Add on-site inspection details');
  });

  it('should return "View on-site inspection details" for INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW', () => {
    const result = pipe.transform('INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW' as RequestTaskDTO['type']);
    expect(result).toBe('View on-site inspection details');
  });

  it('should return "Add 2023 audit report details" for INSTALLATION_AUDIT_APPLICATION_SUBMIT', () => {
    const result = pipe.transform('INSTALLATION_AUDIT_APPLICATION_SUBMIT' as RequestTaskDTO['type']);
    expect(result).toBe('Add 2023 audit report details');
  });

  it('should return "View 2023 audit report details" for INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW', () => {
    const result = pipe.transform('INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW' as RequestTaskDTO['type']);
    expect(result).toBe('View 2023 audit report details');
  });
});
