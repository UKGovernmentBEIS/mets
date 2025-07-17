import { NotFollowingHierarchicalOrderReasonPipe } from './not-following-hierarchical-order-reason.pipe';

describe('NotFollowingHierarchicalOrderReasonPipe', () => {
  const pipe = new NotFollowingHierarchicalOrderReasonPipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('OTHER_DATA_SOURCES_LEAD_TO_LOWER_UNCERTAINTY')).toEqual(
      'Other data sources lead to lower uncertainty according to the simplified uncertainty assessment in line with Article 7(2) of the FAR',
    );
    expect(pipe.transform('USE_OF_BETTER_DATA_SOURCES_TECHNICALLY_INFEASIBLE')).toEqual(
      'Use of better data sources is technically infeasible',
    );
    expect(pipe.transform('USE_OF_BETTER_DATA_SOURCES_UNREASONABLE_COSTS')).toEqual(
      'Use of better data sources would incur unreasonable costs',
    );
  });

  it('should handle empty value', () => {
    const transformation = pipe.transform(null);

    expect(transformation).toEqual('');
  });

  it('should handle undefined value', () => {
    const transformation = pipe.transform(undefined);

    expect(transformation).toEqual('');
  });
});
