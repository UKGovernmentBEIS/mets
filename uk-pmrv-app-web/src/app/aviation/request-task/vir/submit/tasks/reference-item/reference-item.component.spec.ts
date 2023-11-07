import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReferenceItemComponent } from '@aviation/request-task/vir/submit/tasks/reference-item/reference-item.component';
import { ReferenceItemFormProvider } from '@aviation/request-task/vir/submit/tasks/reference-item/reference-item-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { OperatorImprovementResponse, TasksService, UncorrectedItem } from 'pmrv-api';

describe('ReferenceItemComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: ReferenceItemComponent;
  let fixture: ComponentFixture<ReferenceItemComponent>;
  let formProvider: ReferenceItemFormProvider;

  const currentItem = {
    reference: 'B1',
    explanation: 'Test uncorrectedNonConformity',
    materialEffect: true,
  } as UncorrectedItem;
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 19, id: currentItem.reference }, null, {
    verificationDataItem: currentItem,
  });
  const tasksService = mockClass(TasksService);
  const expectedDate = new Date('2023-12-01');

  class Page extends BasePage<ReferenceItemComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get verificationItem() {
      return this.query('app-verification-recommendation-item');
    }

    get isAddressedButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="isAddressed"]');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    set addressedDate(date: Date) {
      this.setInputValue(`#addressedDate-day`, date.getDate());
      this.setInputValue(`#addressedDate-month`, date.getMonth() + 1);
      this.setInputValue(`#addressedDate-year`, date.getFullYear());
    }

    set addressedDescription(value: string) {
      this.setInputValue('#addressedDescription', value);
    }

    set addressedDescription2(value: string) {
      this.setInputValue('#addressedDescription2', value);
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ReferenceItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReferenceItemComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: ReferenceItemFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for new recommendation response details', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<ReferenceItemFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestTask: {
            id: 19,
            type: 'AVIATION_VIR_APPLICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD',
              verificationData: {
                uncorrectedNonConformities: {
                  B1: currentItem,
                },
              },
              virSectionsCompleted: { B1: false },
              operatorImprovementResponses: {},
            },
          },
        },
      } as any);

      formProvider.setFormValue(null);
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Respond to B1');
      expect(page.verificationItem).toBeTruthy();
      expect(page.isAddressedButtons).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([
        'Select if you have addressed this recommendation or plan to in the future',
      ]);
      expect(page.errorSummaryListContents.length).toEqual(1);
    });

    it('should submit a valid form and navigate to `upload-evidence-question` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.isAddressedButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual(['State how the recommendation will be addressed', 'Enter a date']);

      page.isAddressedButtons[0].click();
      page.addressedDescription = 'Test description B1';
      page.addressedDate = expectedDate;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_VIR_SAVE_APPLICATION',
        requestTaskActionPayload: {
          operatorImprovementResponses: {
            B1: {
              isAddressed: true,
              addressedDescription: 'Test description B1',
              addressedDate: expectedDate,
              uploadEvidence: null,
              files: [],
            },
          },
          virSectionsCompleted: { B1: false },
          payloadType: 'AVIATION_VIR_SAVE_APPLICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['upload-evidence-question'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing recommendation response details', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<ReferenceItemFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestTask: {
            id: 19,
            type: 'AVIATION_VIR_APPLICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD',
              verificationData: {
                uncorrectedNonConformities: {
                  B1: currentItem,
                },
              },
              virSectionsCompleted: { B1: false },
              operatorImprovementResponses: {
                B1: {
                  isAddressed: true,
                  addressedDescription: 'Test description B1, when yes',
                  addressedDate: expectedDate,
                  uploadEvidence: false,
                  files: [],
                },
              },
            },
          },
        },
      } as any);
      formProvider.setFormValue(
        store.virDelegate.payload.operatorImprovementResponses['B1'] as OperatorImprovementResponse,
      );

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Respond to B1');
      expect(page.verificationItem).toBeTruthy();
      expect(page.isAddressedButtons).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should edit, submit a valid form and navigate to `upload-evidence-question` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.isAddressedButtons[1].click();
      page.addressedDescription2 = 'Test description B1, when no';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_VIR_SAVE_APPLICATION',
        requestTaskActionPayload: {
          operatorImprovementResponses: {
            B1: {
              isAddressed: false,
              addressedDescription: 'Test description B1, when no',
              addressedDate: null,
              uploadEvidence: false,
              files: [],
            },
          },
          virSectionsCompleted: { B1: false },
          payloadType: 'AVIATION_VIR_SAVE_APPLICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['upload-evidence-question'], { relativeTo: activatedRoute });
    });
  });
});
