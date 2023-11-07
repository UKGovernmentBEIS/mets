import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { DoalTaskComponent } from '../../../shared/components/doal-task/doal-task.component';
import {
  mockDoalApplicationSubmitRequestTaskItem,
  updateMockDoalApplicationSubmitRequestTaskItem,
} from '../../../test/mock';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryComponent> {
    get values() {
      return this.queryAll<HTMLElement>('.govuk-summary-list .govuk-summary-list__value');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SummaryComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
        {},
        {
          additionalDocuments: false,
        },
      ),
    });
  });
  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display additional documents data', () => {
    expect(page.values.map((el) => el.textContent.trim())).toEqual(['Yes', '3.png', 'additionalDocumentsComment']);
  });

  it('should submit status section', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'DOAL_SAVE_APPLICATION',
      requestTaskId: mockDoalApplicationSubmitRequestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'DOAL_SAVE_APPLICATION_PAYLOAD',
        doal: (mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
        doalSectionsCompleted: {
          additionalDocuments: true,
        },
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route });
  });
});
