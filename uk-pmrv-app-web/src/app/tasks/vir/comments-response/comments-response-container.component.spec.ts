import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { mockStateRespond } from '@tasks/vir/comments-response/testing/mock-vir-application-respond-payload';
import { VirTaskCommentsResponseModule } from '@tasks/vir/comments-response/vir-task-comments-response.module';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { CommentsResponseContainerComponent } from './comments-response-container.component';

describe('CommentsResponseContainerComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: CommentsResponseContainerComponent;
  let fixture: ComponentFixture<CommentsResponseContainerComponent>;
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<CommentsResponseContainerComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get operatorText(): HTMLDivElement {
      return this.query<HTMLDivElement>('app-task-header-info > div:nth-child(1)');
    }

    get daysRemainingText(): HTMLDivElement {
      return this.query<HTMLDivElement>('app-task-header-info > div:nth-child(2)');
    }

    get pageContents() {
      return Array.from(
        this.query<HTMLDivElement>(
          '.govuk-grid-column-two-thirds:nth-child(1) .govuk-grid-row:nth-child(2)',
        ).querySelectorAll('h2, ul a, ul strong, ul span, dt, dd:nth-child(2)'),
      ).map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirTaskCommentsResponseModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockStateRespond);
    fixture = TestBed.createComponent(CommentsResponseContainerComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('2022 verifier improvement report');
    expect(page.operatorText.textContent.trim()).toEqual('Assigned to: Operator1 England');
    expect(page.daysRemainingText.textContent.trim()).toEqual('Days Remaining: 100');
    expect(page.pageContents).toEqual([
      'A1: an uncorrected error that remained before the verification report was issued',
      'Respond to the Regulator',
      'Respond to the Regulator',
      'not started',
      "Regulator's response",
      'Test operator actions A1',
      'A1: submit',
      'Send to the regulator',
      'Send to the regulator',
      'cannot start yet',
      'B1: an uncorrected error in the monitoring plan',
      'Respond to the Regulator',
      'Respond to the Regulator',
      'in progress',
      "Regulator's response",
      'Test operator actions B1',
      'B1: submit',
      'Send to the regulator',
      'Send to the regulator',
      'cannot start yet',
    ]);
  });
});
