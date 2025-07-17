import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub, BasePage } from '@testing';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateCompleted } from '../testing/mock-aer-completed';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'INSTALLATION_DETAILS',
    },
  );

  class Page extends BasePage<DetailsComponent> {
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
      providers: [{ provide: ActivatedRoute, useValue: route }],
      imports: [AerModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockStateCompleted);

    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Installation details');
    expect(page.summaryListValues).toHaveLength(15);
    expect(page.summaryListValues).toEqual([
      ['Installation name', 'onshore permit installation'],
      ['Site name', 'site name'],
      ['Installation address', `NN166712 Korinthou 4, Neo Psychiko, line 2 legal testAthens15452`],
      ['Company registration number', '88888'],
      ['Operator name', 'onshore permit'],
      ['Legal status', 'Limited Company'],
      ['Operator address', 'Korinthou 3, Neo Psychiko, line 2 legal test Athens15451'],
      ['Are emissions from the installation reported under the Pollutant Release and Transfer Register?', 'No'],
      ['Regulated activity', 'Ammonia production (Carbon dioxide)'],
      ['Capacity', '100 kVA'],
      ['CRF codes', '1.A.1.a Public Electricity and Heat Production2.A.4 Other Process uses of Carbonates'],
      ['Changes reported by the operator', 'the details of the deviation'],
      ['Approaches used', 'Calculation of CO2  Measurement of CO2  Fallback approach  Inherent CO2 emissions'],
      ['Decision status', 'Accepted'],
      ['Notes', 'Notes'],
    ]);
  });
});
