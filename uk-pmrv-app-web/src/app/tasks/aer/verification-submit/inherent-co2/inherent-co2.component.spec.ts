import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerModule } from '@tasks/aer/aer.module';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { InherentCo2Component } from './inherent-co2.component';

describe('InherentCo2Component', () => {
  let component: InherentCo2Component;
  let fixture: ComponentFixture<InherentCo2Component>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<InherentCo2Component> {
    get inherentCo2Group() {
      return this.queryAll<HTMLDivElement>('app-inherent-co2-group');
    }
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(InherentCo2Component);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Inherent CO2 emissions');
    expect(page.inherentCo2Group).toBeTruthy();
  });
});
