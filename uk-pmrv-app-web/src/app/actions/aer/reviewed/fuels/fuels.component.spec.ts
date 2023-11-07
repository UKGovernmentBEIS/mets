import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateCompleted } from '../testing/mock-aer-completed';
import { FuelsComponent } from './fuels.component';

describe('FuelsComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: FuelsComponent;
  let fixture: ComponentFixture<FuelsComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'FUELS_AND_EQUIPMENT',
    },
  );

  class Page extends BasePage<FuelsComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get tableValues() {
      return this.queryAll<HTMLDListElement>('tr').map((naceCode) =>
        Array.from(naceCode.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
      imports: [AerModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockStateCompleted);

    fixture = TestBed.createComponent(FuelsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Fuels and equipment inventory');
    expect(page.summaryListValues).toHaveLength(2);
    expect(page.summaryListValues).toEqual([
      ['Decision status', 'Accepted'],
      ['Notes', 'Notes'],
    ]);

    expect(page.tableValues).toEqual([
      [],
      ['the reference', 'Anthracite', 'Ammonia: Fuel as process input'],
      ['the other reference', 'Biodiesels', 'Cement clinker: CKD'],
      [],
      ['emission source 1 reference', 'emission source 1 description'],
      ['emission source 2 reference', 'emission source 2 description'],
      [],
      ['EP1', 'west side chimney'],
      ['EP2', 'east side chimney'],
    ]);
  });
});
