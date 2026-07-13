import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { HseTiService } from '../core';
import { hsetiMockReviewState } from '../test/mock-review';
import { ReviewTaskListComponent } from './review-task-list.component';

describe('ReviewTaskListComponent', () => {
  let component: ReviewTaskListComponent;
  let fixture: ComponentFixture<ReviewTaskListComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<ReviewTaskListComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get operatorText(): HTMLDivElement {
      return this.query<HTMLDivElement>('app-task-header-info > div:nth-child(1)');
    }

    get daysRemainingText(): HTMLDivElement {
      return this.query<HTMLDivElement>('app-task-header-info > div:nth-child(2)');
    }

    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item .app-task-list__task-name'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewTaskListComponent],
      providers: [HseTiService, CapitalizeFirstPipe, ItemNamePipe, provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(hsetiMockReviewState);

    fixture = TestBed.createComponent(ReviewTaskListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('Review 2021-2025 HSE target increase application');
    expect(page.operatorText.textContent.trim()).toEqual('Assigned to: Regulator England');
    expect(page.daysRemainingText.textContent.trim()).toEqual('Days Remaining: Overdue');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual([
      'HSE target increase details',
      'Overall decision',
    ]);
  });
});
