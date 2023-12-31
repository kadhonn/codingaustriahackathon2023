import {createRouter, createWebHistory} from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView
        },
        {
            path: '/name',
            name: 'name',
            component: () => import('../views/NameView.vue')
        },
        {
            path: '/list',
            name: 'list',
            component: () => import('../views/ShoppingLIstView.vue')
        },
        {
            path: '/trips',
            name: 'trip',
            component: () => import('../views/TripsView.vue')
        },
        {
            path: '/trip',
            name: 'selectedTrip',
            component: () => import('../views/TripView.vue')
        }

    ]
})

// TOOD authenticated routes

export default router
