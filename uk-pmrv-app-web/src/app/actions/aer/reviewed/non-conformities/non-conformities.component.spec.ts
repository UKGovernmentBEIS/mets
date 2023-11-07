import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateCompleted } from '../testing/mock-aer-completed';
import { NonConformitiesComponent } from './non-conformities.component';

describe('UncorrectedNonConformitiesComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: NonConformitiesComponent;
  let fixture: ComponentFixture<NonConformitiesComponent>;
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'UNCORRECTED_NON_CONFORMITIES',
    },
  );

  class Page extends BasePage<NonConformitiesComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get nonConformitiesGroup() {
      return this.query('app-non-conformities-group');
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
    fixture = TestBed.createComponent(NonConformitiesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all html elements', () => {
    expect(page.heading).toEqual('Uncorrected non-conformities');
    expect(page.nonConformitiesGroup).toBeTruthy();
  });
});
