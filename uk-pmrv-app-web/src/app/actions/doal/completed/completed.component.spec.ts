import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { CommonActionsStore } from '../../store/common-actions.store';
import { DoalActionModule } from '../doal-action.module';
import { CompletedComponent } from './completed.component';
import { mockState } from './testing/mock-doal-completed';

describe('CompletedComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: CompletedComponent;
  let fixture: ComponentFixture<CompletedComponent>;

  class Page extends BasePage<CompletedComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get tasksContents() {
      return this.queryAll<HTMLElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DoalActionModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(CompletedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.heading).toEqual('Activity level determination accepted as approved with corrections');
    expect(page.tasksContents).toEqual([
      'Details',
      'Web view',
      'View details',
      'When did the Authority respond?',
      '12 Mar 2023',
      'Authority decision',
      'Approved with corrections',
      'Explanation of Authority decision for notice',
      'Decision notice',
      'Official notice recipients',
      'Users',
      'Operator2 England, Operator admin - Secondary contact  Operator1 England, Operator admin - Financial contact, Primary contact, Service contact',
      'Name and signature on the official notice',
      'Regulator1 England',
      'Official notice',
      'Activity_level_determination_preliminary_allocation_letter.pdf',
    ]);
  });
});
