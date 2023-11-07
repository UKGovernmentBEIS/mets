import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AirTaskCommentsResponseModule } from '@tasks/air/comments-response/air-task-comments-response.module';
import { mockStateRespond } from '@tasks/air/comments-response/testing/mock-air-application-respond-payload';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
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
        ).querySelectorAll('h2, ul li span, govuk-tag, ul span, dt, dd:nth-child(2)'),
      ).map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AirTaskCommentsResponseModule, RouterTestingModule],
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
    expect(page.heading1.textContent.trim()).toEqual('2022 Annual improvement report follow up');
    expect(page.operatorText.textContent.trim()).toEqual('Assigned to: Operator1 England');
    expect(page.daysRemainingText.textContent.trim()).toEqual('Days Remaining: 100');
    expect(page.pageContents).toEqual([
      'Item 1: F1: Acetylene: major: emission factor',
      'Respond to regulator',
      'completed',
      "Regulator's response",
      'Test official response 1',
      'Item 1: Submit response',
      'Item 1: Send response to regulator',
      'not started',
      'Item 2: F1: Acetylene: major: emission factor',
      'Respond to regulator',
      'in progress',
      "Regulator's response",
      'Test official response 2',
      'Item 2: Submit response',
      'Item 2: Send response to regulator',
      'Item 2: Send response to regulator',
      'cannot start yet',
      'Item 3: EP1: West side chimney: major',
      'Respond to regulator',
      'not started',
      "Regulator's response",
      'Test official response 3',
      'Item 3: Submit response',
      'Item 3: Send response to regulator',
      'Item 3: Send response to regulator',
      'cannot start yet',
    ]);
  });
});
