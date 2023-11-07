import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { mockStateRespond } from '@tasks/vir/comments-response/testing/mock-vir-application-respond-payload';
import { VirTaskCommentsResponseModule } from '@tasks/vir/comments-response/vir-task-comments-response.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService, VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload } from 'pmrv-api';

import { SendReportComponent } from './send-report.component';

describe('SendReportComponent', () => {
  let page: Page;
  let component: SendReportComponent;
  let store: CommonTasksStore;
  let fixture: ComponentFixture<SendReportComponent>;

  const currentItem = 'A1';
  const tasksService = mockClass(TasksService);
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub(
    { taskId: mockStateRespond.requestTaskItem.requestTask.id, id: currentItem },
    null,
    {
      reference: currentItem,
    },
  );

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
    component.isEditable$ = of(true);
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirTaskCommentsResponseModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('when it can be submitted', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateRespond);
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
      expect(page.heading1.textContent.trim()).toEqual('Submit response to A1');
      expect(page.paragraph.textContent.trim()).toEqual(
        'Are you sure you want to submit your response for this item to the Competent Authority for assessment?',
      );
      expect(page.submitButton.textContent.trim()).toEqual('Confirm and complete');
    });

    it('should submit request and set accordingly properties', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS',
        requestTaskId: mockStateRespond.requestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
          reference: 'A1',
          virRespondToRegulatorCommentsSectionsCompleted: (
            mockStateRespond.requestTaskItem.requestTask
              .payload as VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload
          ).virRespondToRegulatorCommentsSectionsCompleted,
        },
      });
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
      expect(page.heading1.textContent.trim()).toEqual('Response submitted');
      expect(page.panelBody.textContent.trim()).toEqual(
        'Your responses have been successfully submitted to the Competent Authority for assessmentYour reference code is: VIR00003-2023',
      );
      expect(page.heading3.textContent.trim()).toEqual('What happens next');
      expect(page.paragraph.textContent.trim()).toEqual(
        'Your response will be forwarded to a regulator at the Competent Authority for review.',
      );
      expect(page.submitButton).toBeFalsy();
    });
  });
});
