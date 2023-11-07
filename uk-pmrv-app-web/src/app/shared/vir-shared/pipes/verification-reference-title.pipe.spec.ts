import { VerificationReferenceTitlePipe } from './verification-reference-title.pipe';

describe('VerificationReferenceTitlePipe', () => {
  const pipe = new VerificationReferenceTitlePipe();

  it('create an instance', () => {
    const pipe = new VerificationReferenceTitlePipe();
    expect(pipe).toBeTruthy();
  });

  it('should properly transform text based on their reference', () => {
    let transformation = pipe.transform('F1');
    expect(transformation).toEqual('');

    transformation = pipe.transform('A1');
    expect(transformation).toEqual('A1: an uncorrected error that remained before the verification report was issued');

    transformation = pipe.transform('B1');
    expect(transformation).toEqual('B1: an uncorrected error in the monitoring plan');

    transformation = pipe.transform('C1');
    expect(transformation).toEqual('C1: an uncorrected breach of the MRR, identified during verification');

    transformation = pipe.transform('D1');
    expect(transformation).toEqual('D1: recommended improvement');

    transformation = pipe.transform('E1');
    expect(transformation).toEqual('E1: an unresolved breach from a previous year');
  });
});
