package com.telecom.etomapi.config;

import com.telecom.etomapi.repository.InMemoryTroubleTicketRepository;
import com.telecom.etomapi.repository.TroubleTicketRepository;
import com.telecom.etomapi.service.TroubleTicketService;
import jakarta.inject.Singleton;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class TroubleTicketBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(InMemoryTroubleTicketRepository.class).to(TroubleTicketRepository.class).in(Singleton.class);
        bind(TroubleTicketService.class).to(TroubleTicketService.class).in(Singleton.class);
    }
}
