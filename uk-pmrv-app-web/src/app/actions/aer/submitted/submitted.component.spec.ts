import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { CommonActionsStore } from '../../store/common-actions.store';
import { AerModule } from '../aer.module';
import { SubmittedComponent } from './submitted.component';
import { mockState } from './testing/mock-aer-submitted';

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
      imports: [AerModule, RouterTestingModule],
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
    expect(page.sections.map((el) => el.textContent.trim())).toEqual([
      'Installation and operator details',
      'Pollutant Release and Transfer Register codes (PRTR)',
      'NACE codes',
      'Regulated activities carried out at the installation',
      'Monitoring plan versions during the reporting year',
      'Monitoring approaches used during the reporting year',
      'Source streams (fuels and materials)',
      'Emission sources',
      'Emission points',
      'Calculation of CO2 emissions',
      'Measurement of CO2 emissions',
      'Inherent CO2 emissions',
      'Fallback approach emissions',
      'Emissions summary',
      'Abbreviations and definitions',
      'Additional documents and information',
      'Confidentiality statement',
    ]);
  });
});
