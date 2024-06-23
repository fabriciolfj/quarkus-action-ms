package org.acme.inventory.service;

import jakarta.transaction.Transactional;
import org.acme.inventory.model.Car;
import org.acme.inventory.repository.CardRepository;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@GraphQLApi
public class GraphQLInventoryService {

    @Inject
    CardRepository repository;

    @Query
    public List<Car> cars() {
        return repository.listAll();
    }

    @Mutation
    @Transactional
    public Car register(Car car) {
        repository.persist(car);
        return car;
    }

    @Mutation
    public boolean remove(String licensePlateNumber) {
        Optional<Car> toBeRemoved = repository
                .findByLicensePlateNumberOptional(
                        licensePlateNumber);
        if (toBeRemoved.isPresent()) {
            repository.delete(toBeRemoved.get());
            return true;
        } else {
            return false;
        }
    }

}
