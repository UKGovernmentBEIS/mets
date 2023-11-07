import { FuelUpliftSupplierRecordTypePipe } from '@aviation/shared/pipes/fuel-uplift-records-type.pipe';

describe('FuelUpliftSupplierRecordTypePipe', () => {
  let pipe: FuelUpliftSupplierRecordTypePipe;

  beforeEach(() => (pipe = new FuelUpliftSupplierRecordTypePipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform fuel uplift supplier record types', () => {
    expect(pipe.transform('FUEL_DELIVERY_NOTES')).toEqual('Fuel delivery notes');
    expect(pipe.transform('FUEL_INVOICES')).toEqual('Fuel invoices');
    expect(pipe.transform(null)).toEqual(null);
    expect(pipe.transform(undefined)).toEqual(null);
  });
});
