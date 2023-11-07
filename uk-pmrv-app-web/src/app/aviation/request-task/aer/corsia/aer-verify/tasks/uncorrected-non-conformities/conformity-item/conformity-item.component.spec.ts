import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ConformityItemComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/uncorrected-non-conformities/conformity-item/conformity-item.component';
import { UncorrectedNonConformitiesFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/uncorrected-non-conformities/uncorrected-non-conformities-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import {
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerCorsiaUncorrectedNonConformities,
  TasksService,
} from 'pmrv-api';

describe('ConformityItemComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: ConformityItemComponent;
  let fixture: ComponentFixture<ConformityItemComponent>;
  let formProvider: UncorrectedNonConformitiesFormProvider;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ index: '0' });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ConformityItemComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    set explanation(value: string) {
      this.setInputValue('#explanation', value);
    }
    get materialRadios() {
      return this.queryAll<HTMLInputElement>('input');
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
    fixture = TestBed.createComponent(ConformityItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConformityItemComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: UncorrectedNonConformitiesFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for new uncorrected non conformity item', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<UncorrectedNonConformitiesFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestInfo: { type: 'AVIATION_AER_CORSIA' },
          requestTask: {
            id: 19,
            type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
              verificationReport: {
                uncorrectedNonConformities: {
                  existUncorrectedNonConformities: true,
                },
              },
              verificationSectionsCompleted: {},
            },
          },
        },
        isEditable: true,
      } as any);
      formProvider.setFormValue(
        (store.aerVerifyDelegate.payload as AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload)
          .verificationReport.uncorrectedNonConformities as AviationAerCorsiaUncorrectedNonConformities,
      );

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents.length).toEqual(2);
      expect(page.errorSummaryListContents).toEqual([
        'Describe the non-conformity',
        'Select if this has a material effect on the total emissions reported',
      ]);
    });

    it('should submit a valid form and navigate to `list` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.explanation = 'non-compliance';
      page.materialRadios[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
        requestTaskActionPayload: {
          uncorrectedNonConformities: {
            existUncorrectedNonConformities: true,
            uncorrectedNonConformities: [
              {
                reference: 'B1',
                explanation: 'non-compliance',
                materialEffect: true,
              },
            ],
          },
          verificationSectionsCompleted: { uncorrectedNonConformities: [false] },
          payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute, queryParams: { change: true } });
    });
  });

  describe('for editing uncorrected non conformity item', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<UncorrectedNonConformitiesFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestInfo: { type: 'AVIATION_AER_CORSIA' },
          requestTask: {
            id: 19,
            type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
              verificationReport: {
                uncorrectedNonConformities: {
                  existUncorrectedNonConformities: true,
                  uncorrectedNonConformities: [
                    {
                      reference: 'B1',
                      explanation: 'non-compliance',
                      materialEffect: true,
                    },
                  ],
                },
              },
              verificationSectionsCompleted: {},
            },
          },
        },
        isEditable: true,
      } as any);
      formProvider.setFormValue(
        (store.aerVerifyDelegate.payload as AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload)
          .verificationReport.uncorrectedNonConformities as AviationAerCorsiaUncorrectedNonConformities,
      );

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual(
        'Add a non-conformity with the approved emissions monitoring plan',
      );
      expect(page.submitButton).toBeTruthy();
    });

    it('should edit, submit a valid form and navigate to `list` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.explanation = 'non-compliance changed';
      page.materialRadios[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
        requestTaskActionPayload: {
          uncorrectedNonConformities: {
            existUncorrectedNonConformities: true,
            uncorrectedNonConformities: [
              {
                reference: 'B1',
                explanation: 'non-compliance changed',
                materialEffect: false,
              },
            ],
          },
          verificationSectionsCompleted: { uncorrectedNonConformities: [false] },
          payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute, queryParams: { change: true } });
    });
  });
});
