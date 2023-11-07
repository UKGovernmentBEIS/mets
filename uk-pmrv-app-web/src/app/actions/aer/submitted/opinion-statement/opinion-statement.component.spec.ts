import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateReviewed } from '../testing/mock-aer-submitted';
import { OpinionStatementComponent } from './opinion-statement.component';

describe('OpinionStatementComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: OpinionStatementComponent;
  let fixture: ComponentFixture<OpinionStatementComponent>;

  class Page extends BasePage<OpinionStatementComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get opinionStatement() {
      return this.query('app-opinion-statement-group');
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
    fixture = TestBed.createComponent(OpinionStatementComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all html elements', () => {
    expect(page.heading).toEqual('Opinion statement');
    expect(page.opinionStatement).toBeTruthy();
  });
});
