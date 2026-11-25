package com.krishnendu.maidbooking.service;

import com.krishnendu.maidbooking.model.Maid;
import com.krishnendu.maidbooking.model.ServiceType;
import com.krishnendu.maidbooking.repository.MaidRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

@Service
public class MaidService {

    private final MaidRepository maidRepository;

    public MaidService(MaidRepository maidRepository) {
        this.maidRepository = maidRepository;
    }

    public Maid add(Maid maid) {
        return maidRepository.save(maid);
    }

    public Maid get(String id) {
        return maidRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Maid not found: " + id));
    }

    public List<Maid> search(String locality, ServiceType service, Double minRating, Double maxPrice) {
        Predicate<Maid> filter = maid -> true;
        if (locality != null) {
            filter = filter.and(m -> m.locality().equalsIgnoreCase(locality));
        }
        if (service != null) {
            filter = filter.and(m -> m.offers(service));
        }
        if (minRating != null) {
            filter = filter.and(m -> m.rating() >= minRating);
        }
        if (maxPrice != null) {
            filter = filter.and(m -> m.pricePerHour() <= maxPrice);
        }
        return maidRepository.findAll().stream().filter(filter).toList();
    }
}
