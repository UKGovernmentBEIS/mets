import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockState } from '../testing/mock-aer-submitted';
import { RegulatedActivitiesComponent } from './regulated-activities.component';

describe('RegulatedActivitiesComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: RegulatedActivitiesComponent;
  let fixture: ComponentFixture<RegulatedActivitiesComponent>;

  class Page extends BasePage<RegulatedActivitiesComponent> {
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
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(RegulatedActivitiesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Regulated activities carried out at the installation');
    expect(page.summaryListValues).toHaveLength(3);
    expect(page.summaryListValues).toEqual([
      ['Regulated activity', 'Ammonia production (Carbon dioxide)'],
      ['Capacity', '100 kVA'],
      ['CRF codes', '1.A.1.a Public Electricity and Heat Production2.A.4 Other Process uses of Carbonates'],
    ]);
  });
});
