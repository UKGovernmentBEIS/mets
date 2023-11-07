import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { MaterialityLevelComponent } from '@tasks/aer/verification-submit/materiality-level/materiality-level.component';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import {
  mockState,
  mockVerificationApplyPayload,
} from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('MaterialityLevelComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: MaterialityLevelComponent;
  let fixture: ComponentFixture<MaterialityLevelComponent>;

  const tasksService = mockClass(TasksService);
  const materialityLevelInfo = {
    materialityLevel: {
      materialityDetails: 'Materiality details 2',
    },
  };

  class Page extends BasePage<MaterialityLevelComponent> {
    get materialityDetails() {
      return this.getInputValue('#materialityDetails');
    }
    set materialityDetails(value: string) {
      this.setInputValue('#materialityDetails', value);
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
    fixture = TestBed.createComponent(MaterialityLevelComponent);
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

  describe('for new materiality details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          materialityLevel: null,
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
      expect(page.errorSummaryList).toEqual(['Please give details about the materiality level']);

      const verificationReport = mockVerificationApplyPayload.verificationReport;
      page.materialityDetails = verificationReport.materialityLevel.materialityDetails;

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            materialityLevel: {
              materialityDetails: 'Materiality details',
            },
          },
          { materialityLevel: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['reference-documents'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing materiality details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
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
      expect(navigateSpy).toHaveBeenCalledWith(['reference-documents'], { relativeTo: activatedRoute });
    });

    it('should edit, submit a valid form and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.materialityDetails = materialityLevelInfo.materialityLevel.materialityDetails;

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            materialityLevel: {
              ...mockVerificationApplyPayload.verificationReport.materialityLevel,
              materialityDetails: materialityLevelInfo.materialityLevel.materialityDetails,
            },
          },
          { materialityLevel: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['reference-documents'], { relativeTo: activatedRoute });
    });
  });
});
