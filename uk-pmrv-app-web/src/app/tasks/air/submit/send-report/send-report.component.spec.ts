import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of } from 'rxjs';

import { AirTaskSubmitModule } from '@tasks/air/submit/air-task-submit.module';
import { mockState } from '@tasks/air/submit/testing/mock-air-application-submit-payload';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
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
      imports: [AirTaskSubmitModule, RouterTestingModule],
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
      expect(page.heading1.textContent.trim()).toEqual('Send report to the regulator');
      expect(page.paragraph.textContent.trim()).toEqual(
        'Are you sure you want to submit your 2022 Annual improvement report to your regulator?',
      );
      expect(page.submitButton.textContent.trim()).toEqual('Submit');
    });

    it('should submit request and set accordingly properties', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'AIR_SUBMIT_APPLICATION',
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
      expect(page.heading1.textContent.trim()).toEqual('Send report to the regulator');
      expect(page.paragraph.textContent.trim()).toEqual(
        'All tasks must be completed before you can submit your report.',
      );
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
      component.requestId$ = new BehaviorSubject('AIR00003-2023');
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.heading1.textContent.trim()).toEqual('Annual improvement report submitted');
      expect(page.panelBody.textContent.trim()).toEqual('Your reference code is: AIR00003-2023');
      expect(page.heading3.textContent.trim()).toEqual('What happens next');
      expect(page.paragraph.textContent.trim()).toEqual(
        'The regulator will review your report and will contact you if necessary.',
      );
      expect(page.submitButton).toBeFalsy();
    });
  });
});
