import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { EmissionPointsSummaryComponent } from './emission-points-summary.component';

describe('EmissionPointsSummaryComponent', () => {
  let component: EmissionPointsSummaryComponent;
  let fixture: ComponentFixture<EmissionPointsSummaryComponent>;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;

  class Page extends BasePage<EmissionPointsSummaryComponent> {
    get emissionPoints() {
      return this.queryAll<HTMLDListElement>('dl').map((emissionPoint) =>
        Array.from(emissionPoint.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EmissionPointsSummaryComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitIssuanceStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(EmissionPointsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of data', () => {
    expect(page.emissionPoints).toEqual([['The big Ref Emission point 1'], ['Yet another reference Point taken!']]);
  });
});
