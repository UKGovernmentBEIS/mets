import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { CommonActionsStore } from '../../store/common-actions.store';
import { AerModule } from '../aer.module';
import { ReviewedComponent } from './reviewed.component';
import { mockStateCompleted } from './testing/mock-aer-completed';

describe('ReviewedComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: ReviewedComponent;
  let fixture: ComponentFixture<ReviewedComponent>;

  class Page extends BasePage<ReviewedComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item > .app-task-list__task-name'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockStateCompleted);

    fixture = TestBed.createComponent(ReviewedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task list', () => {
    expect(page.sections.map((el) => el.textContent.trim())).toEqual([
      'Installation details',
      'Fuels and equipment inventory',
      'Calculation of CO2 emissions',
      'Measurement of CO2 emissions',
      'Inherent CO2 emissions',
      'Fallback approach emissions',
      'Emissions summary',
      'Additional information',
      'Verifier details',
      'Opinion statement',
      'Compliance with ETS rules',
      'Compliance with monitoring and reporting principles',
      'Overall decision',
      'Uncorrected misstatements',
      'Uncorrected non-conformities',
      'Uncorrected non-compliances',
      'Recommended improvements',
      'Methodologies to close data gaps',
      'Materiality level and reference documents',
      'Summary of conditions, changes, clarifications and variations',
    ]);
  });
});
