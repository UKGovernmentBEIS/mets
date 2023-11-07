import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { ReferenceDocumentsComponent } from '@tasks/aer/verification-submit/materiality-level/reference-documents/reference-documents.component';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import {
  mockState,
  mockVerificationApplyPayload,
} from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('ReferenceDocumentsComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: ReferenceDocumentsComponent;
  let fixture: ComponentFixture<ReferenceDocumentsComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ReferenceDocumentsComponent> {
    get UK_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_REGULATION_EU_2018_2067() {
      return this.query<HTMLInputElement>('#ukEtsVerificationConductAccreditedVerifiers-1');
    }
    get EU_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_AND_PROVIDERS_3000() {
      return this.query<HTMLInputElement>('#euEtsVerificationConductAccreditedVerifiersAndAssuranceProviders-0');
    }
    get OTHER() {
      return this.query<HTMLInputElement>('#otherReferences-0');
    }
    get otherReference() {
      return this.getInputValue('#otherReference');
    }
    set otherReference(value: string) {
      this.setInputValue('#otherReference', value);
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
    fixture = TestBed.createComponent(ReferenceDocumentsComponent);
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

  describe('for new reference documents details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          materialityLevel: {
            ...mockVerificationApplyPayload.verificationReport.materialityLevel,
            accreditationReferenceDocumentTypes: null,
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
      expect(page.errorSummaryList).toEqual(['Please select at least one reference document from the list']);

      page.UK_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_REGULATION_EU_2018_2067.click();
      page.EU_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_AND_PROVIDERS_3000.click();
      page.OTHER.click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Enter details']);

      page.otherReference = 'Other type';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            materialityLevel: {
              ...mockVerificationApplyPayload.verificationReport.materialityLevel,
              accreditationReferenceDocumentTypes: [
                'UK_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_REGULATION_EU_2018_2067',
                'EU_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_AND_PROVIDERS_3000',
                'OTHER',
              ],
              otherReference: 'Other type',
            },
          },
          { materialityLevel: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing reference documents details', () => {
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
      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRoute });
    });

    it('should edit, submit a valid form and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.UK_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_REGULATION_EU_2018_2067.click();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            materialityLevel: {
              ...mockVerificationApplyPayload.verificationReport.materialityLevel,
              accreditationReferenceDocumentTypes: [
                'UK_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_REGULATION_EU_2018_2067',
                'EU_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_EA_6_03',
              ],
              otherReference: null,
            },
          },
          { materialityLevel: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRoute });
    });
  });
});
