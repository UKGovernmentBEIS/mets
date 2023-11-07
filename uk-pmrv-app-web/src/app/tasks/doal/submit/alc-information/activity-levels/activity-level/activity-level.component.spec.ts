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

import { DoalTaskComponent } from '../../../../shared/components/doal-task/doal-task.component';
import {
  mockDoalApplicationSubmitRequestTaskItem,
  updateMockDoalApplicationSubmitRequestTaskItem,
} from '../../../../test/mock';
import { ActivityLevelComponent } from './activity-level.component';

describe('ActivityLevelComponent', () => {
  let component: ActivityLevelComponent;
  let fixture: ComponentFixture<ActivityLevelComponent>;

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

  class Page extends BasePage<ActivityLevelComponent> {
    get year(): string {
      return this.getInputValue('#year');
    }
    set year(value: string) {
      this.setInputValue('#year', value);
    }
    get yearSelect(): HTMLSelectElement {
      return this.query('select[name="year"]');
    }
    get yearOptions(): string[] {
      return Array.from(this.yearSelect.options).map((option) => option.textContent.trim());
    }

    get subInstallationName(): string {
      return this.getInputValue('#subInstallationName');
    }
    set subInstallationName(value: string) {
      this.setInputValue('#subInstallationName', value);
    }

    get changeType(): string {
      return this.getInputValue('#changeType');
    }
    set changeType(value: string) {
      this.setInputValue('#changeType', value);
    }

    get otherChangeTypeName(): string {
      return this.getInputValue('#otherChangeTypeName');
    }
    set otherChangeTypeName(value: string) {
      this.setInputValue('#otherChangeTypeName', value);
    }

    get changedActivityLevel(): string {
      return this.getInputValue('#changedActivityLevel');
    }
    set changedActivityLevel(value: string) {
      this.setInputValue('#changedActivityLevel', value);
    }

    get comments(): string {
      return this.getInputValue('#comments');
    }
    set comments(value: string) {
      this.setInputValue('#comments', value);
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
    fixture = TestBed.createComponent(ActivityLevelComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActivityLevelComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new activity level', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
            activityLevelChangeInformation: undefined,
          },
          {},
        ),
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display years', () => {
      expect(page.yearOptions).toEqual([
        '2021',
        '2022',
        '2023',
        '2024',
        '2025',
        '2026',
        '2027',
        '2028',
        '2029',
        '2030',
      ]);
    });

    it('should submit', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.year = '2025';
      page.subInstallationName = 'EAF_CARBON_STEEL';
      page.changeType = 'OTHER';
      page.otherChangeTypeName = 'other change type';
      page.changedActivityLevel = '10%';
      page.comments = 'some comment';

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
                  year: '2025',
                  subInstallationName: 'EAF_CARBON_STEEL',
                  changeType: 'OTHER',
                  changedActivityLevel: '10%',
                  otherChangeTypeName: 'other change type',
                  comments: 'some comment',
                },
              ],
            },
          },
          doalSectionsCompleted: {
            activityLevelChangeInformation: false,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute });
    });
  });

  describe('for editing activity level', () => {
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

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should select values and submit updated data', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.year).toEqual(2025);

      page.year = '2026';

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
                  year: '2026',
                  subInstallationName: 'ADIPIC_ACID',
                  changeType: 'INCREASE',
                  changedActivityLevel: 'changedActivityLevel',
                  comments: 'activityLevel1Comment',
                },
              ],
            },
          },
          doalSectionsCompleted: {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            activityLevelChangeInformation: false,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute });
    });
  });
});
