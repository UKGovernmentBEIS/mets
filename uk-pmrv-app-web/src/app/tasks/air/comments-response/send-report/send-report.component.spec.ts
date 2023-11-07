import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of } from 'rxjs';

import { AirTaskCommentsResponseModule } from '@tasks/air/comments-response/air-task-comments-response.module';
import {
  mockAirApplicationRespondPayload,
  mockStateRespond,
} from '@tasks/air/comments-response/testing/mock-air-application-respond-payload';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AirImprovement, AirSubmitRespondToRegulatorCommentsRequestTaskActionPayload, TasksService } from 'pmrv-api';

import { SendReportComponent } from './send-report.component';

describe('SendReportComponent', () => {
  let page: Page;
  let component: SendReportComponent;
  let store: CommonTasksStore;
  let fixture: ComponentFixture<SendReportComponent>;

  const reference = '1';
  const currentItem: AirImprovement = mockAirApplicationRespondPayload.airImprovements[reference];
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub(
    { taskId: mockStateRespond.requestTaskItem.requestTask.id, id: reference },
    null,
    {
      airImprovement: currentItem,
    },
  );
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SendReportComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
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
      imports: [AirTaskCommentsResponseModule, RouterTestingModule],
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
      expect(page.heading1.textContent.trim()).toEqual('Item 1: Send response to regulator');
      expect(page.paragraph.textContent.trim()).toEqual(
        'Are you sure you want to send your response to this item to the regulator?',
      );
      expect(page.submitButton.textContent.trim()).toEqual('Confirm and complete');
    });

    it('should submit request and set accordingly properties', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'AIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS',
        requestTaskId: mockStateRespond.requestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'AIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
          reference: reference,
          airRespondToRegulatorCommentsSectionsCompleted: (
            mockStateRespond.requestTaskItem.requestTask
              .payload as AirSubmitRespondToRegulatorCommentsRequestTaskActionPayload
          ).airRespondToRegulatorCommentsSectionsCompleted,
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
      component.requestId$ = new BehaviorSubject('AIR00003-2023');
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.heading1.textContent.trim()).toEqual('Response submitted');
      expect(page.panelBody.textContent.trim()).toEqual(
        'Response sent successfullyYour reference code is: AIR00003-2023',
      );
      expect(page.submitButton).toBeFalsy();
    });
  });
});
