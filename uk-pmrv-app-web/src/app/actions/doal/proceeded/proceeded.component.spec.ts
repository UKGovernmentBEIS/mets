import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { CommonActionsStore } from '../../store/common-actions.store';
import { DoalActionModule } from '../doal-action.module';
import { ProceededComponent } from './proceeded.component';
import { mockState } from './testing/mock-doal-proceeded';

describe('ProceededComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: ProceededComponent;
  let fixture: ComponentFixture<ProceededComponent>;

  class Page extends BasePage<ProceededComponent> {
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

    fixture = TestBed.createComponent(ProceededComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.heading).toEqual('Activity level determination sent to UK Authority');
    expect(page.tasksContents).toEqual([
      'Details',
      'Web view',
      'View details',
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
