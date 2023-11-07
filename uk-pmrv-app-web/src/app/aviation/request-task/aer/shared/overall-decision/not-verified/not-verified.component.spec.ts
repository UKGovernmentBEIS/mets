import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';
import produce from 'immer';

import { AviationAerNotVerifiedDecision } from 'pmrv-api';

import { OverallDecisionFormProvider } from '../overall-decision-form.provider';
import { NotVerifiedComponent } from './not-verified.component';

describe('NotVerifiedComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let component: NotVerifiedComponent;
  let fixture: ComponentFixture<NotVerifiedComponent>;
  let formProvider: OverallDecisionFormProvider;

  class Page extends BasePage<NotVerifiedComponent> {
    get checkboxes() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__input');
    }
    get checkbox_labels() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__label');
    }
    get otherDetails() {
      return this.getInputValue('#otherDetails');
    }
    set otherDetails(value: string) {
      this.setInputValue('#otherDetails', value);
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
    fixture = TestBed.createComponent(NotVerifiedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    activatedRoute = TestBed.inject(ActivatedRoute);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotVerifiedComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: OverallDecisionFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    store.setState(
      produce(store.getState(), (state) => {
        state.isEditable = true;
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              verificationReport: {
                overallDecision: {
                  type: 'NOT_VERIFIED',
                  notVerifiedReasons: [],
                } as AviationAerNotVerifiedDecision,
              },
            } as any,
          },
        };
      }),
    );
    formProvider = TestBed.inject<OverallDecisionFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue({
      type: 'NOT_VERIFIED',
    });
    createComponent();
  });

  it('should create 0', () => {
    expect(component).toBeTruthy();
  });

  describe('for new overall decision', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });
    it('should display an empty form', () => {
      expect(page.errorSummary).toBeFalsy();
    });
    it('should submit a valid form and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      const saveAerVerifySpy = jest.spyOn(store.aerVerifyDelegate, 'saveAerVerify').mockReturnValue(of({}));
      expect(page.checkboxes.length).toEqual(7);
      expect(page.checkbox_labels[0].innerHTML).toContain(
        'uncorrected material misstatement (individual or in aggregate)',
      );
      expect(page.checkbox_labels[1].innerHTML).toContain(
        'uncorrected material non-conformity (individual or in aggregate)',
      );
      expect(page.checkbox_labels[2].innerHTML).toContain(
        'Limitations in the data or information made available for verification',
      );
      expect(page.checkbox_labels[3].innerHTML).toContain('Limitations of scope due to lack of clarity');
      expect(page.checkbox_labels[4].innerHTML).toContain(
        'Limitations of scope of the approved emissions monitoring plan',
      );
      expect(page.checkbox_labels[5].innerHTML).toContain(
        'The emissions monitoring plan is not approved by the regulator',
      );
      expect(page.checkbox_labels[6].innerHTML).toContain('Another reason');
      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual([
        'Select at least one option from the list of reasons why you cannot verify the report',
      ]);
      page.checkboxes[6].click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Provide a reason why you cannot verify the report']);
      page.otherDetails = 'Add other reason';
      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();
      expect(saveAerVerifySpy).toHaveBeenCalledTimes(1);

      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRoute });
    });
  });
});
