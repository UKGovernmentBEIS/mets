import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../../store/common-actions.store';
import { DoalActionModule } from '../../../doal-action.module';
import { mockState } from '../../testing/mock-doal-completed';
import { DateSubmittedComponent } from './date-submitted.component';

describe('DateSubmittedComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: DateSubmittedComponent;
  let fixture: ComponentFixture<DateSubmittedComponent>;

  class Page extends BasePage<DateSubmittedComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element?.textContent.trim() ?? ''));
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

    fixture = TestBed.createComponent(DateSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Provide the date application was submitted to UK authorities');
    expect(page.summaryListValues).toHaveLength(1);
    expect(page.summaryListValues).toEqual([
      ['When was the relevant information submitted to the authority?', '12 Mar 2023'],
    ]);
  });
});
