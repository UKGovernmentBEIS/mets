import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockState } from '../testing/mock-aer-submitted';
import { InstallationDetailsComponent } from './installation-details.component';

describe('InstallationDetailsComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: InstallationDetailsComponent;
  let fixture: ComponentFixture<InstallationDetailsComponent>;

  class Page extends BasePage<InstallationDetailsComponent> {
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

    fixture = TestBed.createComponent(InstallationDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Installation and operator details');
    expect(page.summaryListValues).toHaveLength(7);
    expect(page.summaryListValues).toEqual([
      ['Installation name', 'onshore permit installation'],
      ['Site name', 'site name'],
      ['Installation address', 'NN166712 Korinthou 4, Neo Psychiko, line 2 legal testAthens15452'],
      ['Operator name', 'onshore permit'],
      ['Legal status', 'Limited Company'],
      ['Company registration number', '88888'],
      ['Operator address', 'Korinthou 3, Neo Psychiko, line 2 legal test Athens15451'],
    ]);
  });
});
