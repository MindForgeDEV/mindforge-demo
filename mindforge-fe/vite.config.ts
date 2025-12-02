import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
  },
  build: {
    rollupOptions: {
      external: (id) => {
        // Exclude test files from production build
        return id.includes('.spec.ts') || id.includes('.test.ts')
      }
    }
  },
});
