import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { DoalActionModule } from '../../doal-action.module';
import { mockState } from '../../proceeded/testing/mock-doal-proceeded';
import { DeterminationComponent } from './determination.component';

describe('DeterminationComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: DeterminationComponent;
  let fixture: ComponentFixture<DeterminationComponent>;

  class Page extends BasePage<DeterminationComponent> {
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
      imports: [DoalActionModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(DeterminationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Provide determination of activity level');
    expect(page.summaryListValues).toHaveLength(7);
    expect(page.summaryListValues).toEqual([
      ['Decision', 'Proceed to UK ETS authority'],
      [
        'Reason for determination',
        'Article 6a of the Activity Level Changes Regulation (allocation adjustment under Article 5)',
      ],
      ['Information about the selected reasons', 'Reason'],
      ['Has a withholding of allowances notice been issued?', 'Yes'],
      ['When was the notice issued?', '10 Aug 2022'],
      ['Comments on the withholding of allowances notice', 'Withholding of allowances comment'],
      ['Send official notice?', 'Yes'],
    ]);
  });
});
