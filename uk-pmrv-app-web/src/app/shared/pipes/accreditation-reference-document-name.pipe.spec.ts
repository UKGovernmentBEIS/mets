import { AccreditationReferenceDocumentNamePipe } from '@shared/pipes/accreditation-reference-document-name.pipe';

describe('AccreditationReferenceDocumentNamePipe', () => {
  let pipe: AccreditationReferenceDocumentNamePipe;

  beforeEach(async () => {
    pipe = new AccreditationReferenceDocumentNamePipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('UK_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_SI_2020_1265')).toEqual(
      'The Greenhouse Gas Emissions Trading Scheme Order 2020 (SI 2020/1265) as amended by The Greenhouse Gas Emissions Trading Scheme (Amendment) Order 2020 (SI 2020/1557) (the UK ETS Order)',
    );
    expect(pipe.transform('OTHER')).toEqual('Add your own reference');

    expect(pipe.transform(undefined)).toEqual('');
  });
});
