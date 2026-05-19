import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import ts from 'typescript';
import { renderToStaticMarkup } from 'react-dom/server';
import { Fragment, jsx, jsxs } from 'react/jsx-runtime';

const scriptDir = path.dirname(fileURLToPath(import.meta.url));
const sourcePath = path.resolve(scriptDir, '../src/components/Field.tsx');
const source = readFileSync(sourcePath, 'utf8');

const compiled = ts.transpileModule(source, {
  compilerOptions: {
    esModuleInterop: true,
    jsx: ts.JsxEmit.ReactJSX,
    module: ts.ModuleKind.CommonJS,
    target: ts.ScriptTarget.ES2022,
  },
});

const module = { exports: {} };

function requireFromField(id) {
  if (id === 'react/jsx-runtime') {
    return { Fragment, jsx, jsxs };
  }

  throw new Error(`Unexpected import in Field component test: ${id}`);
}

new Function('require', 'module', 'exports', compiled.outputText)(
  requireFromField,
  module,
  module.exports,
);

const { Field, TextInput } = module.exports;

const requiredMarkup = renderToStaticMarkup(
  jsx(Field, {
    label: 'Titulo',
    required: true,
    children: jsx(TextInput, { required: true }),
  }),
);

assert.match(requiredMarkup, /Titulo/);
assert.match(requiredMarkup, /class="field__required"/);
assert.match(requiredMarkup, />\*<\/span>/);
assert.match(requiredMarkup, /aria-hidden="true"/);

const optionalMarkup = renderToStaticMarkup(
  jsx(Field, {
    label: 'ISBN',
    children: jsx(TextInput, {}),
  }),
);

assert.doesNotMatch(optionalMarkup, /class="field__required"/);
