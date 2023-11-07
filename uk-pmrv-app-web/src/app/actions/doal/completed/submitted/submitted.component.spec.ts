import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { DoalActionModule } from '../../doal-action.module';
import { mockState } from '../testing/mock-doal-completed';
import { SubmittedComponent } from './submitted.component';

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
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DoalActionModule, RouterTestingModule],
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
    expect(page.heading).toEqual('Activity level determination accepted as approved with corrections');
    expect(page.tasksContents.map((el) => el.textContent.trim())).toEqual([
      'Enter details',
      'Provide the date application was submitted to UK authorities',
      'Provide UK ETS Authority response',
    ]);
  });
});
