import { UserRoleAllTypesPipe } from './user-role-all-types.pipe';

describe('UserRoleAllTypesPipe', () => {
  let pipe: UserRoleAllTypesPipe;

  beforeEach(() => (pipe = new UserRoleAllTypesPipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map task types to item names', () => {
    expect(pipe.transform('operator_admin')).toEqual('Operator admin');
    expect(pipe.transform('operator')).toEqual('Operator');
    expect(pipe.transform('consultant_agent')).toEqual('Consultant');
    expect(pipe.transform('emitter_contact')).toEqual('Emitter Contact');
    expect(pipe.transform('verifier_admin')).toEqual('Verifier admin');
  });
});
