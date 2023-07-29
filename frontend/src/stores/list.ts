import {defineStore} from 'pinia'
import type {ShoppingItem} from "@/api/dto";

const listKey = 'shoppingList'

export const useListStore = defineStore('list', {
    state: () => {
        const listJson = localStorage.getItem(listKey)
        const list = listJson ? JSON.parse(listJson) as ShoppingItem[] : []
        return {list}
    },
    actions: {
        setList(list: ShoppingItem[]) {
            this.list = {list: list}
            localStorage.setItem(listKey, JSON.stringify(list));
        }
    }
})
