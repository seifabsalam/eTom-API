package com.telecom.etomapi.repository;

import com.telecom.etomapi.model.TroubleTicket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTroubleTicketRepository implements TroubleTicketRepository {

    private final Map<String, TroubleTicket> database = new ConcurrentHashMap<>();

    @Override
    public List<TroubleTicket> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public Optional<TroubleTicket> findById(String id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public TroubleTicket save(TroubleTicket ticket) {
        database.put(ticket.getId(), ticket);
        return ticket;
    }

    @Override
    public boolean deleteById(String id) {
        return database.remove(id) != null;
    }
}
