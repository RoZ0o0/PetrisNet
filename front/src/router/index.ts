import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import UserView from '../views/users/UserView.vue';

import LoginView from '../views/LoginView.vue';
import RegisterView from '../views/RegisterView.vue';

import CreatorView from '../views/creator/CreatorView.vue';
import UserProfileView from '../views/users/UserProfileView.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/users',
    name: 'users',
    component: UserView
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/creator',
    name: 'creator',
    component: CreatorView
  },
  {
    path: '/profile',
    name: 'profile',
    component: UserProfileView
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

export default router;
