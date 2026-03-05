import { Pipe, PipeTransform } from '@angular/core';

/**
 * Maps measurable heat event constants to user-friendly labels.
 * Usage: {{ eventKey | measurableHeatLabel:'imported' }}
 *        {{ eventKey | measurableHeatLabel:'net' }}
 */
@Pipe({
  name: 'measurableHeatLabel',
})
export class MeasurableHeatLabelPipe implements PipeTransform {
  transform(value: string, which: 'imported' | 'net'): string {
    switch (value) {
      case 'MEASURABLE_HEAT_IMPORTED_OTHER_SOURCES':
        return which === 'imported'
          ? 'Imported from other sources'
          : 'Net measurable heat flows imported from other sources';

      case 'MEASURABLE_HEAT_IMPORTED_PRODUCT_BENCHMARK':
        return which === 'imported'
          ? 'Imported from product benchmark'
          : 'Net measurable heat flows imported from product benchmark';

      case 'MEASURABLE_HEAT_IMPORTED_PULP':
        return which === 'imported' ? 'Imported from pulp' : 'Net measurable heat flows imported from pulp';

      case 'MEASURABLE_HEAT_IMPORTED_FUEL_BENCHMARK':
        return which === 'imported'
          ? 'Imported from fuel benchmark'
          : 'Net measurable heat flows imported from fuel benchmark';

      case 'MEASURABLE_HEAT_IMPORTED_WASTE_GAS':
        return which === 'imported'
          ? 'Imported from waste gases'
          : 'Net measurable heat flows imported from waste gases';

      case 'MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED':
        return which === 'imported' ? 'No, we do not have any measurable heat imported' : 'N/A';

      default:
        return '';
    }
  }
}
