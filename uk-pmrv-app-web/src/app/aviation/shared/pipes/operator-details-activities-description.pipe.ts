import { Pipe, PipeTransform } from '@angular/core';

import { ActivitiesDescription } from 'pmrv-api';

interface FlightType {
  SCHEDULED: string;
  NON_SCHEDULED: string;
}

interface OperationScope {
  UK_DOMESTIC: string;
  UK_TO_EEA_COUNTRIES: string;
}

const OPERATOR_TYPE: Record<ActivitiesDescription['operatorType'], string> = {
  COMMERCIAL: 'Commercial',
  NON_COMMERCIAL: 'Non-commercial',
};

const FLIGHT_TYPE: FlightType = {
  SCHEDULED: 'Scheduled flights',
  NON_SCHEDULED: 'Non-scheduled flights',
};

const OPERATION_SCOPES: OperationScope = {
  UK_DOMESTIC: 'UK domestic',
  UK_TO_EEA_COUNTRIES: 'UK to EEA countries',
};

@Pipe({
  name: 'activitiesDescription',
  pure: true,
  standalone: true,
})
export class OperatorDetailsActivitiesDescriptionPipe implements PipeTransform {
  transform(value: string): string | null {
    return OPERATOR_TYPE[value] || FLIGHT_TYPE[value] || OPERATION_SCOPES[value] || null;
  }
}
