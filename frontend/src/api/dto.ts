export type ShoppingItem = {
    id: string;
    name: string;
    quantity: number;
}

export type PlaceGeometry = {
    location: Location
}

export type Place = {
    geometry: PlaceGeometry,
    icon: string,
    name: string
}

export type ShoppingStop = {
    stop: Place,
    items: ShoppingItem
}

export type ShoppingTrip = {
    stops: ShoppingStop[],
    unreachable: ShoppingItem
}
