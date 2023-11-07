import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import produce from 'immer';

import { OverallDecisionFormProvider } from '../../overall-decision-form.provider';
import { ReasonItemComponent } from './reason-item.component';

describe('ReasonItemComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let component: ReasonItemComponent;
  let fixture: ComponentFixture<ReasonItemComponent>;

  const route = new ActivatedRouteStub({ index: '0' });

  class Page extends BasePage<ReasonItemComponent> {
    get reasonValue() {
      return this.getInputValue('#reason');
    }
    set reasonValue(value: string) {
      this.setInputValue('#reason', value);
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
    fixture = TestBed.createComponent(ReasonItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReasonItemComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: OverallDecisionFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: route },
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
                  type: 'VERIFIED_AS_SATISFACTORY_WITH_COMMENTS',
                  reasons: [],
                },
              },
            } as any,
          },
        };
      }),
    );
  });

  describe('for new item', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display an empty form', () => {
      expect(page.errorSummary).toBeFalsy();
    });

    it('should submit a valid form and navigate to list', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      const saveAerVerifySpy = jest.spyOn(store.aerVerifyDelegate, 'saveAerVerify').mockReturnValue(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Give a reason why the report cannot be verified as satisfactory']);

      page.reasonValue = 'Reason 1';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(saveAerVerifySpy).toHaveBeenCalledTimes(1);

      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute, queryParams: { change: true } });
    });
  });

  describe('for existing item', () => {
    beforeEach(() => {
      store.setState(
        produce(store.getState(), (state) => {
          state.isEditable = true;
          state.requestTaskItem = {
            requestTask: {
              type: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT',
              payload: {
                verificationReport: {
                  overallDecision: {
                    type: 'VERIFIED_AS_SATISFACTORY_WITH_COMMENTS',
                    reasons: ['Reason 1'],
                  },
                },
              } as any,
            },
          };
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should edit, submit a valid form and navigate to list', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      const saveAerVerifySpy = jest.spyOn(store.aerVerifyDelegate, 'saveAerVerify').mockReturnValue(of({}));

      expect(page.reasonValue).toEqual('Reason 1');
      expect(page.errorSummary).toBeFalsy();

      page.reasonValue = 'New reason 1';

      expect(page.errorSummary).toBeFalsy();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(saveAerVerifySpy).toHaveBeenCalledTimes(1);

      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute, queryParams: { change: true } });
    });
  });
});
