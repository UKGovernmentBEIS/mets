import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ReviewContainerComponent } from '@tasks/aer/review/review-container.component';
import { mockReview } from '@tasks/aer/review/testing/mock-review';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { AerModule } from '../aer.module';
import { AerService } from '../core/aer.service';

describe('ReviewContainerComponent', () => {
  let component: ReviewContainerComponent;
  let fixture: ComponentFixture<ReviewContainerComponent>;
  let page: Page;
  let store: CommonTasksStore;
  let aerService: AerService;

  class Page extends BasePage<ReviewContainerComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item > .app-task-list__task-name'));
    }
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TaskSharedModule, AerModule],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(ReviewContainerComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for review with verification report', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      jest.spyOn(store, 'storeInitialized$', 'get').mockReturnValue(of(true));
      jest.spyOn(store, 'requestMetadata$', 'get').mockReturnValue(
        of({
          type: 'AER',
          year: '2022',
        }),
      );
      jest.spyOn(store, 'requestTaskItem$', 'get').mockReturnValue(
        of({
          requestTask: {
            id: 1,
            type: 'AER_APPLICATION_REVIEW',
            payload: mockReview,
          },
          allowedRequestTaskActions: [],
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the content', () => {
      expect(page.heading).toEqual('Review 2022 emissions report');
      expect(page.sections.map((el) => el.textContent.trim())).toEqual([
        'Installation details',
        'Fuels and equipment inventory',
        'Calculation of CO2 emissions',
        'Measurement of CO2 emissions',
        'Inherent CO2 emissions',
        'Fallback approach emissions',
        'Calculation of perfluorocarbons (PFC) emissions',
        'Emissions summary',
        'Additional information',

        'Verifier details',
        'Opinion statement',
        'Compliance with ETS rules',
        'Compliance with monitoring and reporting principles',
        'Overall decision',

        'Uncorrected misstatements',
        'Uncorrected non-conformities',
        'Uncorrected non-compliances',
        'Recommended improvements',
        'Methodologies to close data gaps',
        'Materiality level and reference documents',
        'Summary of conditions, changes, clarifications and variations',
      ]);
    });
  });

  describe('for review with verification report after 2025', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      aerService = TestBed.inject(AerService);

      jest.spyOn(store, 'storeInitialized$', 'get').mockReturnValue(of(true));
      jest.spyOn(store, 'requestMetadata$', 'get').mockReturnValue(
        of({
          type: 'AER',
          year: 2025,
        }),
      );
      jest.spyOn(store, 'requestTaskItem$', 'get').mockReturnValue(
        of({
          requestTask: {
            id: 1,
            type: 'AER_APPLICATION_REVIEW',
            payload: { ...mockReview, reportingYear: 2025 },
          },
          allowedRequestTaskActions: [],
        }),
      );
      jest.spyOn(aerService, 'yearEqualAfter2025$', 'get').mockReturnValue(of(true));
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the content', () => {
      expect(page.heading).toEqual('Review 2025 emissions report');
      expect(page.sections.map((el) => el.textContent.trim())).toEqual([
        'Installation details',
        'Fuels and equipment inventory',
        'Calculation of CO2 emissions',
        'Measurement of CO2 emissions',
        'Inherent CO2 emissions',
        'Fallback approach emissions',
        'Calculation of perfluorocarbons (PFC) emissions',
        'Emissions summary',
        'Additional information',

        'Verifier details',
        'Opinion statement',
        'Compliance with ETS rules',
        'Compliance with monitoring and reporting principles',
        'Overall decision',

        'Uncorrected misstatements',
        'Uncorrected non-conformities',
        'Uncorrected non-compliances',
        'Recommended improvements',
        'Methodologies to close data gaps',
        'Further information of relevance to the opinion',
        'Summary of conditions, changes, clarifications and variations',
      ]);
    });
  });

  describe('for review without verification report', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      jest.spyOn(store, 'storeInitialized$', 'get').mockReturnValue(of(true));
      jest.spyOn(store, 'requestMetadata$', 'get').mockReturnValue(
        of({
          type: 'AER',
          year: '2022',
        }),
      );
      jest.spyOn(store, 'requestTaskItem$', 'get').mockReturnValue(
        of({
          requestTask: {
            id: 1,
            type: 'AER_APPLICATION_REVIEW',
            payload: {
              ...mockReview,
              verificationReport: null,
            },
          },
          allowedRequestTaskActions: [],
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the content', () => {
      expect(page.heading).toEqual('Review 2022 emissions report');
      expect(page.sections.map((el) => el.textContent.trim())).toEqual([
        'Installation details',
        'Fuels and equipment inventory',
        'Calculation of CO2 emissions',
        'Measurement of CO2 emissions',
        'Inherent CO2 emissions',
        'Fallback approach emissions',
        'Calculation of perfluorocarbons (PFC) emissions',
        'Emissions summary',
        'Additional information',
      ]);
    });
  });

  describe('for review with verification report and non hidden ALR', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      jest.spyOn(store, 'storeInitialized$', 'get').mockReturnValue(of(true));
      jest.spyOn(store, 'requestMetadata$', 'get').mockReturnValue(
        of({
          type: 'AER',
          year: '2022',
        }),
      );
      jest.spyOn(store, 'requestTaskItem$', 'get').mockReturnValue(
        of({
          requestTask: {
            id: 1,
            type: 'AER_APPLICATION_REVIEW',
            payload: { ...mockReview, permitOriginatedData: { permitType: 'GHGE', isPostALRSectionRemoval: null } },
          },
          allowedRequestTaskActions: [],
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the content', () => {
      expect(page.heading).toEqual('Review 2022 emissions report');
      expect(page.sections.map((el) => el.textContent.trim())).toEqual([
        'Installation details',
        'Fuels and equipment inventory',
        'Calculation of CO2 emissions',
        'Measurement of CO2 emissions',
        'Inherent CO2 emissions',
        'Fallback approach emissions',
        'Calculation of perfluorocarbons (PFC) emissions',
        'Emissions summary',
        'Activity level report',
        'Additional information',

        'Verifier details',
        'Opinion statement',
        'Compliance with ETS rules',
        'Compliance with monitoring and reporting principles',
        'Verification report of the activity level report',
        'Overall decision',

        'Uncorrected misstatements',
        'Uncorrected non-conformities',
        'Uncorrected non-compliances',
        'Recommended improvements',
        'Methodologies to close data gaps',
        'Materiality level and reference documents',
        'Summary of conditions, changes, clarifications and variations',
      ]);
    });
  });
});
