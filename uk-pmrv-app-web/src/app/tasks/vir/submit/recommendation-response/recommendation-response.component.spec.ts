import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { noDate, noRecommendation, noSelection } from '@tasks/vir/errors/validation-errors';
import { RecommendationResponseComponent } from '@tasks/vir/submit/recommendation-response/recommendation-response.component';
import { mockPostBuild, mockStateBuild } from '@tasks/vir/submit/testing/mock-state';
import {
  mockState,
  mockVirApplicationSubmitPayload,
} from '@tasks/vir/submit/testing/mock-vir-application-submit-payload';
import { VirTaskSubmitModule } from '@tasks/vir/submit/vir-task-submit.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService, UncorrectedItem } from 'pmrv-api';

describe('RecommendationResponseComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: RecommendationResponseComponent;
  let fixture: ComponentFixture<RecommendationResponseComponent>;

  const currentItem: UncorrectedItem = mockVirApplicationSubmitPayload.verificationData.uncorrectedNonConformities.B1;
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub(
    { taskId: mockState.requestTaskItem.requestTask.id, id: currentItem.reference },
    null,
    {
      verificationDataItem: currentItem,
    },
  );
  const tasksService = mockClass(TasksService);
  const expectedDate = new Date('2023-12-01');
  const expectedNextRoute = '../../B1/upload-evidence-question';

  class Page extends BasePage<RecommendationResponseComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get verificationItem() {
      return this.query('app-verification-recommendation-item');
    }

    get isAddressedButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="isAddressed"]');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    set addressedDate(date: Date) {
      this.setInputValue(`#addressedDate-day`, date.getDate());
      this.setInputValue(`#addressedDate-month`, date.getMonth() + 1);
      this.setInputValue(`#addressedDate-year`, date.getFullYear());
    }

    set addressedDescription(value: string) {
      this.setInputValue('#addressedDescription', value);
    }

    set addressedDescription2(value: string) {
      this.setInputValue('#addressedDescription2', value);
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(RecommendationResponseComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirTaskSubmitModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for new recommendation response details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          operatorImprovementResponses: {},
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
      expect(page.heading1.textContent.trim()).toEqual('Respond to B1');
      expect(page.verificationItem).toBeTruthy();
      expect(page.isAddressedButtons).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noSelection]);
      expect(page.errorSummaryListContents.length).toEqual(1);
    });

    it('should submit a valid form and navigate to `upload-evidence-question` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.isAddressedButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noRecommendation, noDate]);

      page.isAddressedButtons[0].click();
      page.addressedDescription = 'Test description B1';
      page.addressedDate = expectedDate;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            operatorImprovementResponses: {
              B1: {
                addressedDate: expectedDate,
                addressedDescription: 'Test description B1',
                isAddressed: true,
              },
            },
          },
          { B1: false },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });

  describe('for existing recommendation response details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Respond to B1');
      expect(page.verificationItem).toBeTruthy();
      expect(page.isAddressedButtons).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should submit a valid form and navigate to `upload-evidence-question` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });

    it('should edit, submit a valid form and navigate to `upload-evidence-question` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.isAddressedButtons[1].click();
      page.addressedDescription2 = 'Test description B1, when no';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            operatorImprovementResponses: {
              ...mockVirApplicationSubmitPayload.operatorImprovementResponses,
              [currentItem.reference]: {
                ...mockVirApplicationSubmitPayload.operatorImprovementResponses.B1,
                isAddressed: false,
                addressedDescription: 'Test description B1, when no',
                addressedDate: null,
              },
            },
          },
          { B1: false },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });
});
