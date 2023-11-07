import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { mockStateReview } from '@tasks/vir/review/testing/mock-vir-application-review-payload';
import { VirTaskReviewModule } from '@tasks/vir/review/vir-task-review.module';
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

    get verificationDataGroupReview() {
      return this.query('app-verification-data-group-review');
    }

    get taskListContent(): string[] {
      return this.queryAll('.govuk-grid-column-full > ul.app-task-list__items').map((el) => el.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirTaskReviewModule, RouterTestingModule],
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
    expect(page.heading1.textContent.trim()).toEqual('Review 2022 verifier improvement report');
    expect(page.operatorText.textContent.trim()).toEqual('Assigned to: Regulator1 England');
    expect(page.verificationDataGroupReview).toBeTruthy();
    expect(page.taskListContent).toEqual(['Create summary completed', 'Send to the operator not started']);
  });
});
