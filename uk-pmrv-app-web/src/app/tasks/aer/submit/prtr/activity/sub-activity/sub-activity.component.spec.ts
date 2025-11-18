import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { SubActivityComponent } from './sub-activity.component';

describe('SubActivityComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: SubActivityComponent;
  let fixture: ComponentFixture<SubActivityComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SubActivityComponent> {
    get activityRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="activity"]');
    }

    get subActivity2CRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="subActivity_2_C"]');
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
    fixture = TestBed.createComponent(SubActivityComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule],
      providers: [
        provideRouter([]),
        { provide: TasksService, useValue: tasksService },
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({ activityItem: '_2' }),
            paramMap: of(convertToParamMap({ index: 0 })),
            snapshot: {
              queryParams: { activityItem: '_2' },
              paramMap: convertToParamMap({ index: 0 }),
            },
          },
        },
      ],
    }).compileComponents();
  });

  describe('for new prtr', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild({ prtrCodes: null }));
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show activities and submit form', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.activityRadioButtons.map((el) => el.value)).toEqual(['_2_A', '_2_B', '_2_C', '_2_D', '_2_E', '_2_F']);

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Enter the activity']);

      page.activityRadioButtons[2].click();
      fixture.detectChanges();

      expect(page.subActivity2CRadioButtons.map((el) => el.value)).toEqual(['_2_C_1', '_2_C_2', '_2_C_3']);

      page.subActivity2CRadioButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            prtrCodes: {
              codes: ['_2_C_2_SMITHERIES_WITH_HAMMERS'],
              exist: true,
            },
          },
          { prtrCodes: [false] },
        ),
      );
    });
  });
});
