import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ApproachesDeleteComponent } from './approaches-delete.component';

describe('ApproachesDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: ApproachesDeleteComponent;
  let fixture: ComponentFixture<ApproachesDeleteComponent>;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub(
    {
      monitoringApproach: mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2?.type,
    },
    {},
    { permitTask: 'monitoringApproaches' },
  );

  class Page extends BasePage<ApproachesDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get body() {
      return this.query<HTMLElement>('.govuk-body');
    }

    get deleteButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(ApproachesDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the name of the approach to be deleted', () => {
    expect(page.header.textContent.trim()).toContain("Are you sure you want to delete 'Calculation of CO2'?");
    expect(page.body.textContent.trim()).toContain(
      'All information related to the Calculation of CO2 approach will be deleted.',
    );
  });

  it('should delete the monitoring approach', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    const monitoringApproaches = mockAerApplyPayload.aer.monitoringApproachEmissions;
    const storedStatusesCompleted = mockAerApplyPayload.aerSectionsCompleted;
    const deletedMonitoringApproach = activatedRoute.snapshot.paramMap.get('monitoringApproach');

    delete monitoringApproaches[deletedMonitoringApproach];

    const remainingStatusesCompleted = Object.keys(storedStatusesCompleted)
      .filter((key) => key !== deletedMonitoringApproach)
      .reduce((res, key) => ({ ...res, [key]: storedStatusesCompleted[key] }), {});

    page.deleteButton.click();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AER_SAVE_APPLICATION',
      requestTaskId: 1,
      requestTaskActionPayload: {
        aerSectionsCompleted: {
          ...remainingStatusesCompleted,
          monitoringApproachEmissions: [false],
        },
        payloadType: 'AER_SAVE_APPLICATION_PAYLOAD',
        aer: {
          ...(mockState.requestTaskItem.requestTask.payload as any).aer,
          monitoringApproachEmissions: monitoringApproaches,
        },
      },
    });
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect((store.getState().requestTaskItem.requestTask.payload as any).aer.monitoringApproachEmissions).toEqual(
      monitoringApproaches,
    );
    expect((store.getState().requestTaskItem.requestTask.payload as any).aerSectionsCompleted).toEqual({
      ...remainingStatusesCompleted,
      monitoringApproachEmissions: [false],
    });
  });
});
