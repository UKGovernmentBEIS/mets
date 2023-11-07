import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { EmissionsReductionClaimComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-emissions-reduction-claim/emissions-reduction-claim.component';
import { EmissionsReductionClaimFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-emissions-reduction-claim/emissions-reduction-claim-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import {
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerCorsiaEmissionsReductionClaimVerification,
  TasksService,
} from 'pmrv-api';

describe('EmissionsReductionClaimComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: EmissionsReductionClaimComponent;
  let fixture: ComponentFixture<EmissionsReductionClaimComponent>;
  let formProvider: EmissionsReductionClaimFormProvider;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<EmissionsReductionClaimComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }
    get heading2(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h2');
    }

    set reviewResults(value: string) {
      this.setInputValue('#reviewResults', value);
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
    fixture = TestBed.createComponent(EmissionsReductionClaimComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmissionsReductionClaimComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: EmissionsReductionClaimFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for new emissions reduction claim', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestInfo: { type: 'AVIATION_AER_CORSIA' },
          requestTask: {
            id: 19,
            type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
              verificationReport: {},
              verificationSectionsCompleted: {},
            },
          },
        },
      } as any);

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Verify the emissions reduction claim');
      expect(page.heading2.map((h2) => h2.textContent.trim())).toEqual(['Provide the results of your verification']);
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents.length).toEqual(1);
      expect(page.errorSummaryListContents).toEqual([
        `Enter the results of your verification of the operator's CORSIA eligible fuels claim`,
      ]);
    });

    it('should submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.reviewResults = 'My review results';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
        requestTaskActionPayload: {
          emissionsReductionClaimVerification: {
            reviewResults: 'My review results',
          },
          verificationSectionsCompleted: { emissionsReductionClaimVerification: [false] },
          payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing emissions reduction claim', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestInfo: { type: 'AVIATION_AER_CORSIA' },
          requestTask: {
            id: 19,
            type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
              verificationReport: {
                emissionsReductionClaimVerification: {
                  reviewResults: 'My review results',
                },
              },
              verificationSectionsCompleted: {},
            },
          },
        },
      } as any);
      formProvider.setFormValue(
        (store.aerVerifyDelegate.payload as AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload)
          .verificationReport
          .emissionsReductionClaimVerification as AviationAerCorsiaEmissionsReductionClaimVerification,
      );

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Verify the emissions reduction claim');
      expect(page.heading2.map((h2) => h2.textContent.trim())).toEqual(['Provide the results of your verification']);
      expect(page.submitButton).toBeTruthy();
    });

    it('should edit, submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.reviewResults = 'My review results changed';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
        requestTaskActionPayload: {
          emissionsReductionClaimVerification: {
            reviewResults: 'My review results changed',
          },
          verificationSectionsCompleted: { emissionsReductionClaimVerification: [false] },
          payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });
});
