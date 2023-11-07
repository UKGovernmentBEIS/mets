import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, of } from 'rxjs';

import { BREADCRUMB_ITEMS } from '@core/navigation/breadcrumbs';
import { AuthStore } from '@core/store/auth';
import { AssignmentConfirmationComponent } from '@shared/assignment-confirmation/assignment-confirmation.component';
import { AssignmentComponent } from '@shared/components/assignment/assignment.component';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { TasksModule } from '@tasks/tasks.module';
import { BasePage, mockClass } from '@testing';

import { TasksAssignmentService } from 'pmrv-api';

import { ChangeAssigneeComponent } from './change-assignee.component';

describe('ChangeAssigneeComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: ChangeAssigneeComponent;
  let fixture: ComponentFixture<ChangeAssigneeComponent>;
  let authStore: AuthStore;

  const route = {
    routeConfig: { path: 'change-assignee' },
    parent: { routeConfig: { path: '' } },
  };
  const breadcrumbs = new BehaviorSubject([{ text: 'Parent', link: [''] }]);
  const tasksAssignmentService = mockClass(TasksAssignmentService);
  tasksAssignmentService.assignTask.mockReturnValue(of({}));
  tasksAssignmentService.getCandidateAssigneesByTaskId.mockReturnValue(
    of([{ id: '100', firstName: 'John', lastName: 'Doe' }]),
  );

  class Page extends BasePage<ChangeAssigneeComponent> {
    get select(): HTMLSelectElement {
      return this.query('select');
    }

    get options(): string[] {
      return Array.from(this.select.options).map((option) => option.textContent.trim());
    }

    get confirmationTitle() {
      return this.queryAll<HTMLHeadingElement>('h1');
    }

    get button(): HTMLButtonElement {
      return this.query('button');
    }

    get selectValue(): string {
      return this.select.value;
    }

    set selectValue(value: string) {
      this.setInputValue('select', value);
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChangeAssigneeComponent, AssignmentComponent, AssignmentConfirmationComponent],
      imports: [TasksModule],
      providers: [
        { provide: TasksAssignmentService, useValue: tasksAssignmentService },
        { provide: BREADCRUMB_ITEMS, useValue: breadcrumbs },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'OPERATOR' });
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState({
      requestTaskItem: {
        requestTask: {
          id: 1,
          assigneeFullName: 'John Doe',
        },
        userAssignCapable: true,
      },
    } as CommonTasksState);

    fixture = TestBed.createComponent(ChangeAssigneeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display confirmation', () => {
    expect(page.options).toEqual(['John Doe']);

    page.selectValue = '100';

    page.button.click();
    fixture.detectChanges();

    expect(page.confirmationTitle.map((el) => el.textContent)).toEqual(['The task has been reassigned to']);
  });
});
