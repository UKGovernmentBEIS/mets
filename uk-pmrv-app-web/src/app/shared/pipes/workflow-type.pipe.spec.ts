import { WorkflowTypePipe } from './workflow-type.pipe';

describe('WorkflowTypePipe', () => {
  let pipe: WorkflowTypePipe;

  beforeEach(() => (pipe = new WorkflowTypePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform workflow types', () => {
    // ######################## COMMON ########################
    expect(pipe.transform('SYSTEM_MESSAGE_NOTIFICATION')).toEqual('System Message Notification');

    // ######################## INSTALLATION ########################
    expect(pipe.transform('INSTALLATION_ACCOUNT_OPENING')).toEqual('Account creation');

    expect(pipe.transform('PERMIT_ISSUANCE')).toEqual('Permit Application');
    expect(pipe.transform('PERMIT_SURRENDER')).toEqual('Permit Surrender');
    expect(pipe.transform('PERMIT_REVOCATION')).toEqual('Permit Revocation');
    expect(pipe.transform('PERMIT_TRANSFER_A')).toEqual('Permit Transfer');
    expect(pipe.transform('PERMIT_TRANSFER_B')).toEqual('Permit Transfer');
    expect(pipe.transform('PERMIT_NOTIFICATION')).toEqual('Permit Notification');
    expect(pipe.transform('PERMIT_VARIATION')).toEqual('Permit Variation');
    expect(pipe.transform('PERMIT_REISSUE')).toEqual('Batch variation');

    expect(pipe.transform('NON_COMPLIANCE')).toEqual('Non-compliance');

    expect(pipe.transform('NER')).toEqual('New Entrant Reserve');
    expect(pipe.transform('DOAL')).toEqual('Determination of activity level');
    expect(pipe.transform('AER')).toEqual('Emissions report');
    expect(pipe.transform('VIR')).toEqual('Verifier improvement');
    expect(pipe.transform('AIR')).toEqual('Annual improvement');
    expect(pipe.transform('DRE')).toEqual('Determine emissions');

    expect(pipe.transform('WITHHOLDING_OF_ALLOWANCES')).toEqual('Withholding of allowances');
    expect(pipe.transform('RETURN_OF_ALLOWANCES')).toEqual('Return of allowances');

    // ######################## AVIATION ########################
    expect(pipe.transform('AVIATION_ACCOUNT_CLOSURE')).toEqual('Account closure');

    expect(pipe.transform('AVIATION_NON_COMPLIANCE')).toEqual('Non-compliance');

    expect(pipe.transform('EMP_REISSUE')).toEqual('Batch variation');
    expect(pipe.transform('EMP_ISSUANCE_CORSIA')).toEqual('EMP Application');
    expect(pipe.transform('EMP_ISSUANCE_UKETS')).toEqual('EMP Application');
    expect(pipe.transform('EMP_VARIATION_CORSIA')).toEqual('Variation');
    expect(pipe.transform('EMP_VARIATION_UKETS')).toEqual('Variation');

    expect(pipe.transform('AVIATION_AER_CORSIA')).toEqual('Emissions report');
    expect(pipe.transform('AVIATION_AER_UKETS')).toEqual('Emissions report');
    expect(pipe.transform('AVIATION_DRE_UKETS')).toEqual('Determine emissions');
    expect(pipe.transform('AVIATION_VIR')).toEqual('Verifier improvement');

    expect(pipe.transform(undefined)).toEqual(null);
  });
});
