import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api/listings': 'http://localhost:8082',
      '/api/users': 'http://localhost:8081',
      '/auth': {
        target: 'http://localhost:8080',
        rewrite: (path) =>
          path.replace(/^\/auth/, '/realms/urban-radius/protocol/openid-connect'),
      },
    },
  },
});
