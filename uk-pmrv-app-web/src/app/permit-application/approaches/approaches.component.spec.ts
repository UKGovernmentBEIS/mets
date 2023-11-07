import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { monitoringApproachTypeOptions } from '@shared/components/approaches/approaches-options';
import { SharedModule } from '@shared/shared.module';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { SharedPermitModule } from '../shared/shared-permit.module';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '../testing/mock-state';
import { ApproachesComponent } from './approaches.component';

describe('ApproachesComponent', () => {
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: ApproachesComponent;
  let fixture: ComponentFixture<ApproachesComponent>;

  const tasksService = mockClass(TasksService);
  const availableMonitoringApproaches = monitoringApproachTypeOptions.reduce((res, approach) => {
    res[approach] = { type: approach };
    return res;
  }, {});

  const monitoringApproaches = Object.keys(mockState.permit.monitoringApproaches)
    .filter((key) => key !== 'CALCULATION_CO2')
    .reduce((obj, key) => {
      obj[key] = mockState.permit.monitoringApproaches[key];
      return obj;
    }, {});

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
      declarations: [ApproachesComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
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

  describe('define monitoring approaches can not start yet', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create component', () => {
      expect(component).toBeTruthy();
    });

    it('should hide all buttons', () => {
      expect(page.confirmButton).toBeFalsy();
      expect(page.addMonitoringApproachesButton).toBeFalsy();
    });
  });

  describe('defining monitoring approaches for first time', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockStateBuild({ monitoringApproaches: undefined }, { monitoringApproachesPrepare: [true] }));
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
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockStateBuild({ monitoringApproaches }, { monitoringApproachesPrepare: [true] }));
    });
    beforeEach(createComponent);

    it('should create component', () => {
      expect(component).toBeTruthy();
    });

    it('should display add another and confirm buttons, but hide add button', () => {
      expect(page.confirmButton).toBeTruthy();
      expect(page.addMonitoringApproachesButton).toBeFalsy();
    });

    it('should display selected monitoring approaches', () => {
      expect(page.monitoringApproaches).toEqual([
        [
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
        mockPostBuild({ monitoringApproaches }, { monitoringApproachesPrepare: [true], monitoringApproaches: [true] }),
      );
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: route, state: { notification: true } });
    });
  });

  describe('adding more monitoring approaches not permitted', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          { monitoringApproaches: availableMonitoringApproaches },
          { monitoringApproachesPrepare: [true] },
        ),
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
          { monitoringApproaches: availableMonitoringApproaches },
          { monitoringApproachesPrepare: [true], monitoringApproaches: [true] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: route, state: { notification: true } });
    });
  });
});
