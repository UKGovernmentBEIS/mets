import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ReviewWaitComponent } from '@tasks/vir/review-wait/review-wait.component';
import { VirTaskReviewWaitModule } from '@tasks/vir/review-wait/vir-task-review-wait.module';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TaskSharedModule } from '../../shared/task-shared-module';
import { CommonTasksStore } from '../../store/common-tasks.store';

describe('ReviewWaitComponent', () => {
  let component: ReviewWaitComponent;
  let fixture: ComponentFixture<ReviewWaitComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<ReviewWaitComponent> {
    get content(): string {
      return this.query<HTMLElement>('.govuk-warning-text').textContent.trim();
    }
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirTaskReviewWaitModule, RouterTestingModule, TaskSharedModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    jest.spyOn(store, 'storeInitialized$', 'get').mockReturnValue(of(true));
    jest.spyOn(store, 'requestTaskItem$', 'get').mockReturnValue(
      of({
        id: 1,
        requestTask: {
          id: 1,
          type: 'VIR_WAIT_FOR_REVIEW',
        },
        allowedRequestTaskActions: [],
      }),
    );
    jest.spyOn(store, 'requestMetadata$', 'get').mockReturnValue(
      of({
        type: 'VIR',
        year: 2021,
      }),
    );

    fixture = TestBed.createComponent(ReviewWaitComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review content', () => {
    expect(page.heading).toEqual('2021 verifier improvement report');
    expect(page.content).toEqual('!Warning Waiting for the regulator to complete the review');
  });
});
