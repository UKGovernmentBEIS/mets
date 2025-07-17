import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { MmpInstallationDescriptionSummaryTemplateComponent } from './mmp-installation-description-summary-template.component';

describe('MmpInstallationDescriptionSummaryTemplateComponent', () => {
  let component: MmpInstallationDescriptionSummaryTemplateComponent;
  let fixture: ComponentFixture<MmpInstallationDescriptionSummaryTemplateComponent>;
  let page: Page;

  class Page extends BasePage<MmpInstallationDescriptionSummaryTemplateComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDListElement>('dl').map((data) =>
        Array.from(data.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MmpInstallationDescriptionSummaryTemplateComponent],
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
    const store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c114'],
            digitizedPlan: {
              installationDescription: {
                description: 'description',
                flowDiagrams: ['e227ea8a-778b-4208-9545-e108ea66c113'],
              },
            },
          },
        },
        {
          monitoringMethodologyPlans: [true],
          mmpInstallationDescription: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(MmpInstallationDescriptionSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary', () => {
    expect(page.summaryListValues).toEqual([
      ['description', 'Change'],
      ['', 'Change'],
    ]);
  });
});
