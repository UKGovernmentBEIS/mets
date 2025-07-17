import { TestBed } from '@angular/core/testing';

import { AssessAndControlRisk, ManagementProceduresDefinition, TasksService } from 'pmrv-api';

import { mockClass } from '../../../../testing';
import { IsRiskAssessmentPipe } from './is-risk-assessment.pipe';

describe('RiskAssessmentPipe', () => {
  let pipe: IsRiskAssessmentPipe;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [IsRiskAssessmentPipe],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });

    pipe = new IsRiskAssessmentPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return data flow activities task', () => {
    const task: AssessAndControlRisk = {
      appliedStandards: 'Standards',
      itSystemUsed: 'asdas',
      locationOfRecords: 'Location',
      procedureDescription: 'sadas',
      procedureDocumentName: 'Amazing',
      procedureReference: 'Ref',
      diagramReference: 'diagram ref',
      responsibleDepartmentOrRole: 'Department',
    };

    expect(pipe.transform(task, 'assessAndControlRisk')).toBeTruthy();
  });

  it('should return null for task other than risk assessment', () => {
    const task: ManagementProceduresDefinition = {
      appliedStandards: 'Standards',
      itSystemUsed: 'asdas',
      locationOfRecords: 'Location',
      procedureDescription: 'sadas',
      procedureDocumentName: 'Amazing',
      procedureReference: 'Ref',
      diagramReference: 'diagram ref',
      responsibleDepartmentOrRole: 'Department',
    };
    expect(pipe.transform(task, 'monitoringPlanAppropriateness')).toBeFalsy();
  });
});
