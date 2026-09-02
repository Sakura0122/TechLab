import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dictionary',
    },
    {
      path: '/dictionary',
      name: 'dictionary',
      component: () => import('@/views/dictionary/index.vue'),
    },
    {
      path: '/encryption',
      name: 'encryption',
      component: () => import('@/views/encryption/index.vue'),
    },
  ],
})

export default router
