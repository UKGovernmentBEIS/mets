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

import { NonConformitiesComponent } from './non-conformities.component';

describe('NonConformitiesComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: NonConformitiesComponent;
  let fixture: ComponentFixture<NonConformitiesComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'UNCORRECTED_NON_CONFORMITIES',
    },
  );

  class Page extends BasePage<NonConformitiesComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get nonConformitiesGroup() {
      return this.query('app-non-conformities-group');
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
    fixture = TestBed.createComponent(NonConformitiesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all html elements', () => {
    expect(page.heading).toEqual('Uncorrected non-conformities');
    expect(page.verificationReviewGroupDecision).toBeTruthy();
    expect(page.nonConformitiesGroup).toBeTruthy();
  });
});
