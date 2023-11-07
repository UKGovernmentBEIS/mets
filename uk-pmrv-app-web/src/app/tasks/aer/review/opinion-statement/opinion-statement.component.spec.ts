import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { mockState } from '@tasks/aer/review/testing/mock-review';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerModule } from '../../aer.module';
import { OpinionStatementComponent } from './opinion-statement.component';

describe('OpinionStatementComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: OpinionStatementComponent;
  let fixture: ComponentFixture<OpinionStatementComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'OPINION_STATEMENT',
    },
  );

  class Page extends BasePage<OpinionStatementComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get opinionStatement() {
      return this.query('app-opinion-statement-group');
    }

    get verificationReviewGroupDecision() {
      return this.query('app-verification-review-group-decision');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, AerModule],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(OpinionStatementComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all html elements', () => {
    expect(page.heading).toEqual('Opinion statement');
    expect(page.verificationReviewGroupDecision).toBeTruthy();
    expect(page.opinionStatement).toBeTruthy();
  });
});
