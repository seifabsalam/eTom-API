package com.telecom.etomapi.repository;

import com.telecom.etomapi.model.TroubleTicket;
import java.util.List;
import java.util.Optional;

public interface TroubleTicketRepository {
    List<TroubleTicket> findAll();
    Optional<TroubleTicket> findById(String id);
    TroubleTicket save(TroubleTicket ticket);
    boolean deleteById(String id);
}
