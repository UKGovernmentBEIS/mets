import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { mockNerSubmitStateBuild } from '@tasks/ner/test';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { AccountVerificationBodyService, TasksService } from 'pmrv-api';

import { NerSendReportComponent } from './send-report.component';

describe('SendReportComponent', () => {
  let component: NerSendReportComponent;
  let fixture: ComponentFixture<NerSendReportComponent>;
  let store: CommonTasksStore;
  let page: Page;
  let accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<NerSendReportComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-l').textContent.trim();
    }

    get paragraphsContent() {
      return this.queryAll('p[class="govuk-body"]').map((p) => p.textContent.trim());
    }

    get confirmationComponent() {
      return this.query<HTMLElement>('app-confirmation-shared');
    }

    get confirmationPanelHeading() {
      return this.confirmationComponent.querySelector<HTMLDivElement>('govuk-panel h1').textContent.trim();
    }

    get confirmationWhatHappensNextTemplate() {
      return this.confirmationComponent.querySelector<HTMLDivElement>('#whatHappensNextTemplate');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    accountVerificationBodyService = {
      getVerificationBodyOfAccount: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [NerSendReportComponent],
      providers: [
        CapitalizeFirstPipe,
        provideRouter([]),
        { provide: TasksService, useValue: tasksService },
        { provide: AccountVerificationBodyService, useValue: accountVerificationBodyService },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockNerSubmitStateBuild({
        nerSectionsCompleted: { details: false },
      }),
    );

    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of({ id: 210, name: 'Verifier' }));

    fixture = TestBed.createComponent(NerSendReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display right content for operator side, send to verifier', () => {
    expect(page.confirmationComponent).toBeFalsy();
    expect(page.heading).toEqual('Send application for verification');
    expect(page.paragraphsContent).toEqual([
      'Verifier',
      'By selecting ‘Confirm and send’ you confirm that the information in your application is correct to the best of your knowledge.',
    ]);
  });
});
