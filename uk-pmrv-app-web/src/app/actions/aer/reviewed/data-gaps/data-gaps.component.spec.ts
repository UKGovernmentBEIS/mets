import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateCompleted } from '../testing/mock-aer-completed';
import { DataGapsComponent } from './data-gaps.component';

describe('DataGapsComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: DataGapsComponent;
  let fixture: ComponentFixture<DataGapsComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'CLOSE_DATA_GAPS_METHODOLOGIES',
    },
  );

  class Page extends BasePage<DataGapsComponent> {
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
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
      imports: [AerModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockStateCompleted);

    fixture = TestBed.createComponent(DataGapsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Methodologies to close data gaps');
    expect(page.summaryListValues).toHaveLength(3);
    expect(page.summaryListValues).toEqual([
      ['Was a data gap method required during the reporting year?', 'No'],
      ['Decision status', 'Accepted'],
      ['Notes', 'Notes'],
    ]);
  });
});
