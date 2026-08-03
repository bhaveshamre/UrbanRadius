import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': 'http://localhost:8085',
      '/auth': {
        target: 'http://localhost:8080',
        rewrite: (path) =>
          path.replace(/^\/auth/, '/realms/urban-radius/protocol/openid-connect'),
      },
    },
  },
});
