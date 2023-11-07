import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import {
  CalculationOfCO2MonitoringApproach,
  CalculationOfPFCMonitoringApproach,
  FallbackMonitoringApproach,
  TasksService,
} from 'pmrv-api';

import { mockClass } from '../../../testing';
import { PermitIssuanceStore } from '../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockStateBuild } from '../testing/mock-state';
import { ApproachTaskPipe } from './approach-task.pipe';

describe('ApproachTaskPipe', () => {
  let pipe: ApproachTaskPipe;
  let store: PermitApplicationStore<PermitApplicationState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ApproachTaskPipe],
      providers: [
        { provide: TasksService, useValue: mockClass(TasksService) },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });
    store = TestBed.inject(PermitApplicationStore);
  });

  beforeEach(() => (pipe = new ApproachTaskPipe(store)));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', async () => {
    const pfcApproach: CalculationOfPFCMonitoringApproach = {
      type: 'CALCULATION_PFC',
      sourceStreamCategoryAppliedTiers: [],
      approachDescription: 'Perfluorocarbons monitoring approach description',
      cellAndAnodeTypes: [
        {
          anodeType: 'Anode type',
          cellType: 'Cell type',
        },
      ],
      collectionEfficiency: {
        locationOfRecords: 'locationOfRecords',
        procedureDescription: 'procedureDescription',
        procedureDocumentName: 'procedureDocumentName',
        procedureReference: 'procedureReference',
        responsibleDepartmentOrRole: 'responsibleDepartmentOrRole',
      },
      tier2EmissionFactor: {
        exist: false,
      },
    };

    const fallbackApproach: FallbackMonitoringApproach = {
      type: 'FALLBACK',
      sourceStreamCategoryAppliedTiers: [],
      approachDescription: 'Fallback monitoring approach description',
      justification: 'Fallback monitoring approach justification',
      annualUncertaintyAnalysis: {
        locationOfRecords: 'locationOfRecords',
        procedureDescription: 'procedureDescription',
        procedureDocumentName: 'procedureDocumentName',
        procedureReference: 'procedureReference',
        responsibleDepartmentOrRole: 'responsibleDepartmentOrRole',
      },
    };

    const monitoringApproaches = {
      CALCULATION_CO2: {
        type: 'CALCULATION_CO2',
      } as CalculationOfCO2MonitoringApproach,
      CALCULATION_PFC: pfcApproach,
      FALLBACK: fallbackApproach,
    };
    store.setState(mockStateBuild({ monitoringApproaches: monitoringApproaches }));

    await expect(firstValueFrom(pipe.transform('CALCULATION_PFC'))).resolves.toEqual(pfcApproach);
    await expect(firstValueFrom(pipe.transform('FALLBACK'))).resolves.toEqual(fallbackApproach);
  });
});
