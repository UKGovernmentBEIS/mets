import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import {
  mockState,
  mockVerificationApplyPayload,
} from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { VerifierDetailsComponent } from '@tasks/aer/verification-submit/verifier-details/verifier-details.component';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('VerifierDetailsComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: VerifierDetailsComponent;
  let fixture: ComponentFixture<VerifierDetailsComponent>;

  const tasksService = mockClass(TasksService);
  const verifierDetails = {
    verificationTeamDetails: {
      leadEtsAuditor: 'Lead ETS Auditor2',
      etsAuditors: 'ETS Auditors2',
      etsTechnicalExperts: 'ETS Experts2',
      independentReviewer: 'reviewer2',
      technicalExperts: 'Reviewer Experts2',
      authorisedSignatoryName: 'Authorised signatory2',
    },
    verifierContact: {
      name: 'VerifierAdminFirst2 VerifierAdminLast2',
      email: 'verifieradmin2@xx.gr',
      phoneNumber: '6995286252',
    },
  };

  class Page extends BasePage<VerifierDetailsComponent> {
    get name() {
      return this.getInputValue('#name');
    }
    set name(value: string) {
      this.setInputValue('#name', value);
    }

    get email() {
      return this.getInputValue('#email');
    }
    set email(value: string) {
      this.setInputValue('#email', value);
    }

    get phoneNumber() {
      return this.getInputValue('#phoneNumber');
    }
    set phoneNumber(value: string) {
      this.setInputValue('#phoneNumber', value);
    }

    get leadEtsAuditor() {
      return this.getInputValue('#leadEtsAuditor');
    }
    set leadEtsAuditor(value: string) {
      this.setInputValue('#leadEtsAuditor', value);
    }

    get etsAuditors() {
      return this.getInputValue('#etsAuditors');
    }
    set etsAuditors(value: string) {
      this.setInputValue('#etsAuditors', value);
    }

    get etsTechnicalExperts() {
      return this.getInputValue('#etsTechnicalExperts');
    }
    set etsTechnicalExperts(value: string) {
      this.setInputValue('#etsTechnicalExperts', value);
    }

    get independentReviewer() {
      return this.getInputValue('#independentReviewer');
    }
    set independentReviewer(value: string) {
      this.setInputValue('#independentReviewer', value);
    }

    get technicalExperts() {
      return this.getInputValue('#technicalExperts');
    }
    set technicalExperts(value: string) {
      this.setInputValue('#technicalExperts', value);
    }

    get authorisedSignatoryName() {
      return this.getInputValue('#authorisedSignatoryName');
    }
    set authorisedSignatoryName(value: string) {
      this.setInputValue('#authorisedSignatoryName', value);
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
    fixture = TestBed.createComponent(VerifierDetailsComponent);
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

  describe('for new verifier details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          verifierContact: null,
          verificationTeamDetails: null,
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
      expect(page.errorSummaryList).toEqual([
        "Enter the verifier's name",
        "Enter the verifier's email address",
        "Enter the verifier's phone number",
        'Enter the name of the lead ETS auditor',
        'Enter the name of the ETS auditor',
        'Enter the name of the technical experts (ETS auditor)',
        'Enter the name of the Independent Reviewer',
        'Enter the names of the Technical Experts (Independent Review)',
        'Enter the name of the authorised signatory',
      ]);

      const verificationReport = mockVerificationApplyPayload.verificationReport;
      page.name = verificationReport.verifierContact.name;
      page.email = verificationReport.verifierContact.email;
      page.phoneNumber = verificationReport.verifierContact.phoneNumber;

      page.leadEtsAuditor = verificationReport.verificationTeamDetails.leadEtsAuditor;
      page.etsAuditors = verificationReport.verificationTeamDetails.etsAuditors;
      page.etsTechnicalExperts = verificationReport.verificationTeamDetails.etsTechnicalExperts;
      page.independentReviewer = verificationReport.verificationTeamDetails.independentReviewer;
      page.technicalExperts = verificationReport.verificationTeamDetails.technicalExperts;
      page.authorisedSignatoryName = verificationReport.verificationTeamDetails.authorisedSignatoryName;

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            verifierContact: verificationReport.verifierContact,
            verificationTeamDetails: verificationReport.verificationTeamDetails,
          },
          { verificationTeamDetails: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing verifier details', () => {
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
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });

    it('should edit, submit a valid form and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.name = verifierDetails.verifierContact.name;
      page.email = verifierDetails.verifierContact.email;
      page.phoneNumber = verifierDetails.verifierContact.phoneNumber;

      page.leadEtsAuditor = verifierDetails.verificationTeamDetails.leadEtsAuditor;
      page.etsAuditors = verifierDetails.verificationTeamDetails.etsAuditors;
      page.etsTechnicalExperts = verifierDetails.verificationTeamDetails.etsTechnicalExperts;
      page.independentReviewer = verifierDetails.verificationTeamDetails.independentReviewer;
      page.technicalExperts = verifierDetails.verificationTeamDetails.technicalExperts;
      page.authorisedSignatoryName = verifierDetails.verificationTeamDetails.authorisedSignatoryName;

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            verifierContact: verifierDetails.verifierContact,
            verificationTeamDetails: verifierDetails.verificationTeamDetails,
          },
          { verificationTeamDetails: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });
});
