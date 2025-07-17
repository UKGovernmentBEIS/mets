import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore } from '@core/store/auth';
import { SharedModule } from '@shared/shared.module';
import { BasePage, mockClass } from '@testing';

import {
  MiReportsService,
  OutstandingRequestTasksMiReportResult,
  RegulatorAuthoritiesService,
  RegulatorUsersAuthoritiesInfoDTO,
} from 'pmrv-api';

import { RegulatorOutstandingRequestTasksComponent } from './regulator-outstanding-request-tasks.component';

describe('RegulatorOutstandingRequestTasksComponent', () => {
  let component: RegulatorOutstandingRequestTasksComponent;
  let fixture: ComponentFixture<RegulatorOutstandingRequestTasksComponent>;
  let page: Page;
  let authStore: AuthStore;
  const miReportsService = mockClass(MiReportsService);
  const regulatorAuthoritiesService = mockClass(RegulatorAuthoritiesService);

  class Page extends BasePage<RegulatorOutstandingRequestTasksComponent> {
    get checkboxes() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__input');
    }

    get checkbox_labels() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__label');
    }

    get tasksButton() {
      return this.query<HTMLButtonElement>('button[id="taskTypes"]');
    }

    get regulatorsButton() {
      return this.query<HTMLButtonElement>('button[id="regulators"]');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get table() {
      return this.query<HTMLDivElement>('.govuk-table');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [RegulatorOutstandingRequestTasksComponent],
      providers: [
        { provide: MiReportsService, useValue: miReportsService },
        { provide: RegulatorAuthoritiesService, useValue: regulatorAuthoritiesService },
        DestroySubject,
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setCurrentDomain('INSTALLATION');
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegulatorOutstandingRequestTasksComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    miReportsService.getRegulatorRequestTaskTypes.mockReturnValue(of(requestTaskTypes));
    regulatorAuthoritiesService.getCaRegulators.mockReturnValue(of(regulators));
    miReportsService.generateReport.mockReturnValue(of(results as OutstandingRequestTasksMiReportResult));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fill filters', () => {
    page.tasksButton.click();
    fixture.detectChanges();
    expect(page.checkboxes.length).toEqual(3);
    expect(page.checkbox_labels[0].innerHTML).toContain('Review permit surrender');
    expect(page.checkbox_labels[1].innerHTML).toContain('Review notification');
    expect(page.checkbox_labels[2].innerHTML).toContain('Track payment for permit surrender');

    page.regulatorsButton.click();
    fixture.detectChanges();
    expect(page.checkboxes.length).toEqual(5);
    expect(page.checkbox_labels[3].innerHTML).toContain('Regulator England');
    expect(page.checkbox_labels[4].innerHTML).toContain('Nelle Sellers');
  });

  it('should return report data', () => {
    page.tasksButton.click();
    page.regulatorsButton.click();
    fixture.detectChanges();

    page.checkbox_labels[0].click();
    page.checkbox_labels[3].click();
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(miReportsService.generateReport).toHaveBeenCalledTimes(1);
    expect(miReportsService.generateReport).toHaveBeenCalledWith('INSTALLATION', {
      reportType: 'REGULATOR_OUTSTANDING_REQUEST_TASKS',
      requestTaskTypes: ['PERMIT_SURRENDER_APPLICATION_REVIEW'],
      userIds: ['Jon Jones'],
    });
    const cells = Array.from(page.table.querySelectorAll('td'));
    expect(cells.map((cell) => cell.textContent.trim())).toEqual([
      '1',
      'Installation',
      'Installation name',
      'Legal entity name',
      'AEMR1-2',
      'Permit Revocation',
      'Complete permit revocation',
      'Jon Jones',
      '11 Jan 2022',
      '145',
      '4',
      'Installation',
      'cam Installation name',
      'cam Legal entity name',
      'AEMR4-1',
      'Permit Revocation',
      'Complete permit revocation',
      'Jon Jones',
      '',
      '',
      '5',
      'Installation',
      'bk install name',
      'le',
      'AEMR5-1',
      'Permit Revocation',
      'Complete permit revocation',
      'Jon Jones',
      '',
      '',
    ]);
  });
});

const requestTaskTypes = [
  'PERMIT_SURRENDER_APPLICATION_REVIEW',
  'PERMIT_NOTIFICATION_APPLICATION_REVIEW',
  'PERMIT_SURRENDER_CONFIRM_PAYMENT',
];

const regulators: RegulatorUsersAuthoritiesInfoDTO = {
  caUsers: [
    {
      userId: 'Jon Jones',
      firstName: 'Regulator',
      lastName: 'England',
      jobTitle: 'Doctor',
      authorityCreationDate: '2021-08-31T17:35:42.747079Z',
      authorityStatus: 'ACTIVE',
    },
    {
      userId: 'db6ba6b4-d216-4604-9f3f-391141cc8561',
      firstName: 'Nelle',
      lastName: 'Sellers',
      jobTitle: 'Delectus sit quibu',
      authorityCreationDate: '2022-09-07T12:20:18.014896Z',
      authorityStatus: 'PENDING',
    },
  ],
  editable: true,
};

const results = {
  reportType: 'REGULATOR_OUTSTANDING_REQUEST_TASKS',
  columnNames: [
    'Account ID',
    'Account type',
    'Account name',
    'Legal Entity name',
    'Workflow ID',
    'Workflow type',
    'Workflow task name',
    'Workflow task assignee',
    'Workflow task due date',
    'Workflow task days remaining',
  ],
  results: [
    {
      'Account ID': 1,
      'Account type': 'INSTALLATION',
      'Account name': 'Installation name',
      'Legal Entity name': 'Legal entity name',
      'Workflow ID': 'AEMR1-2',
      'Workflow type': 'PERMIT_REVOCATION',
      'Workflow task name': 'PERMIT_REVOCATION_APPLICATION_SUBMIT',
      'Workflow task assignee': 'Jon Jones',
      'Workflow task days remaining': 145,
      'Workflow task due date': '2022-01-11T13:22:58.760561Z',
    },
    {
      'Account ID': 4,
      'Account type': 'INSTALLATION',
      'Account name': 'cam Installation name',
      'Legal Entity name': 'cam Legal entity name',
      'Workflow ID': 'AEMR4-1',
      'Workflow type': 'PERMIT_REVOCATION',
      'Workflow task name': 'PERMIT_REVOCATION_APPLICATION_SUBMIT',
      'Workflow task assignee': 'Jon Jones',
    },
    {
      'Account ID': 5,
      'Account type': 'INSTALLATION',
      'Account name': 'bk install name',
      'Legal Entity name': 'le',
      'Workflow ID': 'AEMR5-1',
      'Workflow type': 'PERMIT_REVOCATION',
      'Workflow task name': 'PERMIT_REVOCATION_APPLICATION_SUBMIT',
      'Workflow task assignee': 'Jon Jones',
    },
  ],
};
