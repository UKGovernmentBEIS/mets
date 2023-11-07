import { Pipe, PipeTransform } from '@angular/core';

import { OrganisationStructure } from 'pmrv-api';

const FLIGHT_IDENTIFICATION_TYPE: Record<OrganisationStructure['legalStatusType'], string> = {
  LIMITED_COMPANY: 'Limited company',
  INDIVIDUAL: 'Individual',
  PARTNERSHIP: 'Partnership',
};

@Pipe({
  name: 'operatorDetailsLegalStatusType',
  pure: true,
  standalone: true,
})
export class OperatorDetailsLegalStatusTypePipe implements PipeTransform {
  transform(value: OrganisationStructure['legalStatusType']): string | null {
    return FLIGHT_IDENTIFICATION_TYPE[value] || null;
  }
}
