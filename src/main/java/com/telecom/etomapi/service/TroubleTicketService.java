package com.telecom.etomapi.service;

import com.telecom.etomapi.exception.BadRequestException;
import com.telecom.etomapi.exception.ResourceNotFoundException;
import com.telecom.etomapi.model.*;
import com.telecom.etomapi.repository.TroubleTicketRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TroubleTicketService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final TroubleTicketRepository repository;

    @jakarta.inject.Inject
    public TroubleTicketService(TroubleTicketRepository repository) {
        this.repository = repository;
    }

    public List<TroubleTicket> findAll(Map<String, String> filters) {
        List<TroubleTicket> list = repository.findAll();

        if (filters.containsKey("status")) {
            String statusVal = filters.get("status");
            list = list.stream().filter(t -> statusVal.equalsIgnoreCase(t.getStatus())).collect(Collectors.toList());
        }
        if (filters.containsKey("severity")) {
            String sevVal = filters.get("severity");
            list = list.stream().filter(t -> sevVal.equalsIgnoreCase(t.getSeverity())).collect(Collectors.toList());
        }
        if (filters.containsKey("ticketType")) {
            String typeVal = filters.get("ticketType");
            list = list.stream().filter(t -> typeVal.equalsIgnoreCase(t.getTicketType())).collect(Collectors.toList());
        }

        return list;
    }

    public TroubleTicket findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TroubleTicket with id '" + id + "' does not exist"));
    }

    public TroubleTicket create(TroubleTicket ticket, String host) {
        List<String> missingFields = new ArrayList<>();
        if (ticket.getDescription() == null || ticket.getDescription().trim().isEmpty()) missingFields.add("description");
        if (ticket.getSeverity() == null || ticket.getSeverity().trim().isEmpty()) missingFields.add("severity");
        if (ticket.getTicketType() == null || ticket.getTicketType().trim().isEmpty()) missingFields.add("ticketType");

        if (!missingFields.isEmpty()) {
            throw new BadRequestException("Mandatory fields [" + String.join(", ", missingFields) + "] are missing or invalid");
        }

        if (host == null || host.isEmpty()) {
            host = "localhost:8080";
        }
        ticket.setHref("http://" + host + "/troubleTicket/v2/troubleTicket/" + ticket.getId());

        if (ticket.getStatus() == null) {
            ticket.setStatus("Acknowledged");
        }
        if (ticket.getStatusChange().isEmpty()) {
            ticket.getStatusChange().add(new StatusChange(ticket.getStatus(), "Ticket created", "TroubleTicket"));
        }

        return repository.save(ticket);
    }

    public TroubleTicket update(String id, Map<String, Object> updates) {
        TroubleTicket ticket = findById(id);
        String originalStatus = ticket.getStatus();
        boolean statusChanged = false;
        String newStatus = null;
        String statusChangeReasonText = null;

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Silently ignore non-patchable fields
            if (key.equals("id") || key.equals("href") || key.equals("creationDate") || key.equals("lastUpdate") || key.equals("statusChange")) {
                continue;
            }

            switch (key) {
                case "name":
                    ticket.setName((String) value);
                    break;
                case "externalId":
                    ticket.setExternalId((String) value);
                    break;
                case "ticketType":
                    ticket.setTicketType((String) value);
                    break;
                case "description":
                    ticket.setDescription((String) value);
                    break;
                case "severity":
                    ticket.setSeverity((String) value);
                    break;
                case "priority":
                    ticket.setPriority((String) value);
                    break;
                case "status":
                    newStatus = (String) value;
                    if (newStatus != null && !newStatus.equalsIgnoreCase(originalStatus)) {
                        statusChanged = true;
                    }
                    break;
                case "statusChangeReason":
                    statusChangeReasonText = (String) value;
                    ticket.setStatusChangeReason(statusChangeReasonText);
                    break;
                case "requestedResolutionDate":
                    ticket.setRequestedResolutionDate((String) value);
                    break;
                case "expectedResolutionDate":
                    ticket.setExpectedResolutionDate((String) value);
                    break;
                case "resolutionDate":
                    ticket.setResolutionDate((String) value);
                    break;
                case "channel":
                    if (value == null) {
                        ticket.setChannel(null);
                    } else {
                        ticket.setChannel(OBJECT_MAPPER.convertValue(value, Channel.class));
                    }
                    break;
                case "note":
                    if (value != null) {
                        List<Note> newNotes = OBJECT_MAPPER.convertValue(value, new TypeReference<List<Note>>() {});
                        ticket.getNote().addAll(newNotes);
                    }
                    break;
                case "attachment":
                    if (value != null) {
                        List<Attachment> newAttachments = OBJECT_MAPPER.convertValue(value, new TypeReference<List<Attachment>>() {});
                        ticket.getAttachment().addAll(newAttachments);
                    }
                    break;
                case "relatedParty":
                    if (value != null) {
                        List<RelatedParty> newParties = OBJECT_MAPPER.convertValue(value, new TypeReference<List<RelatedParty>>() {});
                        ticket.getRelatedParty().addAll(newParties);
                    }
                    break;
                case "relatedEntity":
                    if (value != null) {
                        List<RelatedEntity> newEntities = OBJECT_MAPPER.convertValue(value, new TypeReference<List<RelatedEntity>>() {});
                        ticket.getRelatedEntity().addAll(newEntities);
                    }
                    break;
                case "ticketRelationship":
                    if (value != null) {
                        List<TicketRelationship> newRelations = OBJECT_MAPPER.convertValue(value, new TypeReference<List<TicketRelationship>>() {});
                        ticket.getTicketRelationship().addAll(newRelations);
                    }
                    break;
            }
        }

        if (statusChanged && newStatus != null) {
            ticket.setStatus(newStatus);
            String reason = statusChangeReasonText != null ? statusChangeReasonText : "Status updated via PATCH";
            ticket.getStatusChange().add(new StatusChange(newStatus, reason, "TroubleTicket"));
        }

        ticket.setLastUpdate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
        
        return repository.save(ticket);
    }

    public void delete(String id) {
        if (!repository.deleteById(id)) {
            throw new ResourceNotFoundException("TroubleTicket with id '" + id + "' does not exist");
        }
    }
}
