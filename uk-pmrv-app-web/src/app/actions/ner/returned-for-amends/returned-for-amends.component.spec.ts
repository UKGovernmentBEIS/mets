import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonActionsState } from '@actions/store/common-actions.state';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { AuthStore } from '@core/store';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { NerReturnedForAmendsComponent } from './returned-for-amends.component';

describe('NerReturnedForAmendsComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: NerReturnedForAmendsComponent;
  let fixture: ComponentFixture<NerReturnedForAmendsComponent>;
  let authStore: AuthStore;

  class Page extends BasePage<NerReturnedForAmendsComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1')?.textContent.trim();
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      providers: [provideRouter([]), provideHttpClient()],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'REGULATOR' });

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      storeInitialized: true,
      action: {
        type: 'NER_APPLICATION_RETURNED_FOR_AMENDS',
        requestId: 'NER00012-5',
        payload: {
          payloadType: 'NER_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD',
          regulatorReviewGroupDecisions: {
            NER: {
              type: 'OPERATOR_AMENDS_NEEDED',
              details: {
                notes: 'fgh',
              },
              reviewDataType: 'NER_DATA',
            },
          },
        },
      },
    } as CommonActionsState);

    fixture = TestBed.createComponent(NerReturnedForAmendsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('New entrance reserve returned for amendments');
    expect(page.summaryListValues).toHaveLength(2);
    expect(page.summaryListValues).toEqual([
      ['Changes required', ''],
      ['Notes', 'fgh'],
    ]);
  });
});
