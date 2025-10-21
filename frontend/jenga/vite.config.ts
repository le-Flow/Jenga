import { defineConfig } from 'vite';
import solidPlugin from 'vite-plugin-solid';
import suidPlugin from '@suid/vite-plugin';
import devtools from 'solid-devtools/vite';

export default defineConfig({
  plugins: [devtools(), solidPlugin(), suidPlugin()],
  server: {
    port: 3000,
  },
  build: {
    target: 'esnext',
  },
});
