import {defineStore} from 'pinia'
import type {Coordinates, ShoppingTrip} from "@/api/dto";
import type {ShoppingItem} from "@/api/dto";

export type User = {
    name: string,
    location: Coordinates,
}

const userKey = 'user'
const listKey = 'shoppingList'
const tripKey = 'shoppingTrip'
const selectedTripKey = 'selectedShoppingTrip'


export const useAuthStore = defineStore('auth', {
    state: () => {
        const userJson = localStorage.getItem(userKey)
        const user = userJson ? JSON.parse(userJson) as User : null

        const listJson = localStorage.getItem(listKey)
        const list = listJson ? JSON.parse(listJson) as ShoppingItem[] : []

        const tripJson = localStorage.getItem(tripKey)
        const trips = tripJson ? JSON.parse(tripJson) as ShoppingTrip[] : []

        const selectedTripJson = localStorage.getItem(selectedTripKey)
        const selectedTrip = tripJson ? JSON.parse(selectedTripJson ?? '{}') as ShoppingTrip : {}

        return {user, list, trips, selectedTrip}
    },
    actions: {
        login(name: string) {
            this.user = {name: name} as User
            localStorage.setItem(userKey, JSON.stringify(this.user));
        },
        setLocation(location: Coordinates) {
            if (!this.user) {
                return
            }
            this.user.location = location
            localStorage.setItem(userKey, JSON.stringify(this.user));
        },
        setList(list: ShoppingItem[]) {
            this.list = list
            localStorage.setItem(listKey, JSON.stringify(list));
        },
        setTrips(trips: ShoppingTrip[]) {
            this.trips = trips
            localStorage.setItem(tripKey, JSON.stringify(trips));
        },
        setSelectedTrip(trip: ShoppingTrip) {
            this.selectedTrip = trip
            localStorage.setItem(selectedTripKey, JSON.stringify(tripKey))
        }
    }
})
