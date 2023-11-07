import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { CommonActionsStore } from '../../store/common-actions.store';
import { AirActionSubmittedModule } from './air-action-submitted.module';
import { SubmittedComponent } from './submitted.component';
import { mockState } from './testing/mock-air-submitted';

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
      imports: [AirActionSubmittedModule, RouterTestingModule],
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
    expect(page.heading).toEqual('2023 Annual improvement report submitted');
    expect(page.tasksContents.map((el) => el.textContent.trim())).toEqual([
      'Item 1: F1: Acetylene: major: emission factor',
      'Provide information about this improvement',
      'Item 2: F1: Acetylene: major: emission factor',
      'Provide information about this improvement',
      'Item 3: EP1: West side chimney: major',
      'Provide information about this improvement',
      'Item 4: EP1: West side chimney: major',
      'Provide information about this improvement',
      'Item 5: F1: Acetylene: major',
      'Provide information about this improvement',
    ]);
  });
});
