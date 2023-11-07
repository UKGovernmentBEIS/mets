import { Injectable } from '@angular/core';

import { SiteVisit } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class SiteVisitsService {
  mapVisitTypeToPath(visitType: SiteVisit['siteVisitType']) {
    switch (visitType) {
      case 'IN_PERSON':
        return 'in-person-visit';
      case 'VIRTUAL':
        return 'virtual-visit';
      case 'NO_VISIT':
        return 'no-visit';
    }
  }

  siteVisitTypeMatches(visitType: SiteVisit['siteVisitType'] | null, lastUrlSegment: string): boolean {
    switch (visitType) {
      case 'IN_PERSON':
        return lastUrlSegment === this.mapVisitTypeToPath(visitType);
      case 'VIRTUAL':
        return lastUrlSegment === this.mapVisitTypeToPath(visitType);
      case 'NO_VISIT':
        return lastUrlSegment === this.mapVisitTypeToPath(visitType);
      default:
        return false;
    }
  }

  mapVisitTypeToLabel(visitType: SiteVisit['siteVisitType']) {
    switch (visitType) {
      case 'IN_PERSON':
        return 'In person visit';
      case 'VIRTUAL':
        return 'Virtual visit';
      case 'NO_VISIT':
        return 'No visit';
    }
  }
}
