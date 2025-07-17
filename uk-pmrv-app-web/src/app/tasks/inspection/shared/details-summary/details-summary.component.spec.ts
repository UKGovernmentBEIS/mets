import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { UserFullNamePipe } from '@shared/pipes/user-full-name.pipe';
import { inspectionSubmitMockState, mockOfficers } from '@tasks/inspection/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksAssignmentService, TasksService } from 'pmrv-api';

import { DetailsSummaryComponent } from './details-summary.component';

describe('DetailsSummaryComponent', () => {
  let page: Page;
  let component: DetailsSummaryComponent;
  let fixture: ComponentFixture<DetailsSummaryComponent>;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const tasksAssignmentService = mockClass(TasksAssignmentService);

  class Page extends BasePage<DetailsSummaryComponent> {
    get header(): string {
      return this.query('app-page-heading').textContent.trim();
    }

    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    tasksAssignmentService.getCandidateAssigneesByTaskType.mockReturnValue(of(mockOfficers));

    await TestBed.configureTestingModule({
      imports: [DetailsSummaryComponent],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TasksService, useValue: tasksService },
        { provide: TasksAssignmentService, useValue: tasksAssignmentService },
        TaskTypeToBreadcrumbPipe,
        UserFullNamePipe,
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(inspectionSubmitMockState);

    fixture = TestBed.createComponent(DetailsSummaryComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);

    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.header).toBeTruthy();
    expect(page.summaryValues).toEqual([
      ['Names of officers', '1. newreg2 User 2. newreg3 User'],
      ['Additional information', 'additionalInformation 1'],
    ]);
  });
});
