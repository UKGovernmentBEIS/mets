import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { MonitoringApproachComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-monitoring-approach/monitoring-approach.component';
import { MonitoringApproachFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-monitoring-approach/monitoring-approach-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import {
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerCorsiaOpinionStatement,
  TasksService,
} from 'pmrv-api';

describe('MonitoringApproachComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: MonitoringApproachComponent;
  let fixture: ComponentFixture<MonitoringApproachComponent>;
  let formProvider: MonitoringApproachFormProvider;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<MonitoringApproachComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }
    get heading2(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h2');
    }

    get checkboxes() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__input');
    }
    get monitoringApproachTypeRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="monitoringApproachType"]');
    }
    get emissionsCorrectRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="emissionsCorrect"]');
    }
    set manuallyInternationalFlightsProvidedEmissions(value: string) {
      this.setInputValue('#manuallyInternationalFlightsProvidedEmissions', value);
    }
    set manuallyOffsettingFlightsProvidedEmissions(value: string) {
      this.setInputValue('#manuallyOffsettingFlightsProvidedEmissions', value);
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(MonitoringApproachComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MonitoringApproachComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: MonitoringApproachFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for new monitoring approach', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<MonitoringApproachFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestInfo: { type: 'AVIATION_AER_CORSIA' },
          requestTask: {
            id: 19,
            type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
              verificationReport: {},
              totalEmissionsProvided: '1200',
              totalOffsetEmissionsProvided: '1000',
              verificationSectionsCompleted: {},
            },
          },
        },
      } as any);

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Confirm monitoring approach and emissions');
      expect(page.heading2.map((h2) => h2.textContent.trim())).toEqual(['Total emissions provided']);
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents.length).toEqual(3);
      expect(page.errorSummaryListContents).toEqual([
        'Select at least one fuel type',
        'Select one emissions monitoring approach',
        'Select if the reported emissions are correct',
      ]);
    });

    it('should submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.checkboxes[0].click();
      page.checkboxes[1].click();
      page.monitoringApproachTypeRadioButtons[0].click();
      page.emissionsCorrectRadioButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([
        'Enter the total verified emissions from all international flights for the scheme year',
        'Enter the total verified emissions from flights with offsetting requirements for the scheme year',
      ]);

      page.manuallyInternationalFlightsProvidedEmissions = '1000';
      page.manuallyOffsettingFlightsProvidedEmissions = '2000';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
        requestTaskActionPayload: {
          opinionStatement: {
            fuelTypes: ['JET_KEROSENE', 'JET_GASOLINE'],
            monitoringApproachType: 'CERT_MONITORING',
            emissionsCorrect: false,
            manuallyInternationalFlightsProvidedEmissions: '1000',
            manuallyOffsettingFlightsProvidedEmissions: '2000',
          },
          verificationSectionsCompleted: { opinionStatement: [false] },
          payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing monitoring approach', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<MonitoringApproachFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestInfo: { type: 'AVIATION_AER_CORSIA' },
          requestTask: {
            id: 19,
            type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
              verificationReport: {
                opinionStatement: {
                  fuelTypes: ['JET_KEROSENE', 'JET_GASOLINE'],
                  monitoringApproachType: 'CERT_MONITORING',
                  emissionsCorrect: false,
                  manuallyInternationalFlightsProvidedEmissions: '1000',
                  manuallyOffsettingFlightsProvidedEmissions: '2000',
                },
              },
              totalEmissionsProvided: '1200',
              totalOffsetEmissionsProvided: '1000',
              verificationSectionsCompleted: {},
            },
          },
        },
      } as any);
      formProvider.setFormValue(
        (store.aerVerifyDelegate.payload as AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload)
          .verificationReport.opinionStatement as AviationAerCorsiaOpinionStatement,
      );

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Confirm monitoring approach and emissions');
      expect(page.heading2.map((h2) => h2.textContent.trim())).toEqual(['Total emissions provided']);
      expect(page.checkboxes[0]).toBeChecked();
      expect(page.checkboxes[1]).toBeChecked();
      expect(page.checkboxes[2]).not.toBeChecked();
      expect(page.checkboxes[3]).not.toBeChecked();
      expect(page.checkboxes[4]).not.toBeChecked();
      expect(page.monitoringApproachTypeRadioButtons[0]).toBeChecked();
      expect(page.monitoringApproachTypeRadioButtons[1]).not.toBeChecked();
      expect(page.monitoringApproachTypeRadioButtons[2]).not.toBeChecked();
      expect(page.submitButton).toBeTruthy();
    });

    it('should edit, submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.checkboxes[0].click();
      page.checkboxes[2].click();
      page.monitoringApproachTypeRadioButtons[1].click();
      page.emissionsCorrectRadioButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
        requestTaskActionPayload: {
          opinionStatement: {
            fuelTypes: ['JET_GASOLINE', 'AVIATION_GASOLINE'],
            monitoringApproachType: 'FUEL_USE_MONITORING',
            emissionsCorrect: true,
            manuallyInternationalFlightsProvidedEmissions: null,
            manuallyOffsettingFlightsProvidedEmissions: null,
          },
          verificationSectionsCompleted: { opinionStatement: [false] },
          payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });
});
