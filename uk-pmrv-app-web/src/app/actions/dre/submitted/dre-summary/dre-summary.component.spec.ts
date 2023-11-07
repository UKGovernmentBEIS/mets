import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { DreModule } from '../../dre.module';
import { mockState } from '../../testing/mock-dre-submitted';
import { DreSummaryComponent } from './dre-summary.component';

describe('DreSummaryComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: DreSummaryComponent;
  let fixture: ComponentFixture<DreSummaryComponent>;

  class Page extends BasePage<DreSummaryComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-heading-m').map((row) => row.querySelector('p'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DreModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(DreSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Reportable emissions');
    expect(page.summaryListValues).toHaveLength(6);
  });
});
