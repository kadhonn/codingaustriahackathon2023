import {defineStore} from 'pinia'

export type User = {
    name: string
}

const userKey = 'user'

export const useAuthStore = defineStore('auth', {
    state: () => {
        const userJson = localStorage.getItem(userKey)
        const user = userJson ? JSON.parse(userJson) as User : null

        return {user}
    },
    actions: {
        login(name: string) {
            this.user = {name: name} as User
            localStorage.setItem(userKey, JSON.stringify(this.user));
        }
    }
})
