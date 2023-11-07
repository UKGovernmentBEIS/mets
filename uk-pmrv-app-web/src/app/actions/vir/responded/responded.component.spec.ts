import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../store/common-actions.store';
import { RespondedComponent } from './responded.component';
import { mockState } from './testing/mock-aer-responded';
import { VirActionRespondedModule } from './vir-action-responded.module';

describe('RespondedComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: RespondedComponent;
  let fixture: ComponentFixture<RespondedComponent>;

  class Page extends BasePage<RespondedComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get verificationItem() {
      return this.query('app-verification-recommendation-item');
    }

    get operatorResponseItem() {
      return this.query('app-operator-response-item');
    }

    get regulatorResponseItem() {
      return this.query('app-regulator-response-item');
    }

    get operatorFollowupItem() {
      return this.query('app-operator-followup-item');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirActionRespondedModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(RespondedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('Follow up response to B1');
    expect(page.verificationItem).toBeTruthy();
    expect(page.operatorResponseItem).toBeTruthy();
    expect(page.regulatorResponseItem).toBeTruthy();
    expect(page.operatorFollowupItem).toBeTruthy();
  });
});
