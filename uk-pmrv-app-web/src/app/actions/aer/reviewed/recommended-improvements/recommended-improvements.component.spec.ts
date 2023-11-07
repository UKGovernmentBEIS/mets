import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateCompleted } from '../testing/mock-aer-completed';
import { RecommendedImprovementsComponent } from './recommended-improvements.component';

describe('RecommendedImprovementsComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: RecommendedImprovementsComponent;
  let fixture: ComponentFixture<RecommendedImprovementsComponent>;
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'RECOMMENDED_IMPROVEMENTS',
    },
  );

  class Page extends BasePage<RecommendedImprovementsComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get recommendedImprovementsGroup() {
      return this.query('app-recommended-improvements-group');
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
    fixture = TestBed.createComponent(RecommendedImprovementsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all html elements', () => {
    expect(page.heading).toEqual('Recommended improvements');
    expect(page.recommendedImprovementsGroup).toBeTruthy();
  });
});
