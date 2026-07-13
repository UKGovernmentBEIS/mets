import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ReviewComponent } from '@tasks/bdrs2/review';
import { mockReview } from '@tasks/bdrs2/review/testing/mock-state';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

describe('ReviewComponent', () => {
  let component: ReviewComponent;
  let fixture: ComponentFixture<ReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<ReviewComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item .app-task-list__task-name'));
    }
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([])],
      imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule, ReviewComponent],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(ReviewComponent);
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
          type: 'BDRS2',
          year: '2022',
        }),
      );
      jest.spyOn(store, 'requestTaskItem$', 'get').mockReturnValue(
        of({
          requestTask: {
            id: 1,
            type: 'BDRS2_APPLICATION_REGULATOR_REVIEW_SUBMIT',
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
      expect(page.heading).toEqual('Review 2022 stage 2 baseline data report');
      expect(page.sections.map((el) => el.textContent.trim())).toEqual([
        'Stage 2 baseline data report and details',
        'Stage 2 BDR verification opinion statement',
        'Overall decision',
        'Outcome of regulator review',
      ]);
    });
  });

  describe('for review without verification report', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      jest.spyOn(store, 'storeInitialized$', 'get').mockReturnValue(of(true));
      jest.spyOn(store, 'requestMetadata$', 'get').mockReturnValue(
        of({
          type: 'BDRS2',
          year: '2022',
        }),
      );
      jest.spyOn(store, 'requestTaskItem$', 'get').mockReturnValue(
        of({
          requestTask: {
            id: 1,
            type: 'BDRS2_APPLICATION_REGULATOR_REVIEW_SUBMIT',
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
      expect(page.heading).toEqual('Review 2022 stage 2 baseline data report');
      expect(page.sections.map((el) => el.textContent.trim())).toEqual([
        'Stage 2 baseline data report and details',
        'Outcome of regulator review',
      ]);
    });
  });
});
