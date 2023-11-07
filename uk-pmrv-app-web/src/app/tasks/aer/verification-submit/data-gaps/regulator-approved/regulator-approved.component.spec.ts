import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { RegulatorApprovedComponent } from '@tasks/aer/verification-submit/data-gaps/regulator-approved/regulator-approved.component';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('RegulatorApprovedComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: RegulatorApprovedComponent;
  let fixture: ComponentFixture<RegulatorApprovedComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<RegulatorApprovedComponent> {
    get radioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="dataGapApproved"]');
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
    fixture = TestBed.createComponent(RegulatorApprovedComponent);
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
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new regulator approved', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          methodologiesToCloseDataGaps: {
            dataGapRequired: true,
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

    it('should submit a valid form and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Please select yes or no']);

      page.radioButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            methodologiesToCloseDataGaps: {
              dataGapRequired: true,
              dataGapRequiredDetails: {
                dataGapApproved: true,
                dataGapApprovedDetails: null,
              },
            },
          },
          { methodologiesToCloseDataGaps: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing regulator approved', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          methodologiesToCloseDataGaps: {
            dataGapRequired: true,
            dataGapRequiredDetails: {
              dataGapApproved: true,
            },
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRoute });
    });

    it('should edit, submit a valid form and navigate to conservative method', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.radioButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            methodologiesToCloseDataGaps: {
              dataGapRequired: true,
              dataGapRequiredDetails: {
                dataGapApproved: false,
              },
            },
          },
          { methodologiesToCloseDataGaps: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../conservative-method'], { relativeTo: activatedRoute });
    });
  });
});
