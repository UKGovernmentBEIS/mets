import { OperatorDetailsLegalStatusTypePipe } from '@aviation/shared/pipes/operator-details-legal-status-type.pipe';

describe('OperatorDetailsLegalStatusTypePipe', () => {
  let pipe: OperatorDetailsLegalStatusTypePipe;

  beforeEach(() => (pipe = new OperatorDetailsLegalStatusTypePipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform legal types', () => {
    expect(pipe.transform('LIMITED_COMPANY')).toEqual('Limited company');
    expect(pipe.transform('INDIVIDUAL')).toEqual('Individual');
    expect(pipe.transform('PARTNERSHIP')).toEqual('Partnership');
    expect(pipe.transform(null)).toEqual(null);
    expect(pipe.transform(undefined)).toEqual(null);
  });
});
