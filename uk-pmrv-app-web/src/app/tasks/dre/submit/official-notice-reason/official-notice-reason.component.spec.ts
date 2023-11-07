import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { DreTaskComponent } from '../../shared/components/dre-task/dre-task.component';
import { dreCompleted, mockCompletedDreApplicationSubmitRequestTaskItem, updateMockedDre } from '../../test/mock';
import { OfficialNoticeReasonComponent } from './official-notice-reason.component';

describe('OfficialNoticeReasonComponent', () => {
  let component: OfficialNoticeReasonComponent;
  let fixture: ComponentFixture<OfficialNoticeReasonComponent>;

  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<OfficialNoticeReasonComponent> {
    get officialNoticeReason() {
      return this.getInputValue('#officialNoticeReason');
    }
    set officialNoticeReason(value: string) {
      this.setInputValue('#officialNoticeReason', value);
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
    fixture = TestBed.createComponent(OfficialNoticeReasonComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OfficialNoticeReasonComponent, DreTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new dre', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockedDre({ officialNoticeReason: undefined }, false),
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Provide a reason that will be included in the official notice.']);

      page.officialNoticeReason = 'officialNoticeReason';

      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'DRE_SAVE_APPLICATION',
        requestTaskId: mockCompletedDreApplicationSubmitRequestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'DRE_SAVE_APPLICATION_PAYLOAD',
          dre: {
            ...dreCompleted,
            officialNoticeReason: 'officialNoticeReason',
          },
          sectionCompleted: false,
        },
      });
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..', 'monitoring-approaches'], { relativeTo: route });
    });
  });

  describe('for existing dre', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockedDre({}, false),
      });
    });
    beforeEach(createComponent);

    it('should submit updated form', () => {
      expect(page.errorSummary).toBeFalsy();
      page.officialNoticeReason = 'new off notice reason';

      fixture.detectChanges();

      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'DRE_SAVE_APPLICATION',
        requestTaskId: mockCompletedDreApplicationSubmitRequestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'DRE_SAVE_APPLICATION_PAYLOAD',
          dre: {
            ...dreCompleted,
            officialNoticeReason: 'new off notice reason',
          },
          sectionCompleted: false,
        },
      });
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..', 'monitoring-approaches'], { relativeTo: route });
    });
  });
});
