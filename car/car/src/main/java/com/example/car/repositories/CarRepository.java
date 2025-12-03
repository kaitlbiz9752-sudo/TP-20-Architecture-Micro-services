package com.example.car.repositories;

import com.example.car.entites.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    // Méthodes CRUD de base fournies par JpaRepository

    // Vous pouvez ajouter des méthodes personnalisées ici, par exemple:
    // List<Car> findByClientId(Long clientId);
}
