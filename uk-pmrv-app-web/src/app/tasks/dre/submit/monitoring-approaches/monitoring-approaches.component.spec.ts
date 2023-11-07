import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { DreTaskComponent } from '../../shared/components/dre-task/dre-task.component';
import { dreCompleted, mockCompletedDreApplicationSubmitRequestTaskItem, updateMockedDre } from '../../test/mock';
import { MonitoringApproachesComponent } from './monitoring-approaches.component';

describe('MonitoringApproachesComponent', () => {
  let component: MonitoringApproachesComponent;
  let fixture: ComponentFixture<MonitoringApproachesComponent>;

  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<MonitoringApproachesComponent> {
    get monitoringApproaches() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__input');
    }

    get hasCalculateTransferCO2YesValue() {
      return this.query<HTMLInputElement>('#hasTransferCalculationCO2-option0');
    }
    get hasCalculateTransferCO2NoValue() {
      return this.query<HTMLInputElement>('#hasTransferCalculationCO2-option1');
    }

    get hasMeasureTransferredCO2YesValue() {
      return this.query<HTMLInputElement>('#hasTransferMeasurementCO2-option0');
    }
    get hasMeasureTransferredCO2NoValue() {
      return this.query<HTMLInputElement>('#hasTransferMeasurementCO2-option1');
    }

    get hasMeasureTransferredN2OYesValue() {
      return this.query<HTMLInputElement>('#hasTransferMeasurementN2O-option0');
    }
    get hasMeasureTransferredN2ONoValue() {
      return this.query<HTMLInputElement>('#hasTransferMeasurementN2O-option1');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(MonitoringApproachesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MonitoringApproachesComponent, DreTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new dre', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockedDre({ monitoringApproachReportingEmissions: undefined }, false),
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select at least one monitoring approach']);

      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.monitoringApproaches[0].click(); // calculation co2
      fixture.detectChanges();
      page.hasCalculateTransferCO2YesValue.click();

      page.monitoringApproaches[1].click(); // measurement co2
      fixture.detectChanges();
      page.hasMeasureTransferredCO2YesValue.click();

      page.monitoringApproaches[2].click(); // inherent co2

      page.monitoringApproaches[3].click(); // measurement n2o
      fixture.detectChanges();
      page.hasMeasureTransferredN2ONoValue.click();
      page.monitoringApproaches[4].click(); // calculation pfc

      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'DRE_SAVE_APPLICATION',
        requestTaskId: mockCompletedDreApplicationSubmitRequestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'DRE_SAVE_APPLICATION_PAYLOAD',
          dre: {
            ...dreCompleted,
            monitoringApproachReportingEmissions: {
              CALCULATION_CO2: {
                type: 'CALCULATION_CO2',
                calculateTransferredCO2: true,
              } as any,
              MEASUREMENT_CO2: {
                type: 'MEASUREMENT_CO2',
                measureTransferredCO2: true,
              } as any,
              MEASUREMENT_N2O: {
                type: 'MEASUREMENT_N2O',
                measureTransferredN2O: false,
              } as any,
              CALCULATION_PFC: {
                type: 'CALCULATION_PFC',
              } as any,
              INHERENT_CO2: {
                type: 'INHERENT_CO2',
              } as any,
            },
          },
          sectionCompleted: false,
        },
      });
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..', 'reportable-emissions'], { relativeTo: route });
    });
  });

  describe('for existing dre', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockedDre(
          {
            monitoringApproachReportingEmissions: {
              CALCULATION_CO2: {
                type: 'CALCULATION_CO2',
                calculateTransferredCO2: true,
              } as any,
            },
          },
          false,
        ),
      });
    });
    beforeEach(createComponent);

    it('should display existing selections', () => {
      expect(page.monitoringApproaches[0].checked).toBeTruthy();
      expect(page.hasCalculateTransferCO2YesValue.checked).toBeTruthy();
      expect(page.monitoringApproaches[1].checked).not.toBeTruthy();
    });
  });
});
