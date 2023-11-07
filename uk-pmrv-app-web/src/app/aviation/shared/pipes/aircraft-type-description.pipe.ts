import { Pipe, PipeTransform } from '@angular/core';

import { AircraftTypeDTO, AircraftTypeInfo } from 'pmrv-api';

export function transformAircraftTypeDescription(
  aircraftType: AircraftTypeDTO | AircraftTypeInfo,
  type: 'label' | 'hint' | 'full' = 'full',
) {
  switch (type) {
    case 'label':
      return `${aircraftType.model} (${aircraftType.designatorType})`;
    case 'hint':
      return `${aircraftType.manufacturer}`;
    case 'full':
      return `${aircraftType.manufacturer} ${aircraftType.model} (${aircraftType.designatorType})`;
    default:
      throw new Error(`cannot transform aircraft type description for arg type ${type}`);
  }
}
@Pipe({
  name: 'aircraftTypeDescription',
  standalone: true,
})
export class AircraftTypeDescriptionPipe implements PipeTransform {
  transform = transformAircraftTypeDescription;
}
