import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskDTO, TasksService } from 'pmrv-api';

import { HseTiTaskSharedModule } from '../shared/hseti-task-shared.module';
import { mockHseTiState } from '../test/mock';
import { SubmitContainerComponent } from './submit-container.component';

describe('SubmitContainerComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: SubmitContainerComponent;
  let fixture: ComponentFixture<SubmitContainerComponent>;

  const tasksService = mockClass(TasksService);
  const createComponent = (requestTaskType: RequestTaskDTO['type']) => {
    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...mockHseTiState,
      requestTaskItem: {
        ...mockHseTiState.requestTaskItem,
        requestTask: { ...mockHseTiState.requestTaskItem.requestTask, type: requestTaskType },
      },
    });
    fixture = TestBed.createComponent(SubmitContainerComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

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

    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item .app-task-list__task-name'));
    }

    get warningText(): string {
      return this.query<HTMLDivElement>('.govuk-warning-text')?.textContent?.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TaskSharedModule, HseTiTaskSharedModule, SubmitContainerComponent],
      providers: [KeycloakService, provideRouter([]), { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  it('should create', () => {
    createComponent('HSE_TI_APPLICATION_SUBMIT');

    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements for submit task', () => {
    createComponent('HSE_TI_APPLICATION_SUBMIT');

    expect(page.warningText).toBeFalsy();
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('Complete 2021-2025 HSE target increase application');
    expect(page.operatorText.textContent.trim()).toEqual('Assigned to: Operator1 England');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual(['Provide HSE details', 'Send to regulator']);
  });
});
