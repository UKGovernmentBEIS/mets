import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockState } from '../testing/mock-aer-submitted';
import { ApproachesComponent } from './approaches.component';

describe('ApproachesComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: ApproachesComponent;
  let fixture: ComponentFixture<ApproachesComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      taskKey: 'CALCULATION_CO2',
    },
  );

  class Page extends BasePage<ApproachesComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get emissions() {
      return this.queryAll<HTMLDListElement>('tr').map((naceCode) =>
        Array.from(naceCode.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(ApproachesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Calculation of CO2 emissions');
    expect(page.emissions).toEqual([
      [],
      ['the reference Anthracite', 'emission source 1 reference', '18', '0', ''],
      ['Total emissions', '', '18 tCO2e', '0 tCO2e', ''],
    ]);
  });
});
