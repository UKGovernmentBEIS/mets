import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { DoalActionModule } from '../../doal-action.module';
import { mockState } from '../../proceeded/testing/mock-doal-proceeded';
import { OperatorReportComponent } from './operator-report.component';

describe('OperatorReportComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: OperatorReportComponent;
  let fixture: ComponentFixture<OperatorReportComponent>;

  class Page extends BasePage<OperatorReportComponent> {
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
      imports: [DoalActionModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(OperatorReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Upload operator activity level report');
    expect(page.summaryListValues).toHaveLength(3);
    expect(page.summaryListValues).toEqual([
      ['Uploaded file', '1.png'],
      ['Has the regulator estimated activity levels?', 'No'],
      ['Comments', 'Operator activity level report comment'],
    ]);
  });
});
