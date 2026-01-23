import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { mockStateBuild } from '../../testing/mock-state';
import { BdrS2SendVerifierOrRegulatorComponent } from './send-verifier-or-regulator.component';

describe('SendVerifierOrRegulatorComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: BdrS2SendVerifierOrRegulatorComponent;
  let fixture: ComponentFixture<BdrS2SendVerifierOrRegulatorComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1' });

  class Page extends BasePage<BdrS2SendVerifierOrRegulatorComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get needsVerification() {
      return this.query<HTMLInputElement>('#needsVerification-option0');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(BdrS2SendVerifierOrRegulatorComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for send to verifier', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          bdrs2: {
            bdrs2guardQuestions: {
              applicationWithdrawalReason: undefined,
              continueApplicationForFreeAllocationType: 'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT',
              covidAdjustments: true,
              inEiteSector: true,
              requiresAdditionalSubInstallationSplitsForCbam: true,
            },
            bdrs2Files: { file: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5' },
            mmpFiles: { file: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5' },
          },
        }),
      );
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Submit your report');
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual(['Select yes if you want to send this report to a verifier']);
      expect(page.errorSummaryListContents.length).toEqual(1);
    });

    it('should navigate to next page', async () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.needsVerification.click();

      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(navigateSpy).toHaveBeenCalledWith(['verifier'], {
        relativeTo: activatedRoute,
        queryParams: { sendTo: 'verifier' },
      });
    });
  });
});
