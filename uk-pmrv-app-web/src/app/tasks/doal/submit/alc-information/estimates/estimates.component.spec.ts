import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { DoalTaskComponent } from '@tasks/doal/shared/components/doal-task/doal-task.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import {
  mockDoalApplicationSubmitRequestTaskItem,
  updateMockDoalApplicationSubmitRequestTaskItem,
} from '../../../test/mock';
import { EstimatesComponent } from './estimates.component';

describe('EstimatesComponent', () => {
  let component: EstimatesComponent;
  let fixture: ComponentFixture<EstimatesComponent>;

  let page: Page;

  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {
      taskId: `${mockDoalApplicationSubmitRequestTaskItem.requestTask.id}`,
    },
    null,
    { sectionKey: 'activityLevelChangeInformation' },
  );

  class Page extends BasePage<EstimatesComponent> {
    get areConservativeEstimatesRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="areConservativeEstimates"]');
    }

    get explainEstimatesTextArea() {
      return this.query<HTMLInputElement>('textarea[name$="explainEstimates"]');
    }

    get explainEstimates(): string {
      return this.getInputValue('#explainEstimates');
    }
    set explainEstimates(value: string) {
      this.setInputValue('#explainEstimates', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(EstimatesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EstimatesComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new estimates', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
            activityLevelChangeInformation: {
              activityLevels: [
                {
                  year: 2025,
                  subInstallationName: 'ADIPIC_ACID',
                  changeType: 'INCREASE',
                  changedActivityLevel: 'changedActivityLevel',
                  comments: 'activityLevel1Comment',
                },
              ],
            },
          },
          {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            activityLevelChangeInformation: false,
          },
        ),
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.explainEstimatesTextArea).toBeDisabled();

      page.areConservativeEstimatesRadios[0].click();
      fixture.detectChanges();
      expect(page.explainEstimatesTextArea).not.toBeDisabled();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Enter a comment']);

      page.explainEstimates = 'A comment';

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'DOAL_SAVE_APPLICATION',
        requestTaskId: mockDoalApplicationSubmitRequestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'DOAL_SAVE_APPLICATION_PAYLOAD',
          doal: {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
            activityLevelChangeInformation: {
              activityLevels: [
                {
                  year: 2025,
                  subInstallationName: 'ADIPIC_ACID',
                  changeType: 'INCREASE',
                  changedActivityLevel: 'changedActivityLevel',
                  comments: 'activityLevel1Comment',
                },
              ],
              areConservativeEstimates: true,
              explainEstimates: 'A comment',
            },
          },
          doalSectionsCompleted: {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            activityLevelChangeInformation: false,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../', 'preliminary-allocations'], { relativeTo: activatedRoute });
    });
  });

  describe('for editing estimates', () => {
    beforeEach(() => {
      const route = new ActivatedRouteStub(
        {
          taskId: `${mockDoalApplicationSubmitRequestTaskItem.requestTask.id}`,
          index: '0',
        },
        null,
        { sectionKey: 'activityLevelChangeInformation' },
      );
      TestBed.overrideProvider(ActivatedRoute, { useValue: route });
    });

    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
            activityLevelChangeInformation: {
              activityLevels: [
                {
                  year: 2025,
                  subInstallationName: 'ADIPIC_ACID',
                  changeType: 'INCREASE',
                  changedActivityLevel: 'changedActivityLevel',
                  comments: 'activityLevel1Comment',
                },
              ],
              areConservativeEstimates: true,
              explainEstimates: 'A comment',
            },
          },
          {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            activityLevelChangeInformation: true,
          },
        ),
      });
    });
    beforeEach(createComponent);

    it('should submit updated data', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.areConservativeEstimatesRadios[0].checked).toBeTruthy();
      expect(page.explainEstimates).toEqual('A comment');

      page.areConservativeEstimatesRadios[1].click();
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'DOAL_SAVE_APPLICATION',
        requestTaskId: mockDoalApplicationSubmitRequestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'DOAL_SAVE_APPLICATION_PAYLOAD',
          doal: {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
            activityLevelChangeInformation: {
              activityLevels: [
                {
                  year: 2025,
                  subInstallationName: 'ADIPIC_ACID',
                  changeType: 'INCREASE',
                  changedActivityLevel: 'changedActivityLevel',
                  comments: 'activityLevel1Comment',
                },
              ],
              areConservativeEstimates: false,
              explainEstimates: null,
            },
          },
          doalSectionsCompleted: {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            activityLevelChangeInformation: false,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../', 'preliminary-allocations'], { relativeTo: activatedRoute });
    });
  });
});
