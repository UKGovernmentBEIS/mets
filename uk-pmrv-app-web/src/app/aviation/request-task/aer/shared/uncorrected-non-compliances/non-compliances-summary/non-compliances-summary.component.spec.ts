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

import { UncorrectedNonCompliancesFormProvider } from '../uncorrected-non-compliances-form.provider';
import { NonCompliancesSummaryComponent } from './non-compliances-summary.component';

describe('NonCompliancesSummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let component: NonCompliancesSummaryComponent;
  let fixture: ComponentFixture<NonCompliancesSummaryComponent>;
  let formProvider: UncorrectedNonCompliancesFormProvider;

  class Page extends BasePage<NonCompliancesSummaryComponent> {
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
      imports: [NonCompliancesSummaryComponent, RouterTestingModule, UncorrectedItemGroupComponent, RouterModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: UncorrectedNonCompliancesFormProvider },
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
                uncorrectedNonCompliances: {
                  exist: true,
                  uncorrectedNonCompliances: [{ reference: 'A1', explanation: 'explanation', materialEffect: true }],
                },
              },
              verificationSectionsCompleted: {
                uncorrectedNonCompliances: [false],
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
    fixture = TestBed.createComponent(NonCompliancesSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);

    formProvider = TestBed.inject<UncorrectedNonCompliancesFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue({
      exist: true,
      uncorrectedNonCompliances: [{ reference: 'A1', explanation: 'explanation', materialEffect: true }],
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
