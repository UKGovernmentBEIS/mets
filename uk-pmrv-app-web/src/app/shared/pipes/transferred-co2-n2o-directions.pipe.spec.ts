import { TransferredCO2N2ODirectionsPipe } from './transferred-co2-n2o-directions.pipe';

describe('TransferredCO2N2ODirectionsPipe', () => {
  let pipe: TransferredCO2N2ODirectionsPipe;

  beforeEach(async () => {
    pipe = new TransferredCO2N2ODirectionsPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('EXPORTED_TO_LONG_TERM_FACILITY')).toEqual(
      'Exported to a long-term geological storage related facility',
    );
    expect(pipe.transform('EXPORTED_FOR_PRECIPITATED_CALCIUM')).toEqual(
      'Exported out of our installation and used to produce precipitated calcium carbonate, in which the used CO2 is chemically bound',
    );
    expect(pipe.transform('RECEIVED_FROM_ANOTHER_INSTALLATION')).toEqual('Received from another installation');
  });
});
