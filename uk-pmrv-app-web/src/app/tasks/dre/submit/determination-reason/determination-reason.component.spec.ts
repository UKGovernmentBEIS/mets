import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { asyncData, BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { DreTaskComponent } from '../../shared/components/dre-task/dre-task.component';
import { dreCompleted, mockCompletedDreApplicationSubmitRequestTaskItem, updateMockedDre } from '../../test/mock';
import { DeterminationReasonComponent } from './determination-reason.component';

describe('DeterminationReasonComponent', () => {
  let component: DeterminationReasonComponent;
  let fixture: ComponentFixture<DeterminationReasonComponent>;

  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);

  class Page extends BasePage<DeterminationReasonComponent> {
    get typeRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="type"]');
    }

    get operatorAskedToResubmitRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="operatorAskedToResubmit"]');
    }

    get regulatorComments() {
      return this.getInputValue('#regulatorComments');
    }
    set regulatorComments(value: string) {
      this.setInputValue('#regulatorComments', value);
    }

    set supportingDocumentsValue(value: File[]) {
      this.setInputValue('input[type="file"]', value);
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
    fixture = TestBed.createComponent(DeterminationReasonComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DeterminationReasonComponent, DreTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
    }).compileComponents();
  });

  describe('for new dre', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockCompletedDreApplicationSubmitRequestTaskItem,
          requestTask: {
            ...mockCompletedDreApplicationSubmitRequestTaskItem.requestTask,
            payload: undefined,
          },
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.operatorAskedToResubmitRadios[1].checked).toBeTruthy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Say why you are determining the reportable emissions.']);

      page.typeRadios[1].click();
      page.operatorAskedToResubmitRadios[0].click();
      page.regulatorComments = 'Regulator comments';

      attachmentService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: '2b587c89-1973-42ba-9682-b3ea5453b9dd' } })),
      );
      page.supportingDocumentsValue = [new File(['fileBytes'], 'file1.txt')];
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
            determinationReason: {
              type: 'CORRECTING_NON_MATERIAL_MISSTATEMENT',
              regulatorComments: 'Regulator comments',
              operatorAskedToResubmit: true,
              supportingDocuments: ['2b587c89-1973-42ba-9682-b3ea5453b9dd'],
            },
          },
          sectionCompleted: false,
        },
      });
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..', 'official-notice-reason'], { relativeTo: route });
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
      expect(page.typeRadios[0].checked).toBeTruthy();
      expect(page.operatorAskedToResubmitRadios[0].checked).toBeTruthy();

      page.typeRadios[1].click();
      page.operatorAskedToResubmitRadios[1].click();
      page.regulatorComments = 'Regulator comments updated';

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
            determinationReason: {
              type: 'CORRECTING_NON_MATERIAL_MISSTATEMENT',
              regulatorComments: 'Regulator comments updated',
              operatorAskedToResubmit: false,
              supportingDocuments: ['2b587c89-1973-42ba-9682-b3ea5453b9dd'],
            },
          },
          sectionCompleted: false,
        },
      });
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..', 'official-notice-reason'], { relativeTo: route });
    });
  });
});
