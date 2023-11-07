import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { DoalTaskComponent } from '../../shared/components/doal-task/doal-task.component';
import {
  mockDoalApplicationSubmitRequestTaskItem,
  updateMockDoalApplicationSubmitRequestTaskItem,
} from '../../test/mock';
import { DeterminationComponent } from './determination.component';

describe('DeterminationComponent', () => {
  let component: DeterminationComponent;
  let fixture: ComponentFixture<DeterminationComponent>;

  let router: Router;
  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {
      taskId: `${mockDoalApplicationSubmitRequestTaskItem.requestTask.id}`,
    },
    null,
    { sectionKey: 'determination' },
  );
  let activatedRoute: ActivatedRoute;

  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<DeterminationComponent> {
    get buttons() {
      return this.queryAll<HTMLLIElement>('button');
    }

    get proceedToAuthorityButton() {
      return this.buttons.filter((el) => el.innerHTML.trim() === 'Proceed to UK ETS authority')[0];
    }

    get closeButton() {
      return this.buttons.filter((el) => el.innerHTML.trim() === 'Close')[0];
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(DeterminationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DeterminationComponent, DoalTaskComponent],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
        {
          determination: undefined,
        },
        {
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
          determination: undefined,
        },
      ),
    });
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('upon pressing proceed to authority button system invokes action and navigate to proceed page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.proceedToAuthorityButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'DOAL_SAVE_APPLICATION',
      requestTaskId: mockDoalApplicationSubmitRequestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'DOAL_SAVE_APPLICATION_PAYLOAD',
        doal: {
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
          determination: {
            type: 'PROCEED_TO_AUTHORITY',
          },
        },
        doalSectionsCompleted: {
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
          determination: false,
        },
      },
    });

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['proceed-authority', 'reason'], { relativeTo: activatedRoute });
  });

  it('upon pressing close button system invokes action and navigate to close page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.closeButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'DOAL_SAVE_APPLICATION',
      requestTaskId: mockDoalApplicationSubmitRequestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'DOAL_SAVE_APPLICATION_PAYLOAD',
        doal: {
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
          determination: {
            type: 'CLOSED',
          },
        },
        doalSectionsCompleted: {
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
          determination: false,
        },
      },
    });

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['close', 'reason'], { relativeTo: activatedRoute });
  });
});
