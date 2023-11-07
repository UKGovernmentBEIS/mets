import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { mockState } from '../testing/mock-aer-reviewed';
import { VirActionReviewedModule } from '../vir-action-reviewed.module';
import { ResponseListComponent } from './response-list.component';

describe('ResponseListComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: ResponseListComponent;
  let fixture: ComponentFixture<ResponseListComponent>;

  class Page extends BasePage<ResponseListComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get tasksContents(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('h2, .app-task-list__task-name'));
    }

    get operatorResponseDataItems() {
      return this.queryAll('app-operator-response-data-item');
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

    fixture = TestBed.createComponent(ResponseListComponent);
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
    expect(page.tasksContents.map((el) => el.textContent.trim())).toEqual([
      'B1: an uncorrected error in the monitoring plan',
      'Respond to operator',
      'D1: recommended improvement',
      'Respond to operator',
      'E1: an unresolved breach from a previous year',
      'Respond to operator',
      'Create report summary',
      'Create summary',
    ]);
    expect(page.operatorResponseDataItems).toHaveLength(3);
  });
});
