import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { EmissionsReductionClaimListTemplateComponent } from './emissions-reduction-claim-list-template.component';

describe('EmissionsReductionClaimListTemplateComponent', () => {
  let page: Page;
  let component: EmissionsReductionClaimListTemplateComponent;
  let fixture: ComponentFixture<EmissionsReductionClaimListTemplateComponent>;

  class Page extends BasePage<EmissionsReductionClaimListTemplateComponent> {
    get tierRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmissionsReductionClaimListTemplateComponent, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(EmissionsReductionClaimListTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);

    component.isEditable = true;
    component.data = [
      {
        purchase: {
          batchNumber: '1',
          fuelName: 'A',
          safMass: '33',
          sustainabilityCriteriaEvidenceType: 'SCREENSHOT_FROM_RTFO_REGISTRY',
          evidenceFiles: ['bb94a5ec-3a94-4472-9dc5-f1c51fef3d22'],
          otherSustainabilityCriteriaEvidenceDescription: null,
        },
        files: [
          {
            downloadUrl: 'link',
            fileName: 'First file',
          },
        ],
      },
      {
        purchase: {
          batchNumber: '2',
          fuelName: 'B',
          safMass: '2',
          sustainabilityCriteriaEvidenceType: 'SCREENSHOT_FROM_RTFO_REGISTRY',
          evidenceFiles: ['bb94a5ec-3a94-4472-9dc5-f1c51fef3d22'],
          otherSustainabilityCriteriaEvidenceDescription: null,
        },
        files: [
          {
            downloadUrl: 'link',
            fileName: 'Second file',
          },
        ],
      },
    ];

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.tierRows.map((row) => Array.from(row.cells).map((col) => col.textContent.trim()))).toEqual([
      ['A', '1', '33 t', 'First file', 'ChangeRemove'],
      ['B', '2', '2 t', 'Second file', 'ChangeRemove'],
    ]);
  });
});
