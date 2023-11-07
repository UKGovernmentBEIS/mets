import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../store/common-actions.store';
import { AirActionRespondedModule } from './air-action-responded.module';
import { RespondedComponent } from './responded.component';
import { mockState } from './testing/mock-air-responded';

describe('RespondedComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: RespondedComponent;
  let fixture: ComponentFixture<RespondedComponent>;

  class Page extends BasePage<RespondedComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get airImprovementItem() {
      return this.query('app-air-improvement-item');
    }

    get airOperatorResponseItem() {
      return this.query('app-air-operator-response-item');
    }

    get airRegulatorResponseItem() {
      return this.query('app-air-regulator-response-item');
    }

    get airOperatorFollowupItem() {
      return this.query('app-air-operator-followup-item');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AirActionRespondedModule, RouterTestingModule],
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
    expect(page.heading1.textContent.trim()).toEqual('Follow up response');
    expect(page.airImprovementItem).toBeTruthy();
    expect(page.airOperatorResponseItem).toBeTruthy();
    expect(page.airRegulatorResponseItem).toBeTruthy();
    expect(page.airOperatorFollowupItem).toBeTruthy();
  });
});
