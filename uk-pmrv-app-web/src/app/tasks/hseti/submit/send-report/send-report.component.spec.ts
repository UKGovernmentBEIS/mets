import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { HseTiTaskSharedModule } from '@tasks/hseti/shared/hseti-task-shared.module';
import { mockStateBuild } from '@tasks/hseti/test/mock';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { SendReportComponent } from './send-report.component';

describe('SendReportComponent', () => {
  let page: Page;
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  let router: Router;
  let component: SendReportComponent;
  let store: CommonTasksStore;
  let fixture: ComponentFixture<SendReportComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1' });

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
      imports: [SharedModule, TaskSharedModule, HseTiTaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('when it can be submitted', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild());
      fixture = TestBed.createComponent(SendReportComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      router = TestBed.inject(Router);
      initComponentState();
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.heading1.textContent.trim()).toEqual('Send to regulator');
      expect(page.paragraph.textContent.trim()).toEqual(
        'Your application will be sent directly to your regulator (Environment Agency).',
      );
      expect(page.submitButton.textContent.trim()).toEqual('Comfirm and send');
    });

    it('should submit request and set accordingly properties', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'HSE_TI_SUBMIT_TO_REGULATOR',
        requestTaskId: mockStateBuild().requestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        },
      });
    });
  });

  describe('when it cannot be submitted', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild());
      fixture = TestBed.createComponent(SendReportComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      router = TestBed.inject(Router);
      initComponentState();
      component.isSendReportAvailable$ = new BehaviorSubject(false);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });
  });

  describe('when it has been submitted', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          hseti: {},
        }),
      );
      fixture = TestBed.createComponent(SendReportComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      component.isSubmitted$ = new BehaviorSubject(true);
      component.requestId$ = new BehaviorSubject('HSE00001-2021_2025');
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.heading1.textContent.trim()).toEqual('2021-2025 HSE target increase submitted');
      expect(page.panelBody.textContent.trim()).toEqual('Your reference code is: HSE00001-2021_2025');
      expect(page.heading3.textContent.trim()).toEqual('What happens next');
      expect(page.paragraph.textContent.trim()).toEqual(
        'The regulator will review your report and will contact you if necessary.',
      );
      expect(page.submitButton).toBeFalsy();
    });
  });
});
