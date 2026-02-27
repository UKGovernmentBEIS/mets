const { pathsToModuleNameMapper } = require('ts-jest');
const { paths } = require('./tsconfig.json').compilerOptions;
const { createCjsPreset } = require('jest-preset-angular/presets');

const presetConfig = createCjsPreset({
  tsconfig: '<rootDir>/tsconfig.spec.json',
});

/** @type {import('ts-jest/dist/types').JestConfigWithTsJest} */
module.exports = {
  ...presetConfig,
  cacheDirectory: 'tmp/jest/cache',
  moduleNameMapper: {
    ...(presetConfig.moduleNameMapper ?? {}),
    ...pathsToModuleNameMapper(paths, { prefix: '<rootDir>/' }),
    'html-diff(.*)': '<rootDir>/html-diff$1.js',
    '^lodash-es$': 'lodash',
  },
  modulePathIgnorePatterns: ['<rootDir>/dist'],
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  transformIgnorePatterns: ['node_modules/(?!.*\\.mjs$)?!(marked)'],
};
