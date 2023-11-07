import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerVerifyTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import produce from 'immer';

import { VERIFICATION_REPORT } from '../../../tests/mock-verification-report';
import { UncorrectedNonConformitiesFormProvider } from '../uncorrected-non-conformities-form.provider';
import UncorrectedNonConformitiesPriorYearItemDeleteComponent from './uncorrected-non-conformities-prior-year-item-delete.component';

describe('UncorrectedNonConformitiesPriorYearItemDeleteComponent', () => {
  let fixture: ComponentFixture<UncorrectedNonConformitiesPriorYearItemDeleteComponent>;
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;

  const route = new ActivatedRouteStub({ index: '0' });

  class Page extends BasePage<UncorrectedNonConformitiesPriorYearItemDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(UncorrectedNonConformitiesPriorYearItemDeleteComponent);
    fixture.componentInstance.formProvider.setFormValue(VERIFICATION_REPORT.uncorrectedNonConformities);

    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UncorrectedNonConformitiesPriorYearItemDeleteComponent, RouterTestingModule],
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

    createComponent();
  });

  describe('for item delete', () => {
    it('should create', () => {
      expect(fixture.componentInstance).toBeTruthy();
    });

    it('should display delete question', () => {
      expect(page.header.textContent.trim()).toEqual(`Are you sure you want to delete reference?`);
    });

    it('should delete and navigate to list', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      const saveAerVerifySpy = jest.spyOn(store.aerVerifyDelegate, 'saveAerVerify').mockReturnValue(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(saveAerVerifySpy).toHaveBeenCalledTimes(1);

      expect(navigateSpy).toHaveBeenCalledWith(['../..'], {
        relativeTo: activatedRoute,
        queryParams: { change: true },
      });
    });
  });
});
