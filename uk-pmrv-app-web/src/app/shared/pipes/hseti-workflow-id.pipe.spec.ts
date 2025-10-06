import { HsetiWorkFlowIdPipe } from './hseti-workflow-id.pipe';

describe('HsetiWorkFlowIdPipe', () => {
  const pipe = new HsetiWorkFlowIdPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform hseti workflow ids with two length years', () => {
    expect(pipe.transform('HSETI00004-26_30-1')).toEqual('HSETI00004-2026_2030-1');
  });

  it('should transform hseti workflow ids with four length years', () => {
    expect(pipe.transform('HSETI00004-2026_2030-1')).toEqual('HSETI00004-2026_2030-1');
  });

  it('should not transform other workflow ids', () => {
    expect(pipe.transform('AEM00004')).toEqual('AEM00004');
    expect(pipe.transform('AEMN00004-1')).toEqual('AEMN00004-1');
  });
});
