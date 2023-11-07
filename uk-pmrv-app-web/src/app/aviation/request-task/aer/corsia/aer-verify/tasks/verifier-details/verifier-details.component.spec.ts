import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { VerifierDetailsComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verifier-details/verifier-details.component';
import { VerifierDetailsFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verifier-details/verifier-details-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import {
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerCorsiaVerifierDetails,
  TasksService,
} from 'pmrv-api';

describe('VerifierDetailsComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: VerifierDetailsComponent;
  let fixture: ComponentFixture<VerifierDetailsComponent>;
  let formProvider: VerifierDetailsFormProvider;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<VerifierDetailsComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }
    get heading2(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h2');
    }

    set name(value: string) {
      this.setInputValue('#name', value);
    }
    set position(value: string) {
      this.setInputValue('#position', value);
    }
    set role(value: string) {
      this.setInputValue('#role', value);
    }
    set email(value: string) {
      this.setInputValue('#email', value);
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(VerifierDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifierDetailsComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: VerifierDetailsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for new verifier details', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<VerifierDetailsFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestInfo: { type: 'AVIATION_AER_CORSIA' },
          requestTask: {
            id: 19,
            type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
              verificationReport: {
                verificationBodyDetails: {
                  name: 'Verification body company',
                  address: {
                    line1: 'Korinthou 4, Neo Psychiko',
                    line2: 'line 2 legal test',
                    city: 'Athens',
                    country: 'GR',
                    postcode: '15452',
                  },
                },
                verificationBodyId: 1,
              },
              verificationSectionsCompleted: {},
            },
          },
        },
      } as any);

      formProvider.setFormValue(null);
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Complete the verifier details');
      expect(page.heading2.map((h2) => h2.textContent.trim())).toEqual([
        'Verification body',
        'Verification team leader',
      ]);
      expect(page.summaryValues).toEqual([
        ['Company name', 'Verification body company'],
        ['Address', `Korinthou 4, Neo Psychiko  , line 2 legal test Athens15452`],
      ]);
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents.length).toEqual(4);
      expect(page.errorSummaryListContents).toEqual([
        'Enter the name of the verification team leader',
        'Enter the position of the verification team leader',
        'Enter the role and expertise of the verification team leader',
        'Enter the email address of the verification team leader',
      ]);
    });

    it('should submit a valid form and navigate to `conflict-interest` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.name = 'My name';
      page.position = 'My position';
      page.role = 'My role';
      page.email = 'test@pmrv.com';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
        requestTaskActionPayload: {
          verifierDetails: {
            interestConflictAvoidance: {
              breakTaken: null,
              impartialityAssessmentResult: null,
              reason: null,
              sixVerificationsConducted: null,
            },
            verificationTeamLeader: {
              name: 'My name',
              role: 'My role',
              email: 'test@pmrv.com',
              position: 'My position',
            },
          },
          verificationSectionsCompleted: { verifierDetails: [false] },
          payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['conflict-interest'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing verifier details', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<VerifierDetailsFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestInfo: { type: 'AVIATION_AER_CORSIA' },
          requestTask: {
            id: 19,
            type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
              verificationReport: {
                verificationBodyDetails: {
                  name: 'Verification body company',
                  address: {
                    line1: 'Korinthou 4, Neo Psychiko',
                    line2: 'line 2 legal test',
                    city: 'Athens',
                    country: 'GR',
                    postcode: '15452',
                  },
                },
                verificationBodyId: 1,
                verifierDetails: {
                  verificationTeamLeader: {
                    name: 'My name',
                    role: 'My role',
                    email: 'test@pmrv.com',
                    position: 'My position',
                  },
                  interestConflictAvoidance: {
                    sixVerificationsConducted: false,
                    impartialityAssessmentResult: 'My results',
                  },
                },
              },
              verificationSectionsCompleted: {},
            },
          },
        },
      } as any);
      formProvider.setFormValue(
        (store.aerVerifyDelegate.payload as AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload)
          .verificationReport.verifierDetails as AviationAerCorsiaVerifierDetails,
      );

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Complete the verifier details');
      expect(page.heading2.map((h2) => h2.textContent.trim())).toEqual([
        'Verification body',
        'Verification team leader',
      ]);
      expect(page.summaryValues).toEqual([
        ['Company name', 'Verification body company'],
        ['Address', `Korinthou 4, Neo Psychiko  , line 2 legal test Athens15452`],
      ]);
      expect(page.submitButton).toBeTruthy();
    });

    it('should edit, submit a valid form and navigate to `conflict-interest` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.name = 'My name changed';
      page.position = 'My position changed';
      page.role = 'My role changed';
      page.email = 'changed@pmrv.com';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
        requestTaskActionPayload: {
          verifierDetails: {
            interestConflictAvoidance: {
              breakTaken: null,
              impartialityAssessmentResult: 'My results',
              reason: null,
              sixVerificationsConducted: false,
            },
            verificationTeamLeader: {
              name: 'My name changed',
              role: 'My role changed',
              email: 'changed@pmrv.com',
              position: 'My position changed',
            },
          },
          verificationSectionsCompleted: { verifierDetails: [false] },
          payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['conflict-interest'], { relativeTo: activatedRoute });
    });
  });
});
