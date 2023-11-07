import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';
import produce from 'immer';

import { RecommendedImprovementsFormProvider } from '../recommended-improvements-form.provider';
import { RecommendedImprovementsSummaryComponent } from './recommended-improvements-summary.component';

describe('RecommendedImprovementsSummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let component: RecommendedImprovementsSummaryComponent;
  let fixture: ComponentFixture<RecommendedImprovementsSummaryComponent>;
  let formProvider: RecommendedImprovementsFormProvider;

  class Page extends BasePage<RecommendedImprovementsSummaryComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDListElement>('dl').map((data) =>
        Array.from(data.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }

    get submitButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RecommendedImprovementsSummaryComponent,
        RouterTestingModule,
        UncorrectedItemGroupComponent,
        RouterModule,
      ],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: RecommendedImprovementsFormProvider },
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
                recommendedImprovements: {
                  exist: true,
                  recommendedImprovements: [{ reference: 'A1', explanation: 'explanation' }],
                },
              },
              verificationSectionsCompleted: {
                recommendedImprovements: [false],
              },
            } as any,
          },
        };
      }),
    );
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RecommendedImprovementsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);

    formProvider = TestBed.inject<RecommendedImprovementsFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue({
      exist: true,
      recommendedImprovements: [{ reference: 'A1', explanation: 'explanation' }],
    });

    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit and navigate to task list', () => {
    expect(page.summaryListValues).toEqual([['Yes', 'Change']]);

    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerVerifySpy = jest.spyOn(store.aerVerifyDelegate, 'saveAerVerify').mockReturnValue(of({}));
    page.submitButton.click();
    fixture.detectChanges();

    expect(saveAerVerifySpy).toHaveBeenCalledTimes(1);

    expect(navigateSpy).toHaveBeenCalledWith(['../../..'], { relativeTo: activatedRoute });
  });
});
