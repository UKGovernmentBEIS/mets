import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateReviewed } from '../testing/mock-aer-submitted';
import { NonCompliancesComponent } from './non-compliances.component';

describe('NonCompliancesComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: NonCompliancesComponent;
  let fixture: ComponentFixture<NonCompliancesComponent>;

  class Page extends BasePage<NonCompliancesComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get nonCompliancesGroup() {
      return this.query('app-non-compliances-group');
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
    fixture = TestBed.createComponent(NonCompliancesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all html elements', () => {
    expect(page.heading).toEqual('Uncorrected non-compliances');
    expect(page.nonCompliancesGroup).toBeTruthy();
  });
});
