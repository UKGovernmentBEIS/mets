import { Pipe, PipeTransform } from '@angular/core';

import { AssessAndControlRisk, ManagementProceduresDefinition, Permit } from 'pmrv-api';

@Pipe({ name: 'IsRiskAssessment' })
export class IsRiskAssessmentPipe implements PipeTransform {
  transform(
    task: ManagementProceduresDefinition | AssessAndControlRisk,
    key: keyof Permit,
  ): task is AssessAndControlRisk {
    return key === 'assessAndControlRisk';
  }
}
