import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { mockState } from '@tasks/vir/submit/testing/mock-vir-application-submit-payload';
import { VirTaskSubmitModule } from '@tasks/vir/submit/vir-task-submit.module';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { SendReportComponent } from './send-report.component';

describe('SendReportComponent', () => {
  let page: Page;
  let component: SendReportComponent;
  let store: CommonTasksStore;
  let fixture: ComponentFixture<SendReportComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SendReportComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get heading3(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h3');
    }

    get paragraph(): HTMLParagraphElement {
      return this.query<HTMLParagraphElement>('p.govuk-body');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }

    get panelBody() {
      return this.query<HTMLElement>('div.govuk-panel__body');
    }
  }

  const initComponentState = () => {
    component.isSubmitted$ = new BehaviorSubject<boolean>(false);
    component.isSendReportAvailable$ = of(true);
    component.isEditable$ = of(true);
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirTaskSubmitModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('when it can be submitted', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
      fixture = TestBed.createComponent(SendReportComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.heading1.textContent.trim()).toEqual('Submit responses');
      expect(page.paragraph.textContent.trim()).toEqual(
        'Are you sure you want to submit this form to the Competent Authority for assessment?',
      );
      expect(page.submitButton.textContent.trim()).toEqual('Confirm and complete');
    });

    it('should submit request and set accordingly properties', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'VIR_SUBMIT_APPLICATION',
        requestTaskId: mockState.requestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        },
      });
    });
  });

  describe('when it cannot be submitted', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(SendReportComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      component.isSendReportAvailable$ = new BehaviorSubject(false);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.heading1.textContent.trim()).toEqual('Submit responses');
      expect(page.paragraph.textContent.trim()).toEqual('You need to complete all tasks before submitting the report.');
      expect(page.submitButton).toBeFalsy();
    });
  });

  describe('when it has been submitted', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(SendReportComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      component.isSubmitted$ = new BehaviorSubject(true);
      component.requestId$ = new BehaviorSubject('VIR00003-2023');
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.heading1.textContent.trim()).toEqual('Responses submitted');
      expect(page.panelBody.textContent.trim()).toEqual(
        'Your responses have been successfully submitted to the Competent Authority for assessmentYour reference code is: VIR00003-2023',
      );
      expect(page.heading3.textContent.trim()).toEqual('What happens next');
      expect(page.paragraph.textContent.trim()).toEqual(
        'We will review what you have told us and will reply to you in due course.',
      );
      expect(page.submitButton).toBeFalsy();
    });
  });
});
