import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateReviewed } from '../testing/mock-aer-submitted';
import { NonConformitiesComponent } from './non-conformities.component';

describe('NonConformitiesComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: NonConformitiesComponent;
  let fixture: ComponentFixture<NonConformitiesComponent>;

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
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockStateReviewed);
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
