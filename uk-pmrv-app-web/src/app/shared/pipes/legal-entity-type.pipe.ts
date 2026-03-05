import { Pipe, PipeTransform } from '@angular/core';

import { LegalEntityDTO } from 'pmrv-api';

@Pipe({ name: 'legalEntityType' })
export class LegalEntityTypePipe implements PipeTransform {
  transform(status?: LegalEntityDTO['type']): string {
    switch (status) {
      case 'LIMITED_COMPANY':
        return 'Limited Company';
      case 'OTHER':
        return 'Other';
      case 'PARTNERSHIP':
        return 'Partnership';
      case 'SOLE_TRADER':
        return 'Sole trader';
      default:
        return null;
    }
  }
}
