import { AccountReportingStatusPipe } from './account-reporting-status.pipe';

describe('AccountReportingStatusPipe', () => {
  it('create an instance', () => {
    const pipe = new AccountReportingStatusPipe();
    expect(pipe).toBeTruthy();
  });

  it('should return correct value for valid reporting status', () => {
    const pipe = new AccountReportingStatusPipe();
    expect(pipe.transform('EXEMPT_COMMERCIAL')).toEqual('Exempt (commercial)');
    expect(pipe.transform('EXEMPT_NON_COMMERCIAL')).toEqual('Exempt (non commercial)');
    expect(pipe.transform('REQUIRED_TO_REPORT')).toEqual('Required to report');
  });

  it('should return null value when invalid reporting status', () => {
    const pipe = new AccountReportingStatusPipe();
    expect(pipe.transform('INVALID' as any)).toEqual(null);
  });
});
