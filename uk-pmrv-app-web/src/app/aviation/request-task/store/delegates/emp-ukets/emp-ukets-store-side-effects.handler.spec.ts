import { BusinessErrorService } from '@error/business-error/business-error.service';
import { mockClass } from '@testing';
import produce from 'immer';

import { EmpProcedureForm, TasksService } from 'pmrv-api';

import { RequestTaskStore } from '../../request-task.store';
import { EmpRequestTaskPayloadUkEts } from '../../request-task.types';
import { EmpUkEtsStoreSideEffectsHandler } from './emp-ukets-store-side-effects.handler';

function createMockProcedure(): EmpProcedureForm {
  return {
    procedureDescription: 'TEST',
    procedureReference: 'TEST',
    procedureDocumentName: 'TEST',
    itSystemUsed: 'TEST',
    locationOfRecords: 'TEST',
    responsibleDepartmentOrRole: 'TEST',
  };
}

describe('EmpUkEtsStoreSideEffectsHandler', () => {
  const tasksService = mockClass(TasksService);
  const businessErrorService = mockClass(BusinessErrorService);
  const store = new RequestTaskStore(tasksService, businessErrorService);
  const sideEffectsHandler = new EmpUkEtsStoreSideEffectsHandler(store);

  beforeEach(() => {
    store.setRequestTaskItem({
      requestTask: {
        payload: {
          payloadType: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT_PAYLOAD',
          empAttachments: [],
          empSectionsCompleted: {},
        } as unknown as EmpRequestTaskPayloadUkEts,
      },
    });
    store.initStoreDelegateByRequestType('EMP_ISSUANCE_UKETS');
  });

  it("should set management procedures status to 'in progress' when switching to small emitters type", () => {
    store.setPayload(
      produce(store.empUkEtsDelegate.payload as EmpRequestTaskPayloadUkEts, (updatedPayload) => {
        updatedPayload.emissionsMonitoringPlan.emissionsMonitoringApproach = {
          monitoringApproachType: 'EUROCONTROL_SUPPORT_FACILITY',
        };
        updatedPayload.emissionsMonitoringPlan.managementProcedures = {
          monitoringReportingRoles: { monitoringReportingRoles: [{ jobTitle: 'TEST', mainDuties: 'TEST' }] },
          recordKeepingAndDocumentation: createMockProcedure(),
        };
        updatedPayload.empSectionsCompleted = { emissionsMonitoringApproach: [true], managementProcedures: [true] };
      }),
    );

    const payload = sideEffectsHandler.applySideEffects({
      emissionsMonitoringApproach: { monitoringApproachType: 'EUROCONTROL_SMALL_EMITTERS' },
    });

    expect(payload.empSectionsCompleted.managementProcedures).toEqual([false]);
  });

  it('should remove other management procedure forms when switching to support facility type', () => {
    store.setPayload(
      produce(store.empUkEtsDelegate.payload as EmpRequestTaskPayloadUkEts, (updatedPayload) => {
        updatedPayload.emissionsMonitoringPlan.emissionsMonitoringApproach = {
          monitoringApproachType: 'EUROCONTROL_SMALL_EMITTERS',
        };
        updatedPayload.emissionsMonitoringPlan.managementProcedures = {
          monitoringReportingRoles: { monitoringReportingRoles: [{ jobTitle: 'TEST', mainDuties: 'TEST' }] },
          recordKeepingAndDocumentation: createMockProcedure(),
          assignmentOfResponsibilities: createMockProcedure(),
          monitoringPlanAppropriateness: createMockProcedure(),
        };
        updatedPayload.empSectionsCompleted = { emissionsMonitoringApproach: [true], managementProcedures: [false] };
      }),
    );

    const payload = sideEffectsHandler.applySideEffects({
      emissionsMonitoringApproach: { monitoringApproachType: 'EUROCONTROL_SUPPORT_FACILITY' },
    });

    expect('assignmentOfResponsibilities' in payload.emissionsMonitoringPlan.managementProcedures).toBe(false);
    expect('monitoringPlanAppropriateness' in payload.emissionsMonitoringPlan.managementProcedures).toBe(false);
    expect(payload.empSectionsCompleted.managementProcedures).toEqual([false]);
  });

  it('should remove a delete consumption method procedure payload when the consumption method is removed from aircraft types', () => {
    store.setPayload(
      produce(store.empUkEtsDelegate.payload as EmpRequestTaskPayloadUkEts, (updatedPayload) => {
        updatedPayload.emissionsMonitoringPlan.emissionsMonitoringApproach = {
          monitoringApproachType: 'FUEL_USE_MONITORING',
        };
        updatedPayload.emissionsMonitoringPlan.emissionSources = {
          aircraftTypes: [
            {
              numberOfAircrafts: 1,
              fuelTypes: ['OTHER'],
              isCurrentlyUsed: false,
              fuelConsumptionMeasuringMethod: 'FUEL_UPLIFT',
              aircraftTypeInfo: {
                manufacturer: 'a',
                model: 'b',
                designatorType: 'c',
              },
            },
          ],
        };
        updatedPayload.emissionsMonitoringPlan.fuelUpliftMethodProcedures = {
          blockHoursPerFlight: createMockProcedure(),
          zeroFuelUplift: 'a',
          fuelUpliftSupplierRecordType: 'FUEL_DELIVERY_NOTES',
          fuelDensity: createMockProcedure(),
        };
        updatedPayload.empSectionsCompleted = {
          emissionsMonitoringApproach: [true],
          emissionSources: [true],
          fuelUpliftMethodProcedures: [true],
        };
      }),
    );

    const payload = sideEffectsHandler.applySideEffects({
      emissionSources: {
        aircraftTypes: [
          {
            numberOfAircrafts: 1,
            fuelTypes: ['OTHER'],
            isCurrentlyUsed: false,
            fuelConsumptionMeasuringMethod: 'BLOCK_HOUR',
            aircraftTypeInfo: {
              manufacturer: 'a',
              model: 'b',
              designatorType: 'c',
            },
          },
        ],
      },
    });

    expect('fuelUpliftMethodProcedures' in payload.emissionsMonitoringPlan).toBe(false);
  });
});
