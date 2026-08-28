package com.eduframepackage.eduframe.service;

import com.eduframepackage.eduframe.dto.TicketDTO;
import com.eduframepackage.eduframe.dto.TicketReceiptDTO;
import com.eduframepackage.eduframe.model.Ticket;
import com.eduframepackage.eduframe.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <h1>UC-05 Submit Support Ticket - Service Control Layer</h1>
 * 
 * <p>
 * Business logic service component encapsulating the operational flow of UC-05.
 * Coordinates input validation, duplicate ticket checking, unique Ticket ID sequence generation,
 * default status assignment, file attachment recording, and persistence via {@link TicketRepository}.
 * </p>
 * 
 * <h2>UC-05 Flow Execution Steps:</h2>
 * <ol>
 *   <li><b>Validation:</b> Verifies that Subject (min 5 chars) and Description (min 10 chars) meet quality rules.</li>
 *   <li><b>Duplicate Prevention:</b> Checks if an open ticket with identical content already exists.</li>
 *   <li><b>Ticket ID Generation:</b> Computes sequential identifier formatted as {@code TKT-1001}, {@code TKT-1002}, etc.</li>
 *   <li><b>Status Assignment:</b> Enforces initial ticket status as {@code "Open"}.</li>
 *   <li><b>Receipt Generation:</b> Constructs an {@link TicketReceiptDTO} (eTicketReceipt) for presentation on the React UI.</li>
 * </ol>
 * 
 * @author EduFrame Group B4G1-06 (SE2030)
 * @version 1.0
 */
@Service
public class TicketService {

    private static final String TICKET_PREFIX = "TKT-";
    private static final long INITIAL_TICKET_NUMBER = 1001L;

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Processes a support ticket submission request (UC-05).
     * 
     * @param dto  the ticket input DTO containing subject and description
     * @param file optional uploaded file attachment
     * @return {@link TicketReceiptDTO} digital eTicketReceipt confirming creation
     * @throws IllegalArgumentException if validation fails or input is missing
     * @throws IllegalStateException    if a duplicate ticket is detected
     */
    @Transactional
    public TicketReceiptDTO submitSupportTicket(TicketDTO dto, MultipartFile file) {
        // 1. Validate Input Data
        validateTicketInput(dto);

        // 2. Perform Duplicate Submission Check
        checkForDuplicates(dto.getSubject(), dto.getDescription());

        // 3. Generate Unique Sequential Ticket ID (e.g. TKT-1001)
        String generatedTicketId = generateUniqueTicketId();

        // 4. Extract Optional File Attachment Metadata
        String attachmentName = null;
        String attachmentType = null;
        if (file != null && !file.isEmpty()) {
            attachmentName = file.getOriginalFilename();
            attachmentType = file.getContentType();
        }

        // 5. Instanciate Ticket JPA Entity (Default status "Open")
        Ticket ticket = new Ticket(
                generatedTicketId,
                dto.getSubject().trim(),
                dto.getDescription().trim(),
                attachmentName,
                attachmentType
        );
        ticket.setStatus("Open");

        // 6. Persist Entity to Repository
        Ticket savedTicket = ticketRepository.save(ticket);

        // 7. Build and Return eTicketReceipt DTO
        return new TicketReceiptDTO(
                savedTicket.getTicketId(),
                savedTicket.getSubject(),
                savedTicket.getDescription(),
                savedTicket.getStatus(),
                savedTicket.getAttachmentName(),
                savedTicket.getCreatedAt(),
                "Your support ticket has been submitted successfully! Please retain your Ticket ID for tracking."
        );
    }

    /**
     * Validates Subject and Description requirements for UC-05.
     * 
     * @param dto ticket DTO to validate
     */
    private void validateTicketInput(TicketDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Ticket data must not be empty.");
        }
        if (dto.getSubject() == null || dto.getSubject().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject is required and cannot be blank.");
        }
        if (dto.getSubject().trim().length() < 5) {
            throw new IllegalArgumentException("Subject must be at least 5 characters long.");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required and cannot be blank.");
        }
        if (dto.getDescription().trim().length() < 10) {
            throw new IllegalArgumentException("Description must be at least 10 characters long.");
        }
    }

    /**
     * Checks if an open ticket with matching subject and description already exists.
     * 
     * @param subject     subject title
     * @param description description text
     */
    private void checkForDuplicates(String subject, String description) {
        boolean duplicateExists = ticketRepository.existsBySubjectIgnoreCaseAndDescriptionIgnoreCaseAndStatus(
                subject.trim(),
                description.trim(),
                "Open"
        );
        if (duplicateExists) {
            throw new IllegalStateException("Duplicate ticket detected! An open support ticket with this identical subject and description has already been submitted.");
        }
    }

    /**
     * Generates a unique sequential ticket code starting at TKT-1001.
     * 
     * @return formatted unique ticket ID string (e.g., TKT-1001)
     */
    public synchronized String generateUniqueTicketId() {
        Optional<Ticket> latestTicket = ticketRepository.findTopByOrderByIdDesc();
        long nextNumber = INITIAL_TICKET_NUMBER;

        if (latestTicket.isPresent()) {
            String lastTicketId = latestTicket.get().getTicketId();
            if (lastTicketId != null && lastTicketId.startsWith(TICKET_PREFIX)) {
                try {
                    long currentNum = Long.parseLong(lastTicketId.substring(TICKET_PREFIX.length()));
                    nextNumber = Math.max(currentNum + 1, INITIAL_TICKET_NUMBER);
                } catch (NumberFormatException e) {
                    nextNumber = ticketRepository.count() + INITIAL_TICKET_NUMBER;
                }
            } else {
                nextNumber = ticketRepository.count() + INITIAL_TICKET_NUMBER;
            }
        }

        return TICKET_PREFIX + nextNumber;
    }

    /**
     * Retrieves ticket receipt details by formatted ticket code.
     * 
     * @param ticketId ticket identifier (e.g. TKT-1001)
     * @return {@link TicketReceiptDTO}
     */
    @Transactional(readOnly = true)
    public TicketReceiptDTO getTicketReceipt(String ticketId) {
        Ticket ticket = ticketRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with ID: " + ticketId));

        return new TicketReceiptDTO(
                ticket.getTicketId(),
                ticket.getSubject(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getAttachmentName(),
                ticket.getCreatedAt(),
                "Support Ticket details retrieved successfully."
        );
    }

    /**
     * Lists all support tickets stored in system.
     * 
     * @return list of all tickets
     */
    @Transactional(readOnly = true)
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
}
