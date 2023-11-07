import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateCompleted } from '../testing/mock-aer-completed';
import { MaterialityLevelComponent } from './materiality-level.component';

describe('MaterialityLevelComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: MaterialityLevelComponent;
  let fixture: ComponentFixture<MaterialityLevelComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'MATERIALITY_LEVEL',
    },
  );

  class Page extends BasePage<MaterialityLevelComponent> {
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

    fixture = TestBed.createComponent(MaterialityLevelComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Materiality level and reference documents');
    expect(page.summaryListValues).toHaveLength(4);
    expect(page.summaryListValues).toEqual([
      ['Materiality level', 'Materiality details'],
      [
        'Accreditation reference documents',
        'EA-6/03 European Co-operation for Accreditation Guidance for the Recognition of Verifiers under EU ETS Directive',
      ],
      ['Decision status', 'Accepted'],
      ['Notes', 'Notes'],
    ]);
  });
});
