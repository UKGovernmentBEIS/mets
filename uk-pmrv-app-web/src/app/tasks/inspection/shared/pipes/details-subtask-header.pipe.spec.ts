import { InspectionService } from '@tasks/inspection/core/inspection.service';

import { DetailsSubtaskHeaderPipe } from './details-subtask-header.pipe';

describe('DetailsSubtaskHeaderPipe', () => {
  let pipe: DetailsSubtaskHeaderPipe;
  let inspectionService: InspectionService;

  beforeEach(() => {
    inspectionService = { auditYear: '2023' } as unknown as InspectionService;
    pipe = new DetailsSubtaskHeaderPipe(inspectionService);
  });

  it('should return "On-site inspection details" for INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT', () => {
    const result = pipe.transform('INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT');
    expect(result).toBe('On-site inspection details');
  });

  it('should return "On-site inspection details" for INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW', () => {
    const result = pipe.transform('INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW');
    expect(result).toBe('On-site inspection details');
  });

  it('should return "On-site inspection details" for INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS', () => {
    const result = pipe.transform('INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS');
    expect(result).toBe('On-site inspection details');
  });

  it('should return "2023 audit report details" for INSTALLATION_AUDIT_APPLICATION_SUBMIT', () => {
    const result = pipe.transform('INSTALLATION_AUDIT_APPLICATION_SUBMIT');
    expect(result).toBe('2023 audit report details');
  });

  it('should return "2023 audit report details" for INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW', () => {
    const result = pipe.transform('INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW');
    expect(result).toBe('2023 audit report details');
  });

  it('should return "2023 audit report details" for INSTALLATION_AUDIT_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS', () => {
    const result = pipe.transform('INSTALLATION_AUDIT_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS');
    expect(result).toBe('2023 audit report details');
  });
});
