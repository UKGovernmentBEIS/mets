import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { CommonActionsStore } from '../../store/common-actions.store';
import { ReviewedComponent } from './reviewed.component';
import { mockState } from './testing/mock-aer-reviewed';
import { VirActionReviewedModule } from './vir-action-reviewed.module';

describe('ReviewedComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: ReviewedComponent;
  let fixture: ComponentFixture<ReviewedComponent>;

  class Page extends BasePage<ReviewedComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get tasksContents() {
      return this.queryAll<HTMLElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirActionReviewedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(ReviewedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.heading).toEqual('2023 verifier improvement report decision submitted');
    expect(page.tasksContents).toEqual([
      'Details',
      'Verifier improvement report',
      'Verifier improvement report',
      'Official notice recipients',
      'Users',
      'Operator2 England, Operator admin - Secondary contact  Operator1 England, Operator admin - Financial contact, Primary contact, Service contact',
      'Name and signature on the official notice',
      'Regulator1 England',
      'Official notice',
      'recommended_improvements.pdf',
    ]);
  });
});
