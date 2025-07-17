import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { DoeEmissionsSummaryTemplateComponent } from '@aviation/shared/components/doe/doe-emissions-summary-template/doe-emissions-summary-template.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { BasePage, mockClass } from '@testing';

import { AviationDoECorsia, TasksService } from 'pmrv-api';

import { DoeCorsiaEmissionsFormProvider } from '../doe-corsia-emissions-form.provider';
import { DoeEmissionsSummaryComponent } from './emissions-summary.component';

class Page extends BasePage<DoeEmissionsSummaryComponent> {
  get summaryTitles() {
    return this.queryAll<HTMLDivElement>('.govuk-heading-m').map((element) => element.textContent.trim());
  }

  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('DoeEmissionsSummaryComponent', () => {
  let fixture: ComponentFixture<DoeEmissionsSummaryComponent>;
  let store: RequestTaskStore;
  let formProvider: DoeCorsiaEmissionsFormProvider;
  const requestTaskFileService = mockClass(RequestTaskFileService);
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DoeEmissionsSummaryTemplateComponent],
      providers: [
        provideRouter([]),
        { provide: TASK_FORM_PROVIDER, useClass: DoeCorsiaEmissionsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    formProvider = TestBed.inject<DoeCorsiaEmissionsFormProvider>(TASK_FORM_PROVIDER);
    setupStore(store, formProvider);

    fixture = TestBed.createComponent(DoeEmissionsSummaryComponent);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(13);

    expect(page.summaryTitles).toEqual(['Reason for estimating emissions', 'Emissions', 'Operator fee']);

    expect(page.summaryValues).toEqual([
      ['Why are we estimating emissions?', 'A verified emissions report has not been submitted'],
      ['Further details', 'further details'],

      ['Total emissions on all international flights', '100 tCO2'],
      ['Emissions from flights with offsetting requirements', '10 tCO2'],
      ['Emissions related to a claim from CORSIA eligible fuels', '10 tCO2'],
      ['How have you estimated or corrected the emissions?', 'text'],
      ['Supporting documents', 'None'],

      ['Do you need to charge the operator a fee?', 'Yes'],
      ['Total billable hours', '2000.2 hours'],
      ['Hourly rate', '£1.5 per hour'],
      ['Set a due date for the payment', '1 Jan 2025'],
      ['Regulator comments', 'regulator comments'],
      ['Total operator fee', '£3,000.30'],
    ]);
  });
});

function setupStore(store: RequestTaskStore, formProvider: DoeCorsiaEmissionsFormProvider) {
  store.setState({
    requestTaskItem: {
      requestTask: {
        id: 33,
        type: 'AVIATION_DOE_CORSIA_APPLICATION_SUBMIT',
        payload: {
          payloadType: 'AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD',
          sectionCompleted: false,
          doe: {
            determinationReason: {
              type: 'VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED',
              furtherDetails: 'further details',
              subtypes: null,
            },
            emissions: {
              calculationApproach: 'text',
              emissionsAllInternationalFlights: '100',
              emissionsClaimFromCorsiaEligibleFuels: '10',
              emissionsFlightsWithOffsettingRequirements: '10',
            },
            fee: {
              chargeOperator: true,
              feeDetails: {
                totalBillableHours: '2000.2',
                hourlyRate: '1.5',
                dueDate: new Date('2025-01-01'),
                comments: 'regulator comments',
              },
            },
          },
        },
        assignable: true,
        assigneeUserId: 'd9512e5e-4bd3-4aae-976b-e15d91f1eb74',
        assigneeFullName: 'Regulator England',
        startDate: '2023-06-13T17:08:58.013809Z',
      },
      allowedRequestTaskActions: ['AVIATION_DOE_CORSIA_SUBMIT_SAVE_PAYLOAD', 'AVIATION_DOE_CORSIA_UPLOAD_ATTACHMENT'],
      userAssignCapable: true,
      requestInfo: {
        id: 'DOECOR00178-2023-1',
        type: 'AVIATION_DOE_CORSIA',
        competentAuthority: 'ENGLAND',
        accountId: 12,
        requestMetadata: {
          type: 'AVIATION_DOE_CORSIA',
          year: '2022',
        },
      },
    },
    relatedTasks: [
      {
        creationDate: '2023-06-13T17:08:58.013809Z',
        requestId: 'DOECOR00178-2023-1',
        requestType: 'AVIATION_DOE_CORSIA',
        taskId: 33,
        taskAssignee: {
          firstName: 'Regulator',
          lastName: 'England',
        },
        taskAssigneeType: 'REGULATOR',
        taskType: 'AVIATION_DOE_CORSIA_APPLICATION_SUBMIT',
        accountId: 12,
        accountName: 'AviationOpNameTest1',
        competentAuthority: 'ENGLAND',
        isNew: false,
      },
    ],
    timeline: [],
    isTaskReassigned: false,
    taskReassignedTo: null,
    isEditable: true,
    tasksState: {
      abbreviations: {
        status: 'not started',
      },
    },
  } as any);

  formProvider.setFormValue(store.doeDelegate.payload.doe as AviationDoECorsia);
}
