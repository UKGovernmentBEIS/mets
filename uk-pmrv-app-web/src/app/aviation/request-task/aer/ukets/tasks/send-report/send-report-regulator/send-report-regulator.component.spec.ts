import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { mockState } from '../testing/mock-state';
import { SendReportRegulatorComponent } from './send-report-regulator.component';

describe('SendReportRegulatorComponent', () => {
  let component: SendReportRegulatorComponent;
  let fixture: ComponentFixture<SendReportRegulatorComponent>;
  let page: Page;
  let store: RequestTaskStore;
  let router: Router;
  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SendReportRegulatorComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-l');
    }
    get paragraphsContent() {
      return this.queryAll('p').map((p) => p.textContent.trim());
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(SendReportRegulatorComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show content', () => {
    expect(page.heading).toBeTruthy();
    expect(page.heading.textContent.trim()).toEqual('Send report to regulator');
    expect(page.paragraphsContent[0]).toEqual('Your report will be sent directly to the Environment Agency.');
  });

  it('should submit', async () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const navigateSpy = jest.spyOn(router, 'navigate');

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AVIATION_AER_UKETS_SUBMIT_APPLICATION',
      requestTaskId: mockState.requestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });

    page.submitButton.click();
    fixture.detectChanges();
    expect(navigateSpy).toHaveBeenCalledWith(['../confirmation-regulator'], { relativeTo: activatedRouteStub });
  });

  it('should submit amends', async () => {
    store.setState({
      ...mockState,
      requestTaskItem: {
        ...mockState.requestTaskItem,
        requestTask: {
          ...mockState.requestTaskItem.requestTask,
          type: 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT',
          payload: {
            ...mockState.requestTaskItem.requestTask.payload,
            payloadType: 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD',
            verificationSectionsCompleted: {},
          },
        },
      },
    } as any);
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const navigateSpy = jest.spyOn(router, 'navigate');

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AVIATION_AER_UKETS_SUBMIT_APPLICATION_AMEND',
      requestTaskId: mockState.requestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'AVIATION_AER_UKETS_SUBMIT_APPLICATION_AMEND_PAYLOAD',
        aerSectionsCompleted: {
          saf: [true],
          operatorDetails: [true],
          monitoringApproach: [true],
          additionalDocuments: [true],
          reportingObligation: [true],
          serviceContactDetails: [true],
          aggregatedEmissionsData: [true],
          aviationAerAircraftData: [true],
          aerMonitoringPlanChanges: [true],
          aviationAerTotalEmissionsConfidentiality: [true],
        },
      },
    });

    page.submitButton.click();
    fixture.detectChanges();
    expect(navigateSpy).toHaveBeenCalledWith(['../confirmation-regulator'], { relativeTo: activatedRouteStub });
  });
});
