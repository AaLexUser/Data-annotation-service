import commonjs from '@rollup/plugin-commonjs';
import { nodeResolve } from '@rollup/plugin-node-resolve';
import terser from '@rollup/plugin-terser';
import typescript from '@rollup/plugin-typescript';

const plugins = [
  typescript({
    compilerOptions: { lib: ['es5', 'es6'], target: 'es5' },
    tsconfig: './tsconfig.util.json',
  }),
  nodeResolve(),
  commonjs(),
  terser(),
];

export default [
  {
    input: './scripts/bootstrap.ts',
    output: {
      file: './scripts/bootstrap.js',
      format: 'cjs',
    },
    plugins,
  },
] as any;
