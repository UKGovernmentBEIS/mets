import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockState } from '../testing/mock-aer-submitted';
import { AbbreviationsComponent } from './abbreviations.component';

describe('AbbreviationsComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: AbbreviationsComponent;
  let fixture: ComponentFixture<AbbreviationsComponent>;

  class Page extends BasePage<AbbreviationsComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(AbbreviationsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Abbreviations and definitions');
    expect(page.summaryListValues).toHaveLength(4);
    expect(page.summaryListValues).toEqual([
      ['Abbreviation, acronym or terminology', 'Mr'],
      ['Definition', 'Mister'],
      ['Abbreviation, acronym or terminology', 'Ms'],
      ['Definition', 'Miss'],
    ]);
  });
});
