import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';

import { AviationAerSaf } from 'pmrv-api';

import { EmissionsReductionClaimSummaryTemplateComponent } from './emissions-reduction-claim-summary-template.component';

describe('EmissionsReductionClaimSummaryTemplateComponent', () => {
  let component: EmissionsReductionClaimSummaryTemplateComponent;
  let fixture: ComponentFixture<EmissionsReductionClaimSummaryTemplateComponent>;
  let page: Page;

  const activatedRouteStub = new ActivatedRouteStub();

  const data = {
    exist: true,
    safDetails: {
      purchases: [
        {
          batchNumber: '1',
          fuelName: 'A',
          safMass: '33',
          sustainabilityCriteriaEvidenceType: 'SCREENSHOT_FROM_RTFO_REGISTRY',
          evidenceFiles: [],
          otherSustainabilityCriteriaEvidenceDescription: null,
        },
        {
          batchNumber: '2',
          fuelName: 'B',
          safMass: '2',
          sustainabilityCriteriaEvidenceType: 'SCREENSHOT_FROM_RTFO_REGISTRY',
          evidenceFiles: [],
          otherSustainabilityCriteriaEvidenceDescription: null,
        },
      ],
      noDoubleCountingDeclarationFile: {
        file: { name: 'New file' } as File,
        uuid: 'bb94a5ec-3a94-4472-9dc5-f1c51fef3d22',
      },
      totalSafMass: '35',
      emissionsFactor: '3.15',
      totalEmissionsReductionClaim: '110.25',
    },
  };

  class Page extends BasePage<EmissionsReductionClaimSummaryTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmissionsReductionClaimSummaryTemplateComponent, RouterTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteStub }],
    }).compileComponents();

    fixture = TestBed.createComponent(EmissionsReductionClaimSummaryTemplateComponent);
    component = fixture.componentInstance;

    component.data = {
      ...data,
      safDetails: {
        ...data.safDetails,
        noDoubleCountingDeclarationFile: data.safDetails?.noDoubleCountingDeclarationFile.uuid,
      },
    } as AviationAerSaf;

    component.declarationFile = {
      downloadUrl: 'link',
      fileName: data.safDetails?.noDoubleCountingDeclarationFile.file.name,
    };

    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(5);
    expect(page.summaryValues).toEqual([
      ['Will you be making an emissions reduction claim as a result of the purchase and delivery of SAF?', 'Yes'],
      ['Declaration of no double counting', 'New file'],
      ['Total mass of sustainable aviation fuel claimed', '35 tonnes'],
      ['Emissions factor applied', '3.15 tCO2 per tonne of fuel'],
      ['Total emissions reduction claim for the scheme Year', '110.25 tCO2'],
    ]);
  });
});
