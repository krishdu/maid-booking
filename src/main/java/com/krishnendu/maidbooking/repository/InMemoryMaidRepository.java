package com.krishnendu.maidbooking.repository;

import com.krishnendu.maidbooking.model.Maid;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryMaidRepository implements MaidRepository {

    private final Map<String, Maid> maids = new ConcurrentHashMap<>();

    @Override
    public Maid save(Maid maid) {
        maids.put(maid.id(), maid);
        return maid;
    }

    @Override
    public Optional<Maid> findById(String id) {
        return Optional.ofNullable(maids.get(id));
    }

    @Override
    public List<Maid> findAll() {
        return List.copyOf(maids.values());
    }
}
