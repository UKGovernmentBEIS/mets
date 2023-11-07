import { Injectable } from '@angular/core';

import { OperatorAirImprovementResponse } from 'pmrv-api';

@Injectable()
export class AirImprovementResponseService {
  mapResponseTypeToPath(type: OperatorAirImprovementResponse['type']) {
    switch (type) {
      case 'YES':
        return 'improvement-positive';
      case 'NO':
        return 'improvement-negative';
      case 'ALREADY_MADE':
        return 'improvement-existing';
      default:
        return '';
    }
  }

  typeMatches(type: OperatorAirImprovementResponse['type'] | null, lastUrlSegment: string): boolean {
    switch (type) {
      case 'YES':
        return lastUrlSegment === this.mapResponseTypeToPath(type);
      case 'NO':
        return lastUrlSegment === this.mapResponseTypeToPath(type);
      case 'ALREADY_MADE':
        return lastUrlSegment === this.mapResponseTypeToPath(type);
      default:
        return false;
    }
  }
}
