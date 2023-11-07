import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockState } from '../testing/mock-aer-submitted';
import { FuelsComponent } from './fuels.component';

describe('FuelsComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: FuelsComponent;
  let fixture: ComponentFixture<FuelsComponent>;

  class Page extends BasePage<FuelsComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get tableValues() {
      return this.queryAll<HTMLDListElement>('tr').map((naceCode) =>
        Array.from(naceCode.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
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
    store.setState(mockState);

    fixture = TestBed.createComponent(FuelsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Fuels and equipment inventory');

    expect(page.tableValues).toEqual([
      [],
      ['the reference', 'Anthracite', 'Ammonia: Fuel as process input'],
      ['the other reference', 'Biodiesels', 'Cement clinker: CKD'],
      [],
      ['emission source 1 reference', 'emission source 1 description'],
      ['emission source 2 reference', 'emission source 2 description'],
      [],
      ['EP1', 'west side chimney'],
      ['EP2', 'east side chimney'],
    ]);
  });
});
