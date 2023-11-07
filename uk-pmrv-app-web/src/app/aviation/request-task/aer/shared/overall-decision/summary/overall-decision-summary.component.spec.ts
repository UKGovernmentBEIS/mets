import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';
import produce from 'immer';

import { OverallDecisionFormProvider } from '../overall-decision-form.provider';
import { OverallDecisionSummaryComponent } from './overall-decision-summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let component: OverallDecisionSummaryComponent;
  let fixture: ComponentFixture<OverallDecisionSummaryComponent>;
  let formProvider: OverallDecisionFormProvider;

  class Page extends BasePage<OverallDecisionSummaryComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDListElement>('dl').map((data) =>
        Array.from(data.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OverallDecisionSummaryComponent, RouterTestingModule],
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
                  notVerifiedReasons: [
                    {
                      type: 'UNCORRECTED_MATERIAL_MISSTATEMENT',
                    },
                  ],
                },
              },
              verificationSectionsCompleted: {
                overallDecision: [false],
              },
            } as any,
          },
          requestInfo: {
            type: 'AVIATION_AER_UKETS',
          },
        };
      }),
    );
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OverallDecisionSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);

    formProvider = TestBed.inject<OverallDecisionFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue({
      type: 'NOT_VERIFIED',
      notVerifiedReasons: [
        {
          type: 'UNCORRECTED_MATERIAL_MISSTATEMENT',
        },
      ],
    });

    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit and navigate to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerVerifySpy = jest.spyOn(store.aerVerifyDelegate, 'saveAerVerify').mockReturnValue(of({}));

    expect(page.summaryListValues).toEqual([
      ['Not verified', 'Change', 'An uncorrected material misstatement (individual or in aggregate)', 'Change'],
    ]);

    page.submitButton.click();
    fixture.detectChanges();

    expect(saveAerVerifySpy).toHaveBeenCalledTimes(1);

    expect(navigateSpy).toHaveBeenCalledWith(['../../..'], { relativeTo: activatedRoute });
  });
});
