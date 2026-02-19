import { defineConfig } from 'vite';
import solidPlugin from 'vite-plugin-solid';
import suidPlugin from '@suid/vite-plugin';
import devtools from 'solid-devtools/vite';
import { VitePWA } from 'vite-plugin-pwa'

export default defineConfig({
  plugins: [devtools(), solidPlugin(), suidPlugin(),
  VitePWA({
    devOptions: {
      enabled: true,
    },
    manifest: {
      name: 'Jenga',
      short_name: 'Jenga',
      description: 'My Awesome App description',
      theme_color: '#ffffff',
      icons: [
        {
          src: '/icons/icon-192.png',
          sizes: '192x192',
          type: 'image/png',
        },
        {
          src: '/icons/icon-512.png',
          sizes: '512x512',
          type: 'image/png',
        },
      ]
    }
  })
  ],
  server: {
    port: 7000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  build: {
    target: 'esnext',
  },
});
