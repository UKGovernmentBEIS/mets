import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { IndependentReviewComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/independent-review/independent-review.component';
import { IndependentReviewFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/independent-review/independent-review-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { CountryService } from '@core/services/country.service';
import { ActivatedRouteStub, BasePage, CountryServiceStub, mockClass } from '@testing';

import {
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerCorsiaIndependentReview,
  TasksService,
} from 'pmrv-api';

describe('IndependentReviewComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: IndependentReviewComponent;
  let fixture: ComponentFixture<IndependentReviewComponent>;
  let formProvider: IndependentReviewFormProvider;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<IndependentReviewComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }
    get heading2(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h2');
    }

    set reviewResults(value: string) {
      this.setInputValue('#reviewResults', value);
    }
    set name(value: string) {
      this.setInputValue('#name', value);
    }
    set position(value: string) {
      this.setInputValue('#position', value);
    }
    set email(value: string) {
      this.setInputValue('#email', value);
    }
    set line1(value: string) {
      this.setInputValue('#line1', value);
    }
    set line2(value: string) {
      this.setInputValue('#line2', value);
    }
    set city(value: string) {
      this.setInputValue('#city', value);
    }
    set state(value: string) {
      this.setInputValue('#state', value);
    }
    set postcode(value: string) {
      this.setInputValue('#postcode', value);
    }
    set country(value: string) {
      this.setInputValue('select[name="country"]', value);
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
    fixture = TestBed.createComponent(IndependentReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IndependentReviewComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: IndependentReviewFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: CountryService, useClass: CountryServiceStub },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for new independent review', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<IndependentReviewFormProvider>(TASK_FORM_PROVIDER);

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
      expect(page.heading1.textContent.trim()).toEqual('Independent review');
      expect(page.heading2.map((h2) => h2.textContent.trim())).toEqual([
        'Results of the independent review',
        'Independent reviewer details',
      ]);
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents.length).toEqual(7);
      expect(page.errorSummaryListContents).toEqual([
        'Provide the results of your independent review',
        'Enter the name of the independent reviewer',
        'Enter the position of the independent reviewer',
        'Enter the email address of the independent reviewer',
        'Enter the first line of your address',
        'Enter your town or city',
        'Enter your country',
      ]);
    });

    it('should submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.reviewResults = 'My review results';
      page.name = 'My name';
      page.position = 'My position';
      page.email = 'test@pmrv.com';
      page.line1 = 'Korinthou 4, Neo Psychiko';
      page.line2 = 'Line 2';
      page.city = 'Athens';
      page.state = 'Attica';
      page.postcode = '14344';
      page.country = 'GR';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
        requestTaskActionPayload: {
          independentReview: {
            reviewResults: 'My review results',
            name: 'My name',
            position: 'My position',
            email: 'test@pmrv.com',
            line1: 'Korinthou 4, Neo Psychiko',
            line2: 'Line 2',
            city: 'Athens',
            state: 'Attica',
            postcode: '14344',
            country: 'GR',
          },
          verificationSectionsCompleted: { independentReview: [false] },
          payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing independent review', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<IndependentReviewFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestInfo: { type: 'AVIATION_AER_CORSIA' },
          requestTask: {
            id: 19,
            type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
              verificationReport: {
                independentReview: {
                  reviewResults: 'My review results',
                  name: 'My name',
                  position: 'My position',
                  email: 'test@pmrv.com',
                  line1: 'Korinthou 4, Neo Psychiko',
                  line2: null,
                  city: 'Athens',
                  state: null,
                  postcode: null,
                  country: 'GR',
                },
              },
              verificationSectionsCompleted: {},
            },
          },
        },
      } as any);
      formProvider.setFormValue(
        (store.aerVerifyDelegate.payload as AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload)
          .verificationReport.independentReview as AviationAerCorsiaIndependentReview,
      );

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Independent review');
      expect(page.heading2.map((h2) => h2.textContent.trim())).toEqual([
        'Results of the independent review',
        'Independent reviewer details',
      ]);
      expect(page.submitButton).toBeTruthy();
    });

    it('should edit, submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.reviewResults = 'My review results changed';
      page.name = 'My name changed';
      page.position = 'My position changed';
      page.email = 'changed@pmrv.com';
      page.line1 = 'Korinthou 4, Neo Psychiko changed';
      page.line2 = 'Line 2 changed';
      page.city = 'Athens changed';
      page.state = 'Attica changed';
      page.postcode = '14344';
      page.country = 'GR';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
        requestTaskActionPayload: {
          independentReview: {
            reviewResults: 'My review results changed',
            name: 'My name changed',
            position: 'My position changed',
            email: 'changed@pmrv.com',
            line1: 'Korinthou 4, Neo Psychiko changed',
            line2: 'Line 2 changed',
            city: 'Athens changed',
            state: 'Attica changed',
            postcode: '14344',
            country: 'GR',
          },
          verificationSectionsCompleted: { independentReview: [false] },
          payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });
});
