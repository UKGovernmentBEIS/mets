import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { AerModule } from '../../../../aer.module';
import { mockState } from '../../../../submit/testing/mock-aer-apply-action';
import { AerSharedModule } from '../../../aer-shared.module';
import { DeleteComponent } from './delete.component';

describe('DeleteComponent', () => {
  let page: Page;
  let router: Router;
  let component: DeleteComponent;
  let fixture: ComponentFixture<DeleteComponent>;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'CALCULATION_CO2',
  });

  const createComponent = () => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(DeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  class Page extends BasePage<DeleteComponent> {
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
      imports: [SharedModule, AerModule, RouterTestingModule, AerSharedModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => createComponent());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the name of the source stream emission to be deleted', () => {
    expect(page.header.textContent.trim()).toContain(
      'Are you sure you want to delete  ‘the reference Anthracite  - Data gap ’?',
    );
  });

  it('should delete the source stream emission', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const index = Number(activatedRoute.snapshot.paramMap.get('index'));

    const aerSectionsCompleted = (mockState.requestTaskItem.requestTask.payload as any).aerSectionsCompleted[
      'CALCULATION_CO2'
    ];
    const remainingAerSectionsCompleted = aerSectionsCompleted.filter((_, idx) => index !== idx);

    const aerSourceStreamEmissions = (mockState.requestTaskItem.requestTask.payload as any).aer
      ?.monitoringApproachEmissions?.CALCULATION_CO2?.sourceStreamEmissions;
    const remainingAerSourceStreamEmissions = aerSourceStreamEmissions.filter((_, idx) => index !== idx);

    const remainingAer = {
      ...(mockState.requestTaskItem.requestTask.payload as any).aer,
      monitoringApproachEmissions: {
        ...(mockState.requestTaskItem.requestTask.payload as any).aer?.monitoringApproachEmissions,
        CALCULATION_CO2: {
          ...(mockState.requestTaskItem.requestTask.payload as any).aer?.monitoringApproachEmissions?.CALCULATION_CO2,
          sourceStreamEmissions: remainingAerSourceStreamEmissions,
        },
      },
    };

    const navigateSpy = jest.spyOn(router, 'navigate');
    page.deleteButton.click();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AER_SAVE_APPLICATION',
      requestTaskId: 1,
      requestTaskActionPayload: {
        aer: remainingAer,
        aerSectionsCompleted: {
          ...(mockState.requestTaskItem.requestTask.payload as any).aerSectionsCompleted,
          CALCULATION_CO2: [...remainingAerSectionsCompleted],
        },
        payloadType: 'AER_SAVE_APPLICATION_PAYLOAD',
      },
    });
    expect(navigateSpy).toHaveBeenCalledTimes(1);

    expect((store.getState().requestTaskItem.requestTask.payload as any).aer).toEqual({
      ...remainingAer,
    });

    expect((store.getState().requestTaskItem.requestTask.payload as any).aerSectionsCompleted).toEqual({
      ...(mockState.requestTaskItem.requestTask.payload as any).aerSectionsCompleted,
      CALCULATION_CO2: [...remainingAerSectionsCompleted],
    });
  });
});
