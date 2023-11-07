import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AerModule } from '@tasks/aer/aer.module';
import { NonCompliancesComponent } from '@tasks/aer/review/non-compliances/non-compliances.component';
import { mockState } from '@tasks/aer/review/testing/mock-review';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('NonCompliancesComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: NonCompliancesComponent;
  let fixture: ComponentFixture<NonCompliancesComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'UNCORRECTED_NON_COMPLIANCES',
    },
  );

  class Page extends BasePage<NonCompliancesComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get nonCompliancesGroup() {
      return this.query('app-non-compliances-group');
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
    fixture = TestBed.createComponent(NonCompliancesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all html elements', () => {
    expect(page.heading).toEqual('Uncorrected non-compliances');
    expect(page.verificationReviewGroupDecision).toBeTruthy();
    expect(page.nonCompliancesGroup).toBeTruthy();
  });
});
