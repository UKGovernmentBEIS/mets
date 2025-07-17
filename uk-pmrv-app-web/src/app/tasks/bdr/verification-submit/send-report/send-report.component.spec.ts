import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of } from 'rxjs';

import { BdrTaskSharedModule } from '@tasks/bdr/shared';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { mockState } from '../testing/mock-bdr-payload';
import { SendBdrReportComponent } from './send-report.component';

describe('SendBbrReportComponent', () => {
  let page: Page;
  let component: SendBdrReportComponent;
  let store: CommonTasksStore;
  let fixture: ComponentFixture<SendBdrReportComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SendBdrReportComponent> {
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
    component.isSendReportAvailable = signal(true);
    component.isEditable = signal(true);
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BdrTaskSharedModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('when it can be submitted', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
      fixture = TestBed.createComponent(SendBdrReportComponent);
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
        'By selecting ‘confirm and send’ you are confirming that the information you have provided in this report is correct to the best of your knowledge.',
      );
      expect(page.submitButton.textContent.trim()).toEqual('Confirm and send');
    });

    it('should submit request and set accordingly properties', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'BDR_SUBMIT_VERIFICATION',
        requestTaskId: mockState.requestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        },
      });
    });
  });

  describe('when it has been submitted', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(SendBdrReportComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      component.isSubmitted$ = new BehaviorSubject(true);
      component.requestId$ = new BehaviorSubject('BDR00180-2025');
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.heading1.textContent.trim()).toEqual('Verification report sent to operator');
      expect(page.panelBody.textContent.trim()).toEqual('Your reference number  BDR00180-2025');
      expect(page.heading3.textContent.trim()).toEqual('What happens next');
      expect(page.paragraph.textContent.trim()).toEqual('The operator can either:');
      expect(page.listContent).toEqual([
        'submit the report to the regulator',
        'make changes and repeat the submission process',
      ]);
      expect(page.submitButton).toBeFalsy();
    });
  });
});
