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

import { FallbackComponent } from './fallback.component';

describe('FallbackComponent', () => {
  let component: FallbackComponent;
  let fixture: ComponentFixture<FallbackComponent>;
  let page: Page;
  let store: CommonTasksStore;

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
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, AerModule],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(FallbackComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Fallback approach emissions');
    expect(page.fallbackEmissionsGroup).toBeTruthy();
  });
});
