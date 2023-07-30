export type ShoppingItem = {
    id: string;
    name: string;
    quantity: number;
}

export type Coordinates = {
    latitude: number;
    longitude: number;
}

export type PlaceGeometry = {
    location: Coordinates
}

export type Place = {
    place_id: string,
    geometry: PlaceGeometry,
    icon: string,
    name: string,
    vicinity: string,
}

export type ShoppingStop = {
    stop: Place,
    items: ShoppingItem
}

export type ShoppingTrip = {
    totalPrice: number,
    stops: ShoppingStop[],
    unreachable?: ShoppingItem
}
