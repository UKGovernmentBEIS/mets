import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { HseTiModule } from '@tasks/hseti/hseti.module';

import { BasePage } from '../../../../testing';
import { AuthStore } from '../../../core/store';
import { CommonActionsState } from '../../store/common-actions.state';
import { CommonActionsStore } from '../../store/common-actions.store';
import { HsetiReturnedForAmendsComponent } from './returned-for-amends.component';

describe('HsetiReturnedForAmendsComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: HsetiReturnedForAmendsComponent;
  let fixture: ComponentFixture<HsetiReturnedForAmendsComponent>;
  let authStore: AuthStore;

  class Page extends BasePage<HsetiReturnedForAmendsComponent> {
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
      imports: [HseTiModule],
      providers: [provideRouter([])],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'REGULATOR' });

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      storeInitialized: true,
      action: {
        type: 'HSE_TI_REGULATOR_REVIEW_RETURNED_FOR_AMENDS',
        requestId: 'HSETI00164-2021_2025',
        payload: {
          payloadType: 'HSE_TI_REGULATOR_REVIEW_RETURNED_FOR_AMENDS_PAYLOAD',
          regulatorReviewGroupDecisions: {
            HSETI: {
              type: 'OPERATOR_AMENDS_NEEDED',
              details: {
                notes: 'fgh',
              },
            },
          },
        },
      },
    } as CommonActionsState);

    fixture = TestBed.createComponent(HsetiReturnedForAmendsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('2021-2025 HSE target increase details returned for amends');
    expect(page.summaryListValues).toHaveLength(2);
    expect(page.summaryListValues).toEqual([
      ['Changes required', ''],
      ['Notes', 'fgh'],
    ]);
  });
});
