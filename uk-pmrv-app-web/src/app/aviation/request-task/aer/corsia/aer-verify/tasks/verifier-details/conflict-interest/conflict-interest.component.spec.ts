import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ConflictInterestComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verifier-details/conflict-interest/conflict-interest.component';
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

describe('ConflictInterestComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: ConflictInterestComponent;
  let fixture: ComponentFixture<ConflictInterestComponent>;
  let formProvider: VerifierDetailsFormProvider;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ConflictInterestComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }
    get heading2(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h2');
    }

    get sixVerificationsConductedRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="sixVerificationsConducted"]');
    }
    get breakTakenRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="breakTaken"]');
    }
    set reason(value: string) {
      this.setInputValue('#reason', value);
    }
    set impartialityAssessmentResult(value: string) {
      this.setInputValue('#impartialityAssessmentResult', value);
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
    fixture = TestBed.createComponent(ConflictInterestComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConflictInterestComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: VerifierDetailsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for new conflict of interest', () => {
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
      expect(page.heading1.textContent.trim()).toEqual('Avoiding a conflict of interest');
      expect(page.heading2.map((h2) => h2.textContent.trim())).toEqual(['Team leader repeat visits']);
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents.length).toEqual(1);
      expect(page.errorSummaryListContents).toEqual([
        'Select if the team leader has made more than 6 annual visits to this operator',
      ]);
    });

    it('should submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.sixVerificationsConductedRadioButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([
        'Provide a reason why you could not meet the conflict of interest requirement',
      ]);

      page.sixVerificationsConductedRadioButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([
        'Select if you then took a break of three consecutive years from providing verifications for this operator',
      ]);

      page.breakTakenRadioButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([
        'Provide a reason why you could not meet the conflict of interest requirement',
      ]);

      page.breakTakenRadioButtons[0].click();
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
              breakTaken: true,
              sixVerificationsConducted: true,
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
      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing conflict of interest', () => {
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
      expect(page.heading1.textContent.trim()).toEqual('Avoiding a conflict of interest');
      expect(page.heading2.map((h2) => h2.textContent.trim())).toEqual(['Team leader repeat visits']);
      expect(page.submitButton).toBeTruthy();
    });

    it('should edit, submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.sixVerificationsConductedRadioButtons[0].click();
      fixture.detectChanges();
      page.breakTakenRadioButtons[0].click();
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
              breakTaken: true,
              sixVerificationsConducted: true,
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
      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRoute });
    });
  });
});
