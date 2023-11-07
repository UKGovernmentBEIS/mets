import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { AerModule } from '../../../aer.module';
import { mockAerApplyPayload } from '../../testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '../../testing/mock-state';
import { MeasurementModule } from '../measurement.module';
import { OperationHoursComponent } from './operation-hours.component';

describe('OperationHoursComponent', () => {
  let page: Page;
  let router: Router;

  let component: OperationHoursComponent;
  let fixture: ComponentFixture<OperationHoursComponent>;

  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'MEASUREMENT_CO2',
  });

  class Page extends BasePage<OperationHoursComponent> {
    get operationalHours() {
      return this.getInputValue('#operationalHours');
    }
    set operationalHours(value: string) {
      this.setInputValue('#operationalHours', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }
  const createComponent = () => {
    fixture = TestBed.createComponent(OperationHoursComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);

    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule, MeasurementModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for adding a new emission point emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              MEASUREMENT_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.MEASUREMENT_CO2,
                emissionPointEmissions: [{}],
              },
            },
          },
          {
            MEASUREMENT_CO2: [],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Enter a value']);

      page.submitButton.click();
      fixture.detectChanges();

      page.operationalHours = '30';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              MEASUREMENT_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.MEASUREMENT_CO2,
                emissionPointEmissions: [
                  {
                    operationalHours: '30',
                    calculationCorrect: null,
                  },
                ],
              },
            },
          },
          {
            MEASUREMENT_CO2: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../gas-flow'], { relativeTo: route });
    });
  });

  describe('for editing a emission point emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              MEASUREMENT_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.MEASUREMENT_CO2,
                emissionPointEmissions: [
                  {
                    operationalHours: '30',
                  },
                ],
              },
            },
          },
          {
            MEASUREMENT_CO2: [],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should  fill the form with data from the store', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.operationalHours).toBe('30');
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../gas-flow'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
