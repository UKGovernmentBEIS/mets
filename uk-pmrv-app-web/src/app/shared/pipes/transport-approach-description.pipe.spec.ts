import { TransportApproachDescriptionPipe } from './transport-approach-description.pipe';

describe('TransportApproachDescriptionPipe', () => {
  let pipe: TransportApproachDescriptionPipe;

  beforeEach(async () => {
    pipe = new TransportApproachDescriptionPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('METHOD_A')).toEqual('METHOD A');
  });
});
