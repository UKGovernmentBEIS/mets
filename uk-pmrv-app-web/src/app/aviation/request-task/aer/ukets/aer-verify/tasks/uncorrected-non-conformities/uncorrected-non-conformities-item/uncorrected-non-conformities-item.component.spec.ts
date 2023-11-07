import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { AerVerifyTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import produce from 'immer';

import { VERIFICATION_REPORT } from '../../../tests/mock-verification-report';
import { UncorrectedNonConformitiesFormProvider } from '../uncorrected-non-conformities-form.provider';
import UncorrectedNonConformitiesItemComponent from './uncorrected-non-conformities-item.component';

describe('UncorrectedNonConformitiesItemComponent', () => {
  let fixture: ComponentFixture<UncorrectedNonConformitiesItemComponent>;
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;

  const route = new ActivatedRouteStub({ index: '0' });

  class Page extends BasePage<UncorrectedNonConformitiesItemComponent> {
    get explanationValue() {
      return this.getInputValue('#explanation');
    }

    set explanationValue(value: string) {
      this.setInputValue('#explanation', value);
    }

    get materialEffects() {
      return this.queryAll<HTMLInputElement>('input');
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
    fixture = TestBed.createComponent(UncorrectedNonConformitiesItemComponent);
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UncorrectedNonConformitiesItemComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: UncorrectedNonConformitiesFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              verificationReport: {
                ...VERIFICATION_REPORT,
              },
            } as AerVerifyTaskPayload,
          },
        };
      }),
    );
  });

  describe('for new item', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(fixture.componentInstance).toBeTruthy();
    });

    it('should display an empty form', () => {
      expect(page.errorSummary).toBeFalsy();
    });

    it('should submit a valid form and navigate to list', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      const setUncorrectedNonConformitiesSpy = jest.spyOn(store.aerVerifyDelegate, 'setUncorrectedNonConformities');

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual([
        'Describe the non-conformity',
        'Select if this has a significant effect on the total emissions reported',
      ]);

      page.explanationValue = 'explanation 1';
      page.materialEffects[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(setUncorrectedNonConformitiesSpy).toHaveBeenCalledTimes(1);

      expect(navigateSpy).toHaveBeenCalledWith(['..'], {
        relativeTo: activatedRoute,
        replaceUrl: true,
        queryParams: { change: true },
      });
    });
  });
});
