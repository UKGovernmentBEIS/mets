import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateCompleted } from '../testing/mock-aer-completed';
import { FallbackComponent } from './fallback.component';

describe('FallbackComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: FallbackComponent;
  let fixture: ComponentFixture<FallbackComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'FALLBACK',
    },
  );

  class Page extends BasePage<FallbackComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get fallbackEmissionsGroup() {
      return this.queryAll<HTMLDivElement>('app-fallback-emissions-group');
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

    fixture = TestBed.createComponent(FallbackComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Fallback approach emissions');
    expect(page.fallbackEmissionsGroup).toBeTruthy();
  });
});
