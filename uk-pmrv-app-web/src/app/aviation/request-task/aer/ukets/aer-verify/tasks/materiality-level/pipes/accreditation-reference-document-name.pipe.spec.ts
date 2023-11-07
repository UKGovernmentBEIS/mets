import { AccreditationReferenceDocumentNamePipe } from './accreditation-reference-document-name.pipe';

describe('AccreditationReferenceDocumentNamePipe', () => {
  const pipe = new AccreditationReferenceDocumentNamePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('UK_ETS_ACCREDITED_VERIFIERS_SI_2020_1265')).toEqual(
      'The Greenhouse Gas Emissions Trading Scheme Order 2020 (SI 2020/1265) as amended by The Greenhouse Gas Emissions Trading Scheme (Amendment) Order 2020 (SI 2020/1557) (the UK ETS Order)',
    );
    
    expect(pipe.transform('OTHER')).toEqual('Add your own reference');

    expect(pipe.transform(undefined)).toEqual('');
  });
});
