package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.Price;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;
    private final MapsClient mapClient;
    private final PriceClient pricingClient;

    public CarService(CarRepository repository, MapsClient mapClient, PriceClient pricingClient) {
        /**
         * TODO(COMPLETED): Add the Maps and Pricing Web Clients you create
         *   in `VehiclesApiApplication` as arguments and set them here.
         */
        this.repository = repository;
        this.mapClient = mapClient;
        this.pricingClient = pricingClient;

    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        /**
         * TODO(COMPLETED): Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         *   Remove the below code as part of your implementation.
         */
        Optional<Car> optionalCar = this.repository.findById(id);

        if (!optionalCar.isPresent()) {
            throw new CarNotFoundException("Car with " + id + " NOT FOUND!!!");
        }

        /**
         * TODO(COMPLETED): Use the Pricing Web client you create in `VehiclesApiApplication`
         *   to get the price based on the `id` input'
         * TODO(COMPLETED):: Set the price of the car
         * Note: The car class file uses @transient, meaning you will need to call
         *   the pricing service each time to get the price.
         */

        Car car = optionalCar.get();
        String carPrice = this.pricingClient.getPrice(id);
        car.setPrice(carPrice);

        /**
         * TODO(COMPLETED):: Use the Maps Web client you create in `VehiclesApiApplication`
         *   to get the address for the vehicle. You should access the location
         *   from the car object and feed it to the Maps service.
         * TODO(COMPLETED):: Set the location of the vehicle, including the address information
         * Note: The Location class file also uses @transient for the address,
         * meaning the Maps service needs to be called each time for the address.
         */
        Location address = this.mapClient.getAddress(car.getLocation());
        car.setLocation(address);

        return car;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        try {
            Car carToBeUpdated = findById(car.getId());
            carToBeUpdated.setDetails(car.getDetails());
            carToBeUpdated.setLocation(car.getLocation());
            carToBeUpdated.setCondition(car.getCondition());
            return repository.save(carToBeUpdated);
        } catch (Exception e) {
            return repository.save(car);
        }
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        /**
         * TODO(COMPLETED):: Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         */
        Optional<Car> optionalCar = this.repository.findById(id);
        if (!optionalCar.isPresent()) {
            throw new CarNotFoundException("Car with id of " + id + " NOT FOUND!!!");
        }


        /**
         * TODO(COMPLETED):: Delete the car from the repository.
         */

        this.repository.delete(optionalCar.get());
    }
}
