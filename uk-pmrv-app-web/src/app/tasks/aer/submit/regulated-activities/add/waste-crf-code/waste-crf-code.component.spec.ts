import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AerModule } from '@tasks/aer/aer.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { mockState } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, MockType } from '@testing';

import { AerRegulatedActivity, TasksService } from 'pmrv-api';

import { WasteCrfCodeComponent } from './waste-crf-code.component';

describe('WasteCrfCodeComponent', () => {
  let component: WasteCrfCodeComponent;
  let fixture: ComponentFixture<WasteCrfCodeComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let aerService: AerService;

  const route = new ActivatedRouteStub({ activityId: '324' });
  const tasksService: MockType<TasksService> = {
    processRequestTaskAction: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<WasteCrfCodeComponent> {
    get wasteCrfCategory() {
      return this.fixture.componentInstance.form.get('wasteCrfCategory').value;
    }

    set wasteCrfCategory(value: string) {
      this.fixture.componentInstance.form.get('wasteCrfCategory').setValue(value);
    }

    get wasteCrf() {
      return this.fixture.componentInstance.form.get('wasteCrf').value;
    }

    set wasteCrf(value: AerRegulatedActivity['wasteCrf']) {
      this.fixture.componentInstance.form.get('wasteCrf').setValue(value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }

    get title() {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, AerModule],
      providers: [
        DestroySubject,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(WasteCrfCodeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    aerService = TestBed.inject(AerService);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding waste crf', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display add title', () => {
      expect(page.title).toEqual('Select a waste CRF code for this regulated activity');
    });

    it('should raise validation error for waste crf', () => {
      page.wasteCrfCategory = '_5_A';
      page.wasteCrf = null;
      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['You must select at least one process']);
    });

    it('should submit a valid form, update the store and navigate', () => {
      store.setState({
        ...mockState,
        requestTaskItem: {
          ...mockState.requestTaskItem,
          requestTask: {
            ...mockState.requestTaskItem.requestTask,
            payload: {
              ...mockState.requestTaskItem.requestTask.payload,
              aer: {
                ...mockState.requestTaskItem.requestTask.payload?.['aer'],
                regulatedActivities: [
                  {
                    ...mockState.requestTaskItem.requestTask.payload?.['aer']['regulatedActivities'][0],
                    hasWasteCrf: true,
                  },
                ],
              },
            },
          },
        },
      });
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      const expectedRegulatedActivities: AerRegulatedActivity[] = [
        {
          id: '324',
          type: 'AMMONIA_PRODUCTION',
          capacity: '100',
          capacityUnit: 'KVA',
          hasEnergyCrf: true,
          hasIndustrialCrf: true,
          energyCrf: '_1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION',
          industrialCrf: '_2_A_4_OTHER_PROCESS_USES_OF_CARBONATES',
          hasWasteCrf: true,
          wasteCrf: '_5_A_1_A_SOLID_WASTE_DISPOSAL_TO_LAND',
        },
      ];
      const navigateSpy = jest.spyOn(router, 'navigate');
      const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');

      page.wasteCrfCategory = '_5_A';
      page.wasteCrf = '_5_A_1_A_SOLID_WASTE_DISPOSAL_TO_LAND';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(postTaskSaveSpy).toHaveBeenCalledTimes(1);
      expect(postTaskSaveSpy).toHaveBeenCalledWith(
        { regulatedActivities: expectedRegulatedActivities },
        {},
        false,
        'regulatedActivities',
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../energy-crf-code'], { relativeTo: route });
    });
  });
});
