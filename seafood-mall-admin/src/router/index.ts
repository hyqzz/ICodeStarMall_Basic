/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import { createRouter, createWebHistory } from 'vue-router'
import { useAdminStore } from '../stores/admin' // Import the admin store

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { requiresAuth: true } // Mark as protected route
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('../views/Products.vue'),
        meta: { requiresAuth: true } // Mark as protected route
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('../views/Orders.vue'),
        meta: { requiresAuth: true } // Mark as protected route
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/Users.vue'),
        meta: { requiresAuth: true } // Mark as protected route
      },
      {
        path: 'categories',
        name: 'Categories',
        component: () => import('../views/Category.vue'),
        meta: { requiresAuth: true } // Mark as protected route
      },
      {
        path: 'banners',
        name: 'Banners',
        component: () => import('../views/Banners.vue'),
        meta: { requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const adminStore = useAdminStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  
  if (requiresAuth && !adminStore.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router 