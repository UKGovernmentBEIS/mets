import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockState } from '../testing/mock-aer-submitted';
import { SourceStreamsComponent } from './source-streams.component';

describe('SourceStreamsComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: SourceStreamsComponent;
  let fixture: ComponentFixture<SourceStreamsComponent>;

  class Page extends BasePage<SourceStreamsComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get sourceStreams() {
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

    fixture = TestBed.createComponent(SourceStreamsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Source streams (fuels and materials)');
    expect(page.sourceStreams).toEqual([
      [],
      ['the reference', 'Anthracite', 'Ammonia: Fuel as process input'],
      ['the other reference', 'Biodiesels', 'Cement clinker: CKD'],
    ]);
  });
});
