import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { ConservativeMethodComponent } from '@tasks/aer/verification-submit/data-gaps/conservative-method/conservative-method.component';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('ConservativeMethodComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: ConservativeMethodComponent;
  let fixture: ComponentFixture<ConservativeMethodComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ConservativeMethodComponent> {
    get radioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="conservativeMethodUsed"]');
    }
    get methodDetails() {
      return this.getInputValue('#methodDetails');
    }
    set methodDetails(value: string) {
      this.setInputValue('#methodDetails', value);
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
    fixture = TestBed.createComponent(ConservativeMethodComponent);
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

  describe('for new conservative method', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          methodologiesToCloseDataGaps: {
            dataGapRequired: true,
            dataGapRequiredDetails: {
              dataGapApproved: false,
            },
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

    it('should submit a valid form and navigate to material misstatement', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Please select yes or no']);

      page.radioButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual([
        'Please give more detail about why the method you used was not conservative',
      ]);

      page.methodDetails = 'Methods';
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
                dataGapApprovedDetails: {
                  conservativeMethodUsed: false,
                  methodDetails: 'Methods',
                },
              },
            },
          },
          { methodologiesToCloseDataGaps: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../material-misstatement'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing conservative method', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          methodologiesToCloseDataGaps: {
            dataGapRequired: true,
            dataGapRequiredDetails: {
              dataGapApproved: false,
              dataGapApprovedDetails: {
                conservativeMethodUsed: false,
                methodDetails: 'Methods',
              },
            },
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form and navigate to material misstatement', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith(['../material-misstatement'], { relativeTo: activatedRoute });
    });

    it('should edit, submit a valid form and navigate to material misstatement', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

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
                dataGapApproved: false,
                dataGapApprovedDetails: {
                  conservativeMethodUsed: true,
                  methodDetails: null,
                },
              },
            },
          },
          { methodologiesToCloseDataGaps: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../material-misstatement'], { relativeTo: activatedRoute });
    });
  });
});
