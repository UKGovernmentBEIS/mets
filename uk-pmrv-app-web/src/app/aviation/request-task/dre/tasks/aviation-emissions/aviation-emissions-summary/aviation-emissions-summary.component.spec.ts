import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { AviationEmissionsSummaryTemplateComponent } from '@aviation/shared/components/dre/aviation-emissions-summary-template';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { BasePage, mockClass } from '@testing';

import { AviationDre, TasksService } from 'pmrv-api';

import { AviationEmissionsFormProvider } from '../aviation-emissions-form.provider';
import { AviationEmissionsSummaryComponent } from './aviation-emissions-summary.component';

class Page extends BasePage<AviationEmissionsSummaryComponent> {
  get summaryTitles() {
    return this.queryAll<HTMLDivElement>('.govuk-heading-m').map((element) => element.textContent.trim());
  }

  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('AviationEmissionsSummaryComponent', () => {
  let fixture: ComponentFixture<AviationEmissionsSummaryComponent>;
  let store: RequestTaskStore;
  let formProvider: AviationEmissionsFormProvider;
  const requestTaskFileService = mockClass(RequestTaskFileService);
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, AviationEmissionsSummaryTemplateComponent],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AviationEmissionsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    formProvider = TestBed.inject<AviationEmissionsFormProvider>(TASK_FORM_PROVIDER);
    setupStore(store, formProvider);

    fixture = TestBed.createComponent(AviationEmissionsSummaryComponent);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(11);

    expect(page.summaryTitles).toEqual([
      'Reason for determining the aviation emissions',
      'Aviation emissions',
      'Operator fee',
    ]);

    expect(page.summaryValues).toEqual([
      ['Why are you determining the aviation emissions?', 'Correcting a non-material misstatement'],
      ['Regulator comments', 'further details optional'],

      ['Total aviation emissions', '2000 tCO2'],
      ['How have you calculated the emissions?', 'Explain how you calculated the emissions for another data source'],
      ['Supporting documents', ''],

      ['Do you need to charge the operator a fee?', 'Yes'],
      ['Billable hours', '2000.2 hours'],
      ['Hourly rate', '£1.5 per hour'],
      ['Payment due date', '1 January 2025'],
      ['Regulator comments', 'regulator comments'],
      ['Total operator fee', '£3,000.30'],
    ]);
  });
});

function setupStore(store: RequestTaskStore, formProvider: AviationEmissionsFormProvider) {
  store.setState({
    requestTaskItem: {
      requestTask: {
        id: 33,
        type: 'AVIATION_DRE_UKETS_APPLICATION_SUBMIT',
        payload: {
          payloadType: 'AVIATION_DRE_UKETS_APPLICATION_SUBMIT_PAYLOAD',
          sectionCompleted: false,
          dre: {
            determinationReason: {
              type: 'CORRECTING_NON_MATERIAL_MISSTATEMENT',
              furtherDetails: 'further details optional',
            },
            totalReportableEmissions: '2000',
            calculationApproach: {
              type: 'OTHER_DATASOURCE',
              otherDataSourceExplanation: 'Explain how you calculated the emissions for another data source',
            },
            supportingDocuments: ['c5322719-abc1-419a-839e-eb9fbf27f277'],
            fee: {
              chargeOperator: true,
              feeDetails: {
                totalBillableHours: '2000.2',
                hourlyRate: '1.5',
                dueDate: '2025-01-01T00:00:00.000Z',
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
      allowedRequestTaskActions: ['AVIATION_DRE_UKETS_SAVE_APPLICATION', 'AVIATION_DRE_UPLOAD_ATTACHMENT'],
      userAssignCapable: true,
      requestInfo: {
        id: 'DRE00012-2022-1',
        type: 'AVIATION_DRE_UKETS',
        competentAuthority: 'ENGLAND',
        accountId: 12,
        requestMetadata: {
          type: 'AVIATION_DRE',
          year: '2022',
        },
      },
    },
    relatedTasks: [
      {
        creationDate: '2023-06-13T17:08:58.013809Z',
        requestId: 'DRE00012-2022-1',
        requestType: 'AVIATION_DRE_UKETS',
        taskId: 33,
        taskAssignee: {
          firstName: 'Regulator',
          lastName: 'England',
        },
        taskAssigneeType: 'REGULATOR',
        taskType: 'AVIATION_DRE_UKETS_APPLICATION_SUBMIT',
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

  formProvider.setFormValue(store.dreDelegate.payload.dre as AviationDre);
}
