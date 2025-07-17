import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'wasteGasActivity',
})
export class WasteGasActivityPipe implements PipeTransform {
  transform(
    value:
      | 'WASTE_GAS_PRODUCED'
      | 'WASTE_GAS_CONSUMED'
      | 'WASTE_GAS_FLARED'
      | 'WASTE_GAS_IMPORTED'
      | 'WASTE_GAS_EXPORTED'
      | 'NO_WASTE_GAS_ACTIVITIES',
  ): string {
    switch (value) {
      case 'WASTE_GAS_PRODUCED':
        return 'Waste gas produced';
      case 'WASTE_GAS_CONSUMED':
        return 'Waste gas consumed, including safety flaring';
      case 'WASTE_GAS_FLARED':
        return 'Waste gas flared, not including safety flaring';
      case 'WASTE_GAS_IMPORTED':
        return 'Waste gas imported';
      case 'WASTE_GAS_EXPORTED':
        return 'Waste gas exported';
      case 'NO_WASTE_GAS_ACTIVITIES':
        return 'No, there are no waste gas activities at this sub-installation';
      default:
        return '';
    }
  }
}
