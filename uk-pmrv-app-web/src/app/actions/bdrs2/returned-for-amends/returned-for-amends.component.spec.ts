import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonActionsState } from '@actions/store/common-actions.state';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { AuthStore } from '@core/store';
import { BdrS2Module } from '@tasks/bdrs2/bdrs2.module';
import { BasePage } from '@testing';

import { Bdrs2ReturnedForAmendsComponent } from './returned-for-amends.component';

describe('Bdrs2ReturnedForAmendsComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: Bdrs2ReturnedForAmendsComponent;
  let fixture: ComponentFixture<Bdrs2ReturnedForAmendsComponent>;
  let authStore: AuthStore;

  class Page extends BasePage<Bdrs2ReturnedForAmendsComponent> {
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
      imports: [BdrS2Module],
      providers: [provideRouter([])],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'REGULATOR' });

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      storeInitialized: true,
      action: {
        type: 'BDRS2_REGULATOR_REVIEW_RETURNED_FOR_AMENDS',
        requestId: 'BDRS2-00019-2022',
        payload: {
          payloadType: 'BDRS2_REGULATOR_REVIEW_RETURNED_FOR_AMENDS_PAYLOAD',
          regulatorReviewGroupDecisions: {
            BDRS2: {
              type: 'OPERATOR_AMENDS_NEEDED',
              details: {
                notes: 'fgh',
              },
              reviewDataType: 'BDRS2_DATA',
            },
          },
        },
      },
    } as CommonActionsState);

    fixture = TestBed.createComponent(Bdrs2ReturnedForAmendsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('2022 stage 2 baseline data report returned for amendments');
    expect(page.summaryListValues).toHaveLength(2);
    expect(page.summaryListValues).toEqual([
      ['Changes required', ''],
      ['Notes', 'fgh'],
    ]);
  });
});
