import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { MonitoringApproachFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-monitoring-approach/monitoring-approach-form.provider';
import { SummaryComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-monitoring-approach/summary/summary.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import {
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerCorsiaOpinionStatement,
  TasksService,
} from 'pmrv-api';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let formProvider: MonitoringApproachFormProvider;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SummaryComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: MonitoringApproachFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

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
                emissionsCorrect: true,
              },
            },
            totalEmissionsProvided: '1200',
            totalOffsetEmissionsProvided: '1000',
            verificationSectionsCompleted: { opinionStatement: [false] },
          },
        },
      },
      isEditable: true,
    } as any);
    formProvider.setFormValue(
      (store.aerVerifyDelegate.payload as AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload)
        .verificationReport.opinionStatement as AviationAerCorsiaOpinionStatement,
    );

    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.summaryValues).toEqual([
      [
        'Standard fuels and emission factors',
        `Jet kerosene (Jet A1 or Jet A) at 3.16 tCO2 per tonne of fuelJet gasoline (Jet B) at 3.10 tCO2 per tonne of fuel`,
      ],
      ['Monitoring approach', 'CERT only'],
      ['Emissions from all flights', '1200 tCO2'],
      ['Emissions from offset flights', '1000 tCO2'],
      ['Are the reported emissions correct?', 'Yes'],
    ]);
  });

  it('should submit a valid form and navigate to task list page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 19,
      requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
      requestTaskActionPayload: {
        opinionStatement: {
          fuelTypes: ['JET_KEROSENE', 'JET_GASOLINE'],
          monitoringApproachType: 'CERT_MONITORING',
          emissionsCorrect: true,
          manuallyInternationalFlightsProvidedEmissions: null,
          manuallyOffsettingFlightsProvidedEmissions: null,
        },
        verificationSectionsCompleted: { opinionStatement: [true] },
        payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../../..'], { relativeTo: activatedRoute });
  });
});
