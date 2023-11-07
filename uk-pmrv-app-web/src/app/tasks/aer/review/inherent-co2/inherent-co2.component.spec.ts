import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AerModule } from '@tasks/aer/aer.module';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { InherentCo2Component } from './inherent-co2.component';

describe('InherentCo2Component', () => {
  let component: InherentCo2Component;
  let fixture: ComponentFixture<InherentCo2Component>;
  let page: Page;
  let store: CommonTasksStore;

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

    get inherentEmissionsGroup() {
      return this.queryAll<HTMLDivElement>('app-inherent-co2-group');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, AerModule],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

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
    expect(page.inherentEmissionsGroup).toBeTruthy();
  });
});
