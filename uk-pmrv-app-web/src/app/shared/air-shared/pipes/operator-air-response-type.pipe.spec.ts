import { OperatorAirImprovementResponse } from 'pmrv-api';

import { OperatorAirResponseTypePipe } from './operator-air-response-type.pipe';

describe('OperatorAirResponseTypePipe', () => {
  let pipe: OperatorAirResponseTypePipe;

  beforeAll(async () => {
    pipe = new OperatorAirResponseTypePipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('YES')).toEqual('Yes');
    expect(pipe.transform('NO')).toEqual('No');
    expect(pipe.transform('ALREADY_MADE')).toEqual('Improvement already made');
    expect(pipe.transform('RANDOM' as OperatorAirImprovementResponse['type'])).toEqual('');
  });
});
