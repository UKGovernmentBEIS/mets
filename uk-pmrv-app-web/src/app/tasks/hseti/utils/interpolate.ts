/**
 * Interpolates a template string with the provided parameters.
 * The template should contain placeholders in the format {{ key }}.
 *
 * @param template - The template string containing placeholders.
 * @param params - An object containing key-value pairs for interpolation.
 * @returns The interpolated string with placeholders replaced by corresponding values from params.
 */
export const interpolate = (template: string, params: Record<string, string | undefined>): string => {
  return template.replace(/{{\s*(\w+)\s*}}/g, (_, key) => params[key] ?? '');
};
