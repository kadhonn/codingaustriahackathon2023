package at.nicerpricer.backend.controller

import at.nicerpricer.backend.model.Data
import at.nicerpricer.backend.model.GroceryList
import at.nicerpricer.backend.model.QueryItem
import at.nicerpricer.backend.model.ShoppingTrip
import at.nicerpricer.backend.service.DataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiController @Autowired constructor(
    private val dataService: DataService
) {

    @GetMapping("/")
    fun index(): String {
        return "Hello to NicerPricer's API!"
    }

    @PostMapping("/shop")
    @ResponseBody
    fun shop(@RequestBody groceryList: GroceryList): List<ShoppingTrip> {
        return dataService.shop(groceryList)
    }

    @GetMapping("/query")
    fun query(query: String): List<QueryItem> {
        return dataService.query(query)
    }

    @GetMapping("/history")
    fun history(item: String): List<Data> {
        return dataService.history(item)
    }
}
