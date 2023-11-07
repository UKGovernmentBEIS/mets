import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { IdentifiedChangesItemComponent } from '@tasks/aer/verification-submit/summary-of-conditions/identified-changes-list/identified-changes-item.component';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import {
  mockState,
  mockVerificationApplyPayload,
} from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('IdentifiedChangesItemComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: IdentifiedChangesItemComponent;
  let fixture: ComponentFixture<IdentifiedChangesItemComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' });

  class Page extends BasePage<IdentifiedChangesItemComponent> {
    get explanationValue() {
      return this.getInputValue('#explanation');
    }
    set explanationValue(value: string) {
      this.setInputValue('#explanation', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(IdentifiedChangesItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new item', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          summaryOfConditions: {
            ...mockVerificationApplyPayload.verificationReport.summaryOfConditions,
            notReportedChanges: null,
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display an empty form', () => {
      expect(page.errorSummary).toBeFalsy();
    });

    it('should submit a valid form and navigate to list', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Please provide details of the change']);

      page.explanationValue = 'Explanation B1';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            summaryOfConditions: {
              ...mockVerificationApplyPayload.verificationReport.summaryOfConditions,
              notReportedChanges: [
                {
                  reference: 'B1',
                  explanation: 'Explanation B1',
                },
              ],
            },
          },
          { summaryOfConditions: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../../identified-changes-list'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing item', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form and navigate to list', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith(['../../identified-changes-list'], { relativeTo: activatedRoute });
    });

    it('should edit, submit a valid form and navigate to list', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.explanationValue).toEqual('Explanation B1');
      expect(page.errorSummary).toBeFalsy();

      page.explanationValue = 'New explanation B1';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            summaryOfConditions: {
              ...mockVerificationApplyPayload.verificationReport.summaryOfConditions,
              notReportedChanges: [
                {
                  reference: 'B1',
                  explanation: 'New explanation B1',
                },
              ],
            },
          },
          { summaryOfConditions: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../../identified-changes-list'], { relativeTo: activatedRoute });
    });
  });
});
