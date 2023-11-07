import { Injectable } from '@angular/core';
import { AbstractControlOptions, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

@Injectable()
export class FormBuilderService extends UntypedFormBuilder {
  group(controlsConfig: { [p: string]: any }, options?: AbstractControlOptions | null): UntypedFormGroup {
    return super.group(controlsConfig, { updateOn: 'submit', ...options });
  }
}
