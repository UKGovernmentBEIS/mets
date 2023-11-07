import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AirTaskSubmitModule } from '@tasks/air/submit/air-task-submit.module';
import { mockState } from '@tasks/air/submit/testing/mock-air-application-submit-payload';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { SubmitContainerComponent } from './submit-container.component';

describe('SubmitContainerComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: SubmitContainerComponent;
  let fixture: ComponentFixture<SubmitContainerComponent>;
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SubmitContainerComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get operatorText(): HTMLDivElement {
      return this.query<HTMLDivElement>('app-task-header-info > div:nth-child(1)');
    }

    get daysRemainingText(): HTMLDivElement {
      return this.query<HTMLDivElement>('app-task-header-info > div:nth-child(2)');
    }

    get airImprovementDataGroup() {
      return this.query('app-air-improvement-data-group');
    }

    get taskList() {
      return this.query('.govuk-grid-column-full > ul.app-task-list__items');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AirTaskSubmitModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(SubmitContainerComponent);
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
    expect(page.heading1.textContent.trim()).toEqual('2022 Annual improvement report');
    expect(page.operatorText.textContent.trim()).toEqual('Assigned to: Operator1 England');
    expect(page.daysRemainingText.textContent.trim()).toEqual('Days Remaining: Overdue');
    expect(page.airImprovementDataGroup).toBeTruthy();
    expect(page.taskList.textContent.trim()).toEqual('Send to the regulator cannot start yet');
  });
});
