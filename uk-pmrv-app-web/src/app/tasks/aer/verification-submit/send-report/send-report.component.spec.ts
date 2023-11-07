import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { mockState } from '@tasks/aer/submit/testing/mock-aer-apply-action';
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

    get actionLegend(): HTMLSpanElement {
      return this.query<HTMLSpanElement>('span.govuk-caption-l');
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

    get listContent(): string[] {
      return this.queryAll<HTMLDListElement>('ul li').map((li) => li.textContent.trim());
    }
  }

  const initComponentState = () => {
    component.isSubmitted$ = new BehaviorSubject<boolean>(false);
    component.isSendReportAvailable$ = of(true);
    component.isEditable$ = of(true);
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
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
      expect(page.heading1.textContent.trim()).toEqual('Send report to operator');
      expect(page.paragraph.textContent.trim()).toEqual(
        'By selecting ‘confirm and complete’ you are confirming that the information you have provided in this verification report is correct to the best of your knowledge.',
      );
      expect(page.submitButton.textContent.trim()).toEqual('Confirm and complete');
    });

    it('should submit request and set accordingly properties', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'AER_SUBMIT_APPLICATION_VERIFICATION',
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
      expect(page.heading1.textContent.trim()).toEqual('Send report to operator');
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
      component.requestId$ = new BehaviorSubject('AEM00003-2023');
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.heading1.textContent.trim()).toEqual('Report sent to operator');
      expect(page.panelBody.textContent.trim()).toEqual('Your reference is AEM00003-2023');
      expect(page.actionLegend.textContent.trim()).toEqual('We have sent you a confirmation email.');
      expect(page.heading3.textContent.trim()).toEqual('What happens next');
      expect(page.paragraph.textContent.trim()).toEqual('The operator will either:');
      expect(page.listContent).toEqual([
        'submit the report to the regulator',
        'make changes and repeat the submission process',
      ]);
      expect(page.submitButton).toBeFalsy();
    });
  });
});
