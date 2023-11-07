import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { mockState } from '@aviation/request-task/aer/ukets/tasks/send-report/testing/mock-state';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import SendReportToOperatorConfirmationComponent from './send-report-to-operator-confirmation.component';

describe('SendReportToOperatorConfirmationComponent', () => {
  let fixture: ComponentFixture<SendReportToOperatorConfirmationComponent>;
  let page: Page;
  let store: RequestTaskStore;
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SendReportToOperatorConfirmationComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-l');
    }
    get confirmationTitle() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get confirmationPanelContent() {
      return this.query<HTMLElement>('.govuk-panel__body');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SendReportToOperatorConfirmationComponent, RouterTestingModule],
      providers: [
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    store.setState(mockState);

    fixture = TestBed.createComponent(SendReportToOperatorConfirmationComponent);
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should show content', () => {
    expect(page.confirmationTitle).toBeTruthy();
    expect(page.confirmationTitle.textContent.trim()).toEqual('Verification report sent to operator');
    expect(page.confirmationPanelContent.textContent.trim()).toEqual('Your reference code is: AEM00016-2022');
  });
});
