import { FormArray, FormControl, FormGroup } from '@angular/forms';

import { render } from '@testing-library/angular';

import { SpecialProduct, SubInstallationHierarchicalOrder } from 'pmrv-api';

import { ProductBenchmarkFormComponent } from './product-benchmark-form.component';

describe('ProductBenchmarkFormComponent', () => {
  const renderComponent = async () => {
    const { fixture, detectChanges } = await render(
      `
      <app-product-benchmark-form
        [form]="form"
      ></app-product-benchmark-form>
      `,
      {
        imports: [ProductBenchmarkFormComponent],
        componentProperties: {
          form: new FormGroup({
            dataSources: new FormArray([
              new FormGroup({
                detail: new FormControl(),
              }),
            ]),
            methodologyAppliedDescription: new FormControl<SpecialProduct['methodologyAppliedDescription']>(null),
            hierarchicalOrder: new FormGroup({
              followed: new FormControl<SubInstallationHierarchicalOrder['followed']>(null),
              notFollowingHierarchicalOrderReason: new FormControl<
                SubInstallationHierarchicalOrder['notFollowingHierarchicalOrderReason']
              >(null),
              notFollowingHierarchicalOrderDescription: new FormControl<
                SubInstallationHierarchicalOrder['notFollowingHierarchicalOrderDescription']
              >(null),
            }),
            supportingFiles: new FormControl<SpecialProduct['supportingFiles'] | null>(null),
          }),
        },
      },
    );

    return { component: fixture.componentInstance, detectChanges };
  };

  it('should create', async () => {
    const { component } = await renderComponent();
    expect(component).toBeTruthy();
  });
});
