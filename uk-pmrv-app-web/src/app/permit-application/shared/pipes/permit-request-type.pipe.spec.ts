import { PermitRequestTypePipe } from './permit-request-type.pipe';

describe('PermitRequestTypePipe', () => {
  let pipe: PermitRequestTypePipe;

  beforeEach(async () => {
    pipe = new PermitRequestTypePipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('PERMIT_ISSUANCE')).toEqual('permit');
    expect(pipe.transform('PERMIT_VARIATION')).toEqual('permit variation');
    expect(pipe.transform('PERMIT_TRANSFER_B')).toEqual('permit transfer');
  });
});
