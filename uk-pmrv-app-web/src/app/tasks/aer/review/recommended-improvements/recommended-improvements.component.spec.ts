import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AerModule } from '@tasks/aer/aer.module';
import { mockState } from '@tasks/aer/review/testing/mock-review';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RecommendedImprovementsComponent } from './recommended-improvements.component';

describe('RecommendedImprovementsComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: RecommendedImprovementsComponent;
  let fixture: ComponentFixture<RecommendedImprovementsComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'RECOMMENDED_IMPROVEMENTS',
    },
  );

  class Page extends BasePage<RecommendedImprovementsComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get recommendedImprovementsGroup() {
      return this.query('app-recommended-improvements-group');
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
    fixture = TestBed.createComponent(RecommendedImprovementsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all html elements', () => {
    expect(page.heading).toEqual('Recommended improvements');
    expect(page.verificationReviewGroupDecision).toBeTruthy();
    expect(page.recommendedImprovementsGroup).toBeTruthy();
  });
});
