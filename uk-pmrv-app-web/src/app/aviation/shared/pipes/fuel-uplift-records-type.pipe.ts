import { Pipe, PipeTransform } from '@angular/core';

import { EmpFuelUpliftMethodProcedures } from 'pmrv-api';

const FUEL_UPLIFT_SUPPLIER_RECORDS_SELECTION = {
  FUEL_DELIVERY_NOTES: 'Fuel delivery notes',
  FUEL_INVOICES: 'Fuel invoices',
};

@Pipe({
  name: 'fuelUpliftSupplierRecordType',
  pure: true,
  standalone: true,
})
export class FuelUpliftSupplierRecordTypePipe implements PipeTransform {
  transform(value: EmpFuelUpliftMethodProcedures['fuelUpliftSupplierRecordType']): string | null {
    if (value == null) {
      return null;
    }

    return FUEL_UPLIFT_SUPPLIER_RECORDS_SELECTION[value] ?? null;
  }
}
