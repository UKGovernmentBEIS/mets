import { PermitTransferPartyPipe } from './permit-transfer-party.pipe';

describe('PermitTransferPartyPipe', () => {
  let pipe: PermitTransferPartyPipe;

  beforeEach(async () => {
    pipe = new PermitTransferPartyPipe();
  });
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('RECEIVER')).toEqual('Receiver');
    expect(pipe.transform('TRANSFERER')).toEqual('Transferer');
  });
});
