import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerVerifyTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';
import produce from 'immer';

import { UncorrectedMisstatementsComponent } from './uncorrected-misstatements.component';
import { UncorrectedMisstatementsFormProvider } from './uncorrected-misstatements-form.provider';

describe('UncorrectedMisstatementsComponent', () => {
  let page: Page;
  let fixture: ComponentFixture<UncorrectedMisstatementsComponent>;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let component: UncorrectedMisstatementsComponent;

  class Page extends BasePage<UncorrectedMisstatementsComponent> {
    get existRadios() {
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        UncorrectedMisstatementsComponent,
        RouterTestingModule.withRoutes([
          {
            path: 'summary',
            component: UncorrectedMisstatementsComponent,
          },
        ]),
      ],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: UncorrectedMisstatementsFormProvider },
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
              verificationReport: {},
            } as AerVerifyTaskPayload,
          },
        };
      }),
    );

    fixture = TestBed.createComponent(UncorrectedMisstatementsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit a valid form and navigate to summary', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerVerifySpy = jest.spyOn(store.aerVerifyDelegate, 'saveAerVerify').mockReturnValue(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryList).toEqual([
      'Select if there are any misstatements not corrected before issuing the report',
    ]);

    page.existRadios[1].click();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(saveAerVerifySpy).toHaveBeenCalledTimes(1);

    expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
      relativeTo: activatedRoute,
      queryParams: { change: true },
    });
  });

  it('should submit a valid form and navigate to add item', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerVerifySpy = jest.spyOn(store.aerVerifyDelegate, 'saveAerVerify').mockReturnValue(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryList).toEqual([
      'Select if there are any misstatements not corrected before issuing the report',
    ]);

    page.existRadios[0].click();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(saveAerVerifySpy).toHaveBeenCalledTimes(1);

    expect(navigateSpy).toHaveBeenCalledWith(['list/0'], {
      relativeTo: activatedRoute,
      queryParams: { change: true },
    });
  });
});
