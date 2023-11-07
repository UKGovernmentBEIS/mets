import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { mockState } from '@aviation/request-task/aer/ukets/tasks/send-report/testing/mock-state';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { CompleteReportConfirmationComponent } from './complete-report-confirmation.component';

describe('CompleteReportConfirmationComponent', () => {
  let component: CompleteReportConfirmationComponent;
  let fixture: ComponentFixture<CompleteReportConfirmationComponent>;
  let page: Page;
  let store: RequestTaskStore;
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<CompleteReportConfirmationComponent> {
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
      imports: [RouterTestingModule],
      providers: [
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },

        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(CompleteReportConfirmationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show content', () => {
    expect(page.confirmationTitle).toBeTruthy();
    expect(page.confirmationTitle.textContent.trim()).toEqual('Emissions report completed');
    expect(page.confirmationPanelContent.textContent.trim()).toEqual('Your reference is AEM00016-2022');
  });
});
