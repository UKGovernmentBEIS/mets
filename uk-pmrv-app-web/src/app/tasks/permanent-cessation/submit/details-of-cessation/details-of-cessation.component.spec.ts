import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { PermanentCessationService } from '@tasks/permanent-cessation/shared';
import { mockPermanentCessationState } from '@tasks/permanent-cessation/submit/testing/mock-permanent-cessation-payload';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { PermanentCessationApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { DetailsOfCessationComponent } from './details-of-cessation.component';

describe('DetailsOfCessationComponent', () => {
  let component: DetailsOfCessationComponent;
  let fixture: ComponentFixture<DetailsOfCessationComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<DetailsOfCessationComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get description() {
      return this.query<HTMLTextAreaElement>('#description').textContent.trim();
    }

    set description(value: string) {
      this.setInputValue('#description', value);
    }

    get cessationDate() {
      return this.queryAll<HTMLInputElement>('#cessationDate .govuk-date-input__item');
    }
    get cessationDateType() {
      return this.fixture.componentInstance.form.get('cessationDate');
    }

    get cessationScopeRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="cessationScope"]');
    }

    get cessationScopeField() {
      return this.fixture.componentInstance.form.get('cessationScope');
    }

    set cessationScope(value: string) {
      this.setInputValue('#cessationScope', value);
    }

    get additionalDetails() {
      return this.getInputValue('#additionalDetails');
    }
    set additionalDetails(value: string) {
      this.setInputValue('#additionalDetails', value);
    }

    get comments() {
      return this.getInputValue('#regulatorComments');
    }
    set comments(value: string) {
      this.setInputValue('#regulatorComments', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(DetailsOfCessationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsOfCessationComponent],
      providers: [
        ItemNamePipe,
        PermanentCessationService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new permanent cessation', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockPermanentCessationState.requestTaskItem,
          requestTask: {
            ...mockPermanentCessationState.requestTaskItem.requestTask,
            payload: {
              ...mockPermanentCessationState.requestTaskItem.requestTask.payload,
            } as PermanentCessationApplicationSubmitRequestTaskPayload,
          },
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display an empty form', () => {
      expect(page.header).toEqual('Permanent cessation details');
      expect(page.description).toEqual('');
      expect(page.cessationDate.length).toEqual(3);
      expect(page.cessationScopeRadios.length).toEqual(2);
      expect(page.cessationScopeRadios.every((radio) => !radio.checked)).toBeTruthy();
      expect(page.additionalDetails).toEqual('');
      expect(page.comments).toEqual('');
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();
    });

    it('should submit and check for errors', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummaryListContents).toBeTruthy();
      expect(page.errorSummaryListContents.length).toEqual(4);
      expect(page.errorSummaryListContents).toEqual([
        'Enter a description of the cessation',
        'Enter the date of cessation',
        'Select if the cessation covers the whole installation or a sub-installation',
        'Enter the details to include in the notice document',
      ]);

      const date = new Date();
      date.setFullYear(new Date().getFullYear() + 1);
      page.cessationDateType.setValue(date);
      page.cessationScopeField.setValue('WHOLE_INSTALLATION');
      page.description = 'Description';
      page.additionalDetails = 'Comment';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummaryListContents).toEqual(['Date of cessation must be today or in the past']);

      date.setFullYear(new Date().getFullYear() - 1);
      page.cessationDateType.setValue(date);
      fixture.detectChanges();

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();
    });

    it('should submit and navigate to choose workflow', () => {
      page.description = 'Description';
      page.additionalDetails = 'Comment';
      const date = new Date('2024-03-21');
      date.setFullYear(date.getFullYear() - 1);
      page.cessationDateType.setValue(date);

      page.cessationScopeField.setValue('WHOLE_INSTALLATION');
      fixture.detectChanges();

      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionPayload: {
          payloadType: 'PERMANENT_CESSATION_SAVE_APPLICATION_PAYLOAD',
          permanentCessation: {
            additionalDetails: 'Comment',
            cessationDate: date,
            cessationScope: 'WHOLE_INSTALLATION',
            description: 'Description',
            files: [],
            regulatorComments: null,
          },
          permanentCessationAttachments: {},
          permanentCessationSectionsCompleted: {
            details: false,
          },
        },
        requestTaskActionType: 'PERMANENT_CESSATION_SAVE_APPLICATION',
        requestTaskId: 198,
      });
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: route });
    });
  });
});
