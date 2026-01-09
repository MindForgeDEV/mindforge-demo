import { createApp } from "vue";
import { createRouter, createWebHistory } from "vue-router";
import "./style.css";
import App from "./App.vue";
import AuthFlow from "./components/AuthFlow.vue";
import Dashboard from "./components/Dashboard.vue";
import AdminDashboard from "./components/AdminDashboard.vue";
import Scene3D from "./components/Scene3D.vue";

const routes = [
  { path: "/", redirect: "/auth" },
  { path: "/auth", component: AuthFlow },
  { path: "/dashboard", component: Dashboard },
  { path: "/admin", component: AdminDashboard },
  { path: "/scene", component: Scene3D },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

const app = createApp(App);
app.use(router);
app.mount("#app");
