import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateCompleted } from '../testing/mock-aer-completed';
import { InherentCo2Component } from './inherent-co2.component';

describe('InherentCo2Component', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: InherentCo2Component;
  let fixture: ComponentFixture<InherentCo2Component>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'INHERENT_CO2',
    },
  );

  class Page extends BasePage<InherentCo2Component> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get inherentCo2EmissionsGroup() {
      return this.queryAll<HTMLDivElement>('app-inherent-co2-group');
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
    store.setState(mockStateCompleted);

    fixture = TestBed.createComponent(InherentCo2Component);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Inherent CO2 emissions');
    expect(page.inherentCo2EmissionsGroup).toBeTruthy();
  });
});
