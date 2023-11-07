import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage, mockClass } from '@testing';

import { AccountVerificationBodyService, TasksService } from 'pmrv-api';

import { mockState } from '../testing/mock-state';
import { ConfirmationVerifierComponent } from './confirmation-verifier.component';

describe('ConfirmationVerifierComponent', () => {
  let component: ConfirmationVerifierComponent;
  let fixture: ComponentFixture<ConfirmationVerifierComponent>;

  let accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>>;
  let page: Page;
  let store: RequestTaskStore;
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ConfirmationVerifierComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-l');
    }
    get paragraphsContent() {
      return this.queryAll('p').map((p) => p.textContent.trim());
    }
    get confirmationTitle() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get confirmationPanelContent() {
      return this.query<HTMLElement>('.govuk-panel__body');
    }
  }

  beforeEach(async () => {
    accountVerificationBodyService = {
      getVerificationBodyOfAccount: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: AccountVerificationBodyService, useValue: accountVerificationBodyService },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);
    store.setState(mockState);
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of({ id: 210, name: 'Verifier' }));
    fixture = TestBed.createComponent(ConfirmationVerifierComponent);
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
    expect(page.confirmationTitle.textContent.trim()).toEqual('Report sent for verification');
    expect(page.confirmationPanelContent.textContent.trim()).toEqual('Your reference is AEM00016-2022');
    expect(page.paragraphsContent).toEqual([
      'Your report has been sent to Verifier. You can recall your report at any time before Verifier returns it to you.',
      'Verifier will return the report to you once they have added an opinion statement. You will then be able to submit your report to the Environment Agency.',
    ]);
  });
});
