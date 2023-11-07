import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { DoalActionModule } from '../../doal-action.module';
import { mockState } from '../../proceeded/testing/mock-doal-proceeded';
import { AlcInformationComponent } from './alc-information.component';

describe('AlcInformationComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: AlcInformationComponent;
  let fixture: ComponentFixture<AlcInformationComponent>;

  class Page extends BasePage<AlcInformationComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element?.textContent.trim() ?? ''));
    }
    get tableValues() {
      return this.queryAll<HTMLDListElement>('tr').map((naceCode) =>
        Array.from(naceCode.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
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

    fixture = TestBed.createComponent(AlcInformationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Provide information about this activity level change');
    expect(page.summaryListValues).toHaveLength(5);
    expect(page.summaryListValues).toEqual([
      ['Were conservative estimates made to determine the activity level?', 'Yes'],
      ['Why the estimate was made', 'Explain estimates'],
      ['Comments', 'Comments for UkEts authority comment'],
      ['Activity level details', ''],
      ['Allocation for each sub-installation details', ''],
    ]);

    expect(page.tableValues).toEqual([
      [],
      ['2025', 'Adipic acid', 'Increase', 'Changed activity level', 'Activity level 1 comment'],
      [],
      ['2025', 'Aluminium', '10'],
    ]);
  });
});
