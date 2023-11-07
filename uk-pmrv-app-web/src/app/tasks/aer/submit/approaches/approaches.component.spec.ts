import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { monitoringApproachTypeOptions } from '@shared/components/approaches/approaches-options';
import { AerModule } from '@tasks/aer/aer.module';
import { mockAerApplyPayload } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { mockPostBuild, mockStateBuild } from '../testing/mock-state';
import { ApproachesComponent } from './approaches.component';

describe('ApproachesComponent', () => {
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;
  let component: ApproachesComponent;
  let fixture: ComponentFixture<ApproachesComponent>;

  const tasksService = mockClass(TasksService);
  const availableMonitoringApproaches = monitoringApproachTypeOptions.reduce(
    (acc: { [x: string]: any }, curr: string) => ((acc[curr] = null), acc),
    {},
  );

  const monitoringApproachEmissions = mockAerApplyPayload.aer.monitoringApproachEmissions;

  class Page extends BasePage<ApproachesComponent> {
    get confirmButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }

    get addMonitoringApproachesButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add monitoring approaches',
      );
    }

    get monitoringApproaches() {
      return this.queryAll<HTMLDListElement>('dl').map((monitoringApproach) =>
        Array.from(monitoringApproach.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(ApproachesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('defining monitoring approaches for first time', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild({ monitoringApproachEmissions: {} }));
    });
    beforeEach(createComponent);

    it('should create component', () => {
      expect(component).toBeTruthy();
    });

    it('should display add monitoring approaches button and hide all others', () => {
      expect(page.addMonitoringApproachesButton).toBeTruthy();
      expect(page.confirmButton).toBeFalsy();
    });
  });

  describe('adding more monitoring approaches', () => {
    beforeEach(() => {
      delete monitoringApproachEmissions.CALCULATION_CO2;
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild({ ...monitoringApproachEmissions }));
    });
    beforeEach(createComponent);

    it('should create component', () => {
      expect(component).toBeTruthy();
    });

    it('should display add another and confirm buttons, but hide add button', () => {
      expect(page.confirmButton).toBeTruthy();
      expect(page.addMonitoringApproachesButton).toBeFalsy();
    });

    //ADD AFTER FULL IMPLEMENTATION
    // it('should display selected monitoring approaches', () => {
    //   expect(page.monitoringApproaches).toEqual([['Measurement of CO2 approach']]);
    // });

    it('should submit selected monitoring approaches and navigate to summary page', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.confirmButton.click();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild({ ...monitoringApproachEmissions }),
      );
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
    });
  });

  describe('adding more monitoring approaches not permitted', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          monitoringApproachEmissions: availableMonitoringApproaches,
        }),
      );
    });
    beforeEach(createComponent);

    it('should create component', () => {
      expect(component).toBeTruthy();
    });

    it('should display confirm button, but hide add and add another button', () => {
      expect(page.confirmButton).toBeTruthy();
      expect(page.addMonitoringApproachesButton).toBeFalsy();
    });

    it('should display selected monitoring approaches', () => {
      expect(page.monitoringApproaches).toEqual([
        [
          'Calculation of CO2',
          'Measurement of CO2',
          'Fallback approach',
          'Measurement of nitrous oxide (N2O)',
          'Calculation of perfluorocarbons (PFC)',
          'Inherent CO2 emissions',
          'Procedures for transferred CO2 or N2O',
          'Change',
        ],
      ]);
    });

    it('should submit selected monitoring approaches and navigate to summary page', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.confirmButton.click();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          { monitoringApproachEmissions: availableMonitoringApproaches },
          { monitoringApproachEmissions: [true] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
    });
  });
});
