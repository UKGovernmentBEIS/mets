import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AirActionReviewedModule } from '../air-action-reviewed.module';
import { mockState } from '../testing/mock-air-reviewed';
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

    get airOperatorResponseDataItems() {
      return this.queryAll('app-air-operator-response-data-item');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AirActionReviewedModule, RouterTestingModule],
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
    expect(page.heading).toEqual('2023 Annual improvement report decision submitted');
    expect(page.tasksContents.map((el) => el.textContent.trim())).toEqual([
      'Item 1: F1: Acetylene: major: emission factor',
      'Review information about this improvement',
      'Item 2: F1: Acetylene: major: emission factor',
      'Review information about this improvement',
      'Item 3: EP1: West side chimney: major',
      'Review information about this improvement',
      'Review summary',
      'Provide summary of improvements for official notice',
    ]);
    expect(page.airOperatorResponseDataItems).toHaveLength(3);
  });
});
