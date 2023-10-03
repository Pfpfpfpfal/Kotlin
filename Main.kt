import kotlin.random.Random

data class City(val name:String,val isMajor:Boolean)

data class Transport(val name: String, val costPerWeight: Double, val speed: Double, val accidentProb: Double)

data class Road(val name: String, val isHighway: Boolean, val accidentProbReduction: Double)

data class Weather(val name: String, val isGood: Boolean)

data class Cargo(val weight: Double)

data class Order(val cargo: Cargo, val origin: City, val destination: City, val desiredSpeed: Double, val desiredPrice: Double)

class TransAgency {
    private val cities = mutableListOf<City>()
    private val transports = mutableListOf<Transport>()
    private val roads = mutableListOf<Road>()
    private val weatherConditions = mutableListOf<Weather>()
    private val orders = mutableListOf<Order>()
    private val income = mutableMapOf<String, Double>()
    private val deliveryTimes = mutableMapOf<String, MutableList<Double>>()
    private var weatherLosses = 0.0
    private val accidentLosses = mutableMapOf<String, MutableMap<String, Double>>()
    private var highwayIncome = 0.0
    private var regularRoadIncome = 0.0

    fun addCity(name: String, isMajor: Boolean) {
        cities.add(City(name, isMajor))
    }

    fun addTransport(name: String, costPerWeight: Double, speed: Double, accidentProb: Double) {
        transports.add(Transport(name, costPerWeight, speed, accidentProb))
    }

    fun addRoad(name: String, isHighway: Boolean, accidentProbReduction: Double) {
        roads.add(Road(name, isHighway, accidentProbReduction))
    }

    fun addWeatherCondition(name: String, isGood: Boolean) {
        weatherConditions.add(Weather(name, isGood))
    }

    fun addOrder(cargo: Cargo, origin: String, destination: String, desiredSpeed: Double, desiredPrice: Double) {
        val originCity = cities.find { it.name == origin }
        val destinationCity = cities.find { it.name == destination }
        if (originCity != null && destinationCity != null) {
            orders.add(Order(cargo, originCity, destinationCity, desiredSpeed, desiredPrice))
        }
    }

    fun simulateDelivery() {
        for (order in orders) {
            val transport = chooseTransport(order)
            val road = chooseRoad(order.origin, order.destination)
            val weather = getWeather(order.origin)

            if (transport.name == "air" && (!isMajorCity(order.origin) || !isMajorCity(order.destination))) {
                println("Cannot use air transport for delivery from ${order.origin.name} to ${order.destination.name}")
                continue
            }

            if (!isWeatherGood(weather)) {
                println("Weather is bad in ${order.origin.name}, delivery delayed")
                continue
            }

            val deliveryTime = calculateDeliveryTime(order.cargo.weight, road, transport)
            val deliveryCost = calculateDeliveryCost(order.cargo.weight, road, transport)

            if (deliveryTime > order.desiredSpeed) {
                println("Delivery time too long for order from ${order.origin.name} to ${order.destination.name}")
                continue
            }

            if (deliveryCost > order.desiredPrice) {
                println("Delivery cost too high for order from ${order.origin.name} to ${order.destination.name}")
                continue
            }

            updateIncome(order.destination.name, deliveryCost)
            updateDeliveryTime(order.destination.name, deliveryTime)
            updateAccidentLosses(transport, road)

            println("Order from ${order.origin.name} to ${order.destination.name} delivered successfully")
        }

        printStats()
    }

    private fun chooseTransport(order: Order): Transport {
        val availableTransports = mutableListOf<Transport>()

        for (transport in transports) {
            if (transport.name == "air") {

                if (isMajorCity(order.origin) && isMajorCity(order.destination)) {
                    availableTransports.add(transport)
                }
            } else if (transport.name == "rail") {
                if (isMajorCity(order.origin) || isMajorCity(order.destination)) {
                    availableTransports.add(transport)
                }
            } else {
                availableTransports.add(transport)
            }
        }

        return availableTransports.random()
    }

    private fun chooseRoad(origin: City, destination: City): Road {
        for (road in roads) {
            if (road.isHighway && (isMajorCity(origin) || isMajorCity(destination))) {
                return road
            }
        }

        return roads.random()
    }

    private fun getWeather(city: City): Weather {
        return weatherConditions.random()
    }
}