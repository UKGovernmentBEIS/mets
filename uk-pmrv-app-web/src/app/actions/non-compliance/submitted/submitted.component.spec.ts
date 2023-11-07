import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { CommonActionsStore } from '../../store/common-actions.store';
import { NonComplianceModule } from '../non-compliance.module';
import { mockState } from '../testing/mock-non-compliance-submitted';
import { SubmittedComponent } from './submitted.component';

describe('SubmittedComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: SubmittedComponent;
  let fixture: ComponentFixture<SubmittedComponent>;

  class Page extends BasePage<SubmittedComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item > .app-task-list__task-name'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonComplianceModule, RouterTestingModule],
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

  it('should show task list', () => {
    expect(page.sections.map((el) => el.textContent.trim())).toEqual(['Details of breach']);
  });
});
