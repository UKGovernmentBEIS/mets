import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { CommonActionsStore } from '../../store/common-actions.store';
import { SubmittedComponent } from './submitted.component';
import { mockState } from './testing/mock-aer-submitted';
import { VirActionSubmittedModule } from './vir-action-submitted.module';

describe('SubmittedComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: SubmittedComponent;
  let fixture: ComponentFixture<SubmittedComponent>;

  class Page extends BasePage<SubmittedComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get tasksContents(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('h2, .app-task-list__task-name'));
    }

    get verificationDataItems() {
      return this.queryAll('app-verification-data-item');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirActionSubmittedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(SubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.heading).toEqual('2023 verifier improvement report submitted');
    expect(page.tasksContents.map((el) => el.textContent.trim())).toEqual([
      'B1: an uncorrected error in the monitoring plan',
      'Respond to recommendation',
      'D1: recommended improvement',
      'Respond to recommendation',
      'E1: an unresolved breach from a previous year',
      'Respond to recommendation',
    ]);
    expect(page.verificationDataItems).toHaveLength(3);
  });
});
