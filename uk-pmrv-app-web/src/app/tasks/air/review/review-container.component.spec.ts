import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AirTaskReviewModule } from '@tasks/air/review/air-task-review.module';
import { mockStateReview } from '@tasks/air/review/testing/mock-air-application-review-payload';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ReviewContainerComponent } from './review-container.component';

describe('ReviewContainerComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: ReviewContainerComponent;
  let fixture: ComponentFixture<ReviewContainerComponent>;
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ReviewContainerComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get operatorText(): HTMLDivElement {
      return this.query<HTMLDivElement>('app-task-header-info > div:nth-child(1)');
    }

    get airImprovementDataGroupReview() {
      return this.query('app-air-improvement-data-group-review');
    }

    get taskListContent(): string[] {
      return this.queryAll('.govuk-grid-column-full > ul.app-task-list__items').map((el) => el.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AirTaskReviewModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockStateReview);
    fixture = TestBed.createComponent(ReviewContainerComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('2022 Annual improvement report review');
    expect(page.operatorText.textContent.trim()).toEqual('Assigned to: Regulator1 England');
    expect(page.airImprovementDataGroupReview).toBeTruthy();
    expect(page.taskListContent).toEqual(['Provide summary of improvements for official notice completed']);
  });
});
