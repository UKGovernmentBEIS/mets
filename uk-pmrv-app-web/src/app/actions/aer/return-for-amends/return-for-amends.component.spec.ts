import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { AuthStore } from '../../../core/store';
import { CommonActionsState } from '../../store/common-actions.state';
import { CommonActionsStore } from '../../store/common-actions.store';
import { AerModule } from '../aer.module';
import { ReturnForAmendsComponent } from './return-for-amends.component';

describe('ReturnForAmendsComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: ReturnForAmendsComponent;
  let fixture: ComponentFixture<ReturnForAmendsComponent>;
  let authStore: AuthStore;

  class Page extends BasePage<ReturnForAmendsComponent> {
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
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'REGULATOR' });

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      storeInitialized: true,
      action: {
        type: 'AER_APPLICATION_RETURNED_FOR_AMENDS',
        requestId: 'AEM00019-2022',
        payload: {
          payloadType: 'AER_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD',
          reviewGroupDecisions: {
            FUELS_AND_EQUIPMENT: {
              type: 'OPERATOR_AMENDS_NEEDED',
              details: {
                notes: 'fgh',
              },
              reviewDataType: 'AER_DATA',
            },
          },
        },
      },
    } as CommonActionsState);

    fixture = TestBed.createComponent(ReturnForAmendsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('2022 emissions report returned for amends');
    expect(page.summaryListValues).toHaveLength(2);
    expect(page.summaryListValues).toEqual([
      ['Changes required', ''],
      ['Notes', 'fgh'],
    ]);
  });
});
