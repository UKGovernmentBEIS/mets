import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { BdrModule } from '@tasks/bdr/bdr.module';

import { BasePage } from '../../../../testing';
import { AuthStore } from '../../../core/store';
import { CommonActionsState } from '../../store/common-actions.state';
import { CommonActionsStore } from '../../store/common-actions.store';
import { ReturnedForAmendsComponent } from './returned-for-amends.component';

describe('ReturnedForAmendsComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: ReturnedForAmendsComponent;
  let fixture: ComponentFixture<ReturnedForAmendsComponent>;
  let authStore: AuthStore;

  class Page extends BasePage<ReturnedForAmendsComponent> {
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
      imports: [BdrModule],
      providers: [provideRouter([])],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'REGULATOR' });

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      storeInitialized: true,
      action: {
        type: 'BDR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS',
        requestId: 'BDR00019-2022',
        payload: {
          payloadType: 'BDR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS_PAYLOAD',
          regulatorReviewGroupDecisions: {
            BDR: {
              type: 'OPERATOR_AMENDS_NEEDED',
              details: {
                notes: 'fgh',
              },
              reviewDataType: 'BDR_DATA',
            },
          },
        },
      },
    } as CommonActionsState);

    fixture = TestBed.createComponent(ReturnedForAmendsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('2022 baseline data report returned for amends');
    expect(page.summaryListValues).toHaveLength(2);
    expect(page.summaryListValues).toEqual([
      ['Changes required', ''],
      ['Notes', 'fgh'],
    ]);
  });
});
