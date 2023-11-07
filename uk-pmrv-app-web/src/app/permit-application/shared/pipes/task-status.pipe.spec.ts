import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { mockClass } from '@testing';

import { MeasurementOfN2OEmissionPointCategoryAppliedTier, TasksService } from 'pmrv-api';

import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { n2oCategoryTierStatuses, n2oStaticStatuses } from '../../approaches/n2o/n2o-status';
import { transferredCo2Statuses } from '../../approaches/transferred-co2/transferred-co2-status';
import { initialState, PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../testing/mock-state';
import { TaskStatusPipe } from './task-status.pipe';

describe('TaskStatusPipe', () => {
  let pipe: TaskStatusPipe;
  let store: PermitApplicationStore<PermitApplicationState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [TaskStatusPipe],
      providers: [
        { provide: TasksService, useValue: mockClass(TasksService) },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });

    store = TestBed.inject(PermitApplicationStore);
    store.setState({ ...initialState, isRequestTask: true });
  });

  beforeEach(() => (pipe = new TaskStatusPipe(store)));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should resolve statuses as completed if request action', async () => {
    store.setState({ ...store.getState(), isRequestTask: false });
    await expect(firstValueFrom(pipe.transform('sourceStreams'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('emissionSources'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('assignmentOfResponsibilities'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('environmentalPermitsAndLicences'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('assignmentOfResponsibilities'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('environmentalPermitsAndLicences'))).resolves.toEqual('complete');
  });

  it('should resolve statuses', async () => {
    await expect(firstValueFrom(pipe.transform('sourceStreams'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('emissionSources'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('assignmentOfResponsibilities'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('environmentalPermitsAndLicences'))).resolves.toEqual('not started');

    store.setState(mockState);

    await expect(firstValueFrom(pipe.transform('assignmentOfResponsibilities'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('environmentalPermitsAndLicences'))).resolves.toEqual('complete');
  });

  it('should calculate the permit type', async () => {
    store.setState(mockState);

    await expect(firstValueFrom(pipe.transform('permitType'))).resolves.toEqual('complete');

    store.setState({ ...store.getState(), permitType: undefined });

    await expect(firstValueFrom(pipe.transform('permitType'))).resolves.toEqual('not started');
  });

  it('should calculate the source streams status', async () => {
    store.setState(mockState);

    await expect(firstValueFrom(pipe.transform('sourceStreams'))).resolves.toEqual('in progress');

    store.setState({ ...store.getState(), permit: { ...store.permit, sourceStreams: [] } });

    await expect(firstValueFrom(pipe.transform('sourceStreams'))).resolves.toEqual('not started');

    store.setState({
      ...store.getState(),
      permitSectionsCompleted: { ...store.payload.permitSectionsCompleted, sourceStreams: [true] },
    });

    await expect(firstValueFrom(pipe.transform('sourceStreams'))).resolves.toEqual('complete');
  });

  it('should calculate the emission summaries status', async () => {
    store.setState(mockState);

    await expect(firstValueFrom(pipe.transform('emissionSummaries'))).resolves.toEqual('in progress');

    store.setState({ ...store.getState(), permit: { ...store.permit, emissionPoints: [] } });

    await expect(firstValueFrom(pipe.transform('emissionSummaries'))).resolves.toEqual('needs review');

    store.setState({ ...store.getState(), permit: { ...store.permit, emissionSummaries: [] } });

    await expect(firstValueFrom(pipe.transform('emissionSummaries'))).resolves.toEqual('cannot start yet');

    store.setState({
      ...store.getState(),
      permitSectionsCompleted: {
        ...store.payload.permitSectionsCompleted,
        sourceStreams: [true],
        emissionPoints: [true],
        emissionSources: [true],
        regulatedActivities: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('emissionSummaries'))).resolves.toEqual('not started');
  });

  it('should calculate the n2o main task status', async () => {
    store.setState({
      ...store.getState(),
      permit: {
        ...store.getState().permit,
        monitoringApproaches: {
          MEASUREMENT_N2O: {},
        },
      },
    });

    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O'))).resolves.toEqual('not started');

    store.setState({
      ...store.getState(),
      permitSectionsCompleted: {
        MEASUREMENT_N2O_Description: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O'))).resolves.toEqual('in progress');

    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        ...n2oStaticStatuses.reduce((result, item) => ({ ...result, [item]: [true] }), {}),
        ...n2oCategoryTierStatuses.reduce((result, item) => ({ ...result, [item]: [true] }), {}),
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O'))).resolves.toEqual('complete');
  });

  it('should calculate the n2o main sub task status', async () => {
    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        MEASUREMENT_N2O_Category: [true],
        MEASUREMENT_N2O_Measured_Emissions: [true],
        MEASUREMENT_N2O_Applied_Standard: [true],
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O_Category_Tier', 0))).resolves.toEqual('complete');

    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        MEASUREMENT_N2O_Category: [true],
        MEASUREMENT_N2O_Measured_Emissions: [true],
        MEASUREMENT_N2O_Applied_Standard: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O_Category_Tier', 0))).resolves.toEqual('complete');

    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        MEASUREMENT_N2O_Category: [true],
        MEASUREMENT_N2O_Measured_Emissions: [false],
        MEASUREMENT_N2O_Applied_Standard: [true],
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O_Category_Tier', 0))).resolves.toEqual('in progress');

    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        MEASUREMENT_N2O_Category: [true],
        MEASUREMENT_N2O_Measured_Emissions: [false],
        MEASUREMENT_N2O_Applied_Standard: [false],
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O_Category_Tier', 0))).resolves.toEqual('in progress');

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          MEASUREMENT_N2O: {
            type: 'MEASUREMENT_N2O',
            emissionPointCategoryAppliedTiers: [
              {
                emissionPointCategory: {
                  sourceStreams: ['16236817394240.1574963093314663'],
                  emissionSources: ['16245246343280.27155194483385103'],
                  emissionPoint: 'unknown',
                  emissionType: 'ABATED',
                  monitoringApproachType: 'CALCULATION',
                  annualEmittedCO2Tonnes: '23.5',
                  categoryType: 'MAJOR',
                },
              },
            ] as MeasurementOfN2OEmissionPointCategoryAppliedTier[],
          },
        },
      }),
      permitSectionsCompleted: {
        MEASUREMENT_N2O_Category: [true],
        MEASUREMENT_N2O_Measured_Emissions: [true],
        MEASUREMENT_N2O_Applied_Standard: [true],
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O_Category_Tier', 0))).resolves.toEqual('needs review');

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          MEASUREMENT_N2O: {
            type: 'MEASUREMENT_N2O',
            emissionPointCategoryAppliedTiers: [
              {
                emissionPointCategory: {
                  sourceStreams: ['16236817394240.1574963093314663'],
                  emissionSources: ['unknown'],
                  emissionPoint: '16363790610230.8369404469603225',
                  emissionType: 'ABATED',
                  monitoringApproachType: 'CALCULATION',
                  annualEmittedCO2Tonnes: '23.5',
                  categoryType: 'MAJOR',
                },
              },
            ] as MeasurementOfN2OEmissionPointCategoryAppliedTier[],
          },
        },
      }),
      permitSectionsCompleted: {
        MEASUREMENT_N2O_Category: [true],
        MEASUREMENT_N2O_Measured_Emissions: [true],
        MEASUREMENT_N2O_Applied_Standard: [true],
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O_Category_Tier', 0))).resolves.toEqual('needs review');

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          MEASUREMENT_N2O: {
            type: 'MEASUREMENT_N2O',
            emissionPointCategoryAppliedTiers: [
              {
                emissionPointCategory: {
                  sourceStreams: ['16236817394240.1574963093314663'],
                  emissionSources: ['16245246343280.27155194483385103'],
                  emissionPoint: 'unknown',
                  emissionType: 'ABATED',
                  monitoringApproachType: 'CALCULATION',
                  annualEmittedCO2Tonnes: '23.5',
                  categoryType: 'MAJOR',
                },
              },
            ] as MeasurementOfN2OEmissionPointCategoryAppliedTier[],
          },
        },
      }),
      permitSectionsCompleted: {
        MEASUREMENT_N2O_Category: [true],
        MEASUREMENT_N2O_Measured_Emissions: [true],
        MEASUREMENT_N2O_Applied_Standard: [true],
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O_Category_Tier', 0))).resolves.toEqual('needs review');
  });

  it('should calculate the n2o sub task status', async () => {
    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O_Category', 0))).resolves.toEqual('not started');

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          MEASUREMENT_N2O: {
            type: 'MEASUREMENT_N2O',
            emissionPointCategoryAppliedTiers: [
              {
                emissionPointCategory: {
                  sourceStreams: ['16236817394240.1574963093314663'],
                  emissionSources: ['16245246343280.27155194483385103'],
                  emissionPoint: 'unknown',
                  emissionType: 'ABATED',
                  monitoringApproachType: 'CALCULATION',
                  annualEmittedCO2Tonnes: '23.5',
                  categoryType: 'MAJOR',
                },
              },
            ] as MeasurementOfN2OEmissionPointCategoryAppliedTier[],
          },
        },
      }),
    });
    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O_Category', 0))).resolves.toEqual('in progress');

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          MEASUREMENT_N2O: {
            type: 'MEASUREMENT_N2O',
            emissionPointCategoryAppliedTiers: [
              {
                emissionPointCategory: null,
              },
            ],
          },
        },
      }),
    });
    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O_Category', 0))).resolves.toEqual('not started');

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          MEASUREMENT_N2O: {
            type: 'MEASUREMENT_N2O',
            emissionPointCategoryAppliedTiers: [
              {
                emissionPointCategory: {
                  sourceStreams: ['16236817394240.1574963093314663'],
                  emissionSources: ['16245246343280.27155194483385103'],
                  emissionPoint: '16363790610230.8369404469603225',
                  emissionType: 'ABATED',
                  monitoringApproachType: 'CALCULATION',
                  annualEmittedCO2Tonnes: '23.5',
                  categoryType: 'MAJOR',
                  transfer: {
                    entryAccountingForTransfer: false,
                  },
                },
              },
            ] as MeasurementOfN2OEmissionPointCategoryAppliedTier[],
          },
        },
      }),
      permitSectionsCompleted: {
        MEASUREMENT_N2O_Category: [true],
      },
    });
    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O_Category', 0))).resolves.toEqual('complete');

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          MEASUREMENT_N2O: {
            type: 'MEASUREMENT_N2O',
            emissionPointCategoryAppliedTiers: [
              {
                emissionPointCategory: {
                  sourceStreams: ['unknown'],
                  emissionSources: ['16245246343280.27155194483385103'],
                  emissionPoint: '16363790610230.8369404469603225',
                  emissionType: 'ABATED',
                  monitoringApproachType: 'CALCULATION',
                  annualEmittedCO2Tonnes: '23.5',
                  categoryType: 'MAJOR',
                  transfer: {
                    entryAccountingForTransfer: false,
                  },
                },
              },
            ] as MeasurementOfN2OEmissionPointCategoryAppliedTier[],
          },
        },
      }),
      permitSectionsCompleted: {
        MEASUREMENT_N2O_Category: [true],
      },
    });
    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O_Category', 0))).resolves.toEqual('needs review');
  });

  it('should calculate the transferred main task status', async () => {
    store.setState({
      ...store.getState(),
      permit: {
        ...store.getState().permit,
        monitoringApproaches: {
          TRANSFERRED_CO2: {},
        },
      },
    });

    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_N2O'))).resolves.toEqual('not started');

    store.setState({
      ...store.getState(),
      permitSectionsCompleted: {
        TRANSFERRED_CO2_N2O_Transport_Network_Approach: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_N2O'))).resolves.toEqual('in progress');

    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        ...transferredCo2Statuses.reduce((result, item) => ({ ...result, [item]: [true] }), {}),
      },
    });

    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_N2O'))).resolves.toEqual('complete');
  });

  it('should calculate the transferred sub task status', async () => {
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_N2O_Transport_Network_Approach', 0))).resolves.toEqual(
      'not started',
    );

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            type: 'TRANSFERRED_CO2_N2O',
            monitoringTransportNetworkApproach: 'METHOD_A',
          },
        },
      }),
      permitSectionsCompleted: {
        TRANSFERRED_CO2_N2O_Transport_Network_Approach: [true],
      },
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_N2O_Transport_Network_Approach', 0))).resolves.toEqual(
      'complete',
    );
  });

  it('should calculate the transferred sub task status', async () => {
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_N2O_Pipeline', 0))).resolves.toEqual('not started');

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2_N2O: {
            type: 'TRANSFERRED_CO2_N2O',
            transportCO2AndN2OPipelineSystems: {
              exist: true,
            },
          },
        },
      }),
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_N2O_Pipeline', 0))).resolves.toEqual('in progress');
  });
});
