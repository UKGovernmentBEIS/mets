import { TestBed } from '@angular/core/testing';

import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { InspectionService } from '@tasks/inspection/core/inspection.service';
import { inspectionSubmitMockState } from '@tasks/inspection/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { RequestTaskDTO } from 'pmrv-api';

import { pageTitleResolver } from './page-title.resolver';

jest.mock('@angular/core', () => ({
  ...jest.requireActual('@angular/core'),
  inject: (token: any) => TestBed.inject(token),
}));

describe('pageTitleResolver', () => {
  let store: jest.Mocked<CommonTasksStore>;
  let inspectionService: jest.Mocked<InspectionService>;

  const buildMockData = (taskType: RequestTaskDTO['type']) => {
    store.getState.mockReturnValue({
      requestTaskItem: {
        requestTask: { ...inspectionSubmitMockState.requestTaskItem.requestTask, type: taskType },
      },
    } as any);
  };

  beforeEach(() => {
    store = {
      getState: jest.fn(),
    } as unknown as jest.Mocked<CommonTasksStore>;

    inspectionService = {
      auditYear: '2023',
    } as unknown as jest.Mocked<InspectionService>;

    TestBed.configureTestingModule({
      providers: [
        { provide: CommonTasksStore, useValue: store },
        { provide: InspectionService, useValue: inspectionService },
        ItemNamePipe,
      ],
    });
  });

  it('should return "Create an on-site inspection" for onsite type', () => {
    buildMockData('INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT');

    const result = pageTitleResolver(null!, null!);

    expect(result).toBe('Create an on-site inspection');
  });

  it('should return "Create 2023 audit report" for audit type', () => {
    buildMockData('INSTALLATION_AUDIT_APPLICATION_SUBMIT');

    const result = pageTitleResolver(null!, null!);

    expect(result).toBe('Create 2023 audit report');
  });
});
