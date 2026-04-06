import { BulkDownloadWorkflowTypePipe } from './bulk-download-workflow-type.pipe';

describe('BulkDownloadWorkflowTypePipe', () => {
  let pipe: BulkDownloadWorkflowTypePipe;

  beforeEach(() => {
    pipe = new BulkDownloadWorkflowTypePipe();
  });

  it('should return "Activity Level Report" for ALR', () => {
    expect(pipe.transform('ALR')).toBe('Activity Level Report');
  });

  it('should return "Stage 2 baseline data report" for BDR', () => {
    expect(pipe.transform('BDRS2')).toBe('Stage 2 baseline data report');
  });

  it('should return "Waste Voluntary Quarterly report" for WASTE_QDR', () => {
    expect(pipe.transform('WASTE_QDR')).toBe('Waste Voluntary Quarterly report');
  });

  it('should return null for unsupported request types', () => {
    expect(pipe.transform('UNKNOWN' as any)).toBeNull();
  });

  it('should return null when type is null', () => {
    expect(pipe.transform(null as any)).toBeNull();
  });

  it('should return null when type is undefined', () => {
    expect(pipe.transform(undefined as any)).toBeNull();
  });
});
