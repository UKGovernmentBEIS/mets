import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared/bdrs2-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { mockPostBuild, mockStateBuild } from '../../../bdrs2/submit/testing/mock-state';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1' });

  class Page extends BasePage<DetailsComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get radioButtons() {
      return this.queryAll<HTMLInputElement>('.govuk-radios__input');
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
    fixture = TestBed.createComponent(DetailsComponent);
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
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new free allocation details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          bdrs2: {
            bdrs2guardQuestions: {
              continueApplicationForFreeAllocationType: 'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT',
              applicationWithdrawalReason: undefined,
            },
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
      expect(page.heading1.textContent.trim()).toEqual('Details about your installation');
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([
        'Select yes if you made COVID adjustments that you want to be excluded from your HAL calculation',
        'Select yes if your installation is in the aluminium, cement, fertiliser, hydrogen, iron or steel sector',
      ]);
      expect(page.errorSummaryListContents.length).toEqual(2);
    });

    it('should submit a valid form and navigate to next page', async () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.radioButtons[0].click();
      page.radioButtons[2].click();

      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            bdrs2: {
              bdrs2guardQuestions: {
                applicationWithdrawalReason: undefined,
                continueApplicationForFreeAllocationType: 'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT',
                covidAdjustments: true,
                inEiteSector: true,
              },
            },
          },
          {
            baseline: false,
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../cbam'], { relativeTo: activatedRoute });
    });
  });
});
