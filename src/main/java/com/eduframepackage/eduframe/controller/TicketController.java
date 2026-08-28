package com.eduframepackage.eduframe.controller;

import com.eduframepackage.eduframe.dto.TicketDTO;
import com.eduframepackage.eduframe.dto.TicketReceiptDTO;
import com.eduframepackage.eduframe.model.Ticket;
import com.eduframepackage.eduframe.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>UC-05 Submit Support Ticket - REST Controller</h1>
 * 
 * <p>
 * REST API Boundary Controller handling client HTTP requests for UC-05 "Submit Support Ticket".
 * Exposes endpoints for ticket creation with file attachment, ticket receipt lookup, 
 * and administrative ticket listing.
 * </p>
 * 
 * <h2>API Endpoint Summary:</h2>
 * <ul>
 *   <li>{@code POST /api/tickets/submit}: Processes support ticket creation (accepts multipart/form-data or JSON).</li>
 *   <li>{@code GET /api/tickets/{ticketId}}: Fetches eTicketReceipt for a specific ticket ID (e.g. TKT-1001).</li>
 *   <li>{@code GET /api/tickets}: Returns all submitted support tickets.</li>
 * </ul>
 * 
 * @author EduFrame Group B4G1-06 (SE2030)
 * @version 1.0
 */
@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Handles support ticket submission (UC-05).
     * Accepts multipart/form-data containing subject, description, and optional file attachment.
     * 
     * @param subject      the subject title of the support request
     * @param description  the detailed description body
     * @param studentId    optional student ID
     * @param studentEmail optional student contact email
     * @param file         optional attached file (max 5MB)
     * @return {@link ResponseEntity} containing {@link TicketReceiptDTO} (eTicketReceipt)
     */
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitTicketMultipart(
            @RequestParam("subject") String subject,
            @RequestParam("description") String description,
            @RequestParam(value = "studentId", required = false) String studentId,
            @RequestParam(value = "studentEmail", required = false) String studentEmail,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            TicketDTO dto = new TicketDTO(subject, description, studentId, studentEmail);
            TicketReceiptDTO receipt = ticketService.submitSupportTicket(dto, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Validation Error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Duplicate Ticket");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Server Error");
            error.put("message", "An unexpected error occurred while processing your request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Alternative JSON submission endpoint for support ticket creation (without file upload).
     * 
     * @param dto the ticket request DTO payload
     * @return {@link ResponseEntity} containing {@link TicketReceiptDTO}
     */
    @PostMapping(value = "/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> submitTicketJson(@RequestBody TicketDTO dto) {
        try {
            TicketReceiptDTO receipt = ticketService.submitSupportTicket(dto, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Validation Error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Duplicate Ticket");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Server Error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Retrieves eTicketReceipt details by ticket identifier (e.g. TKT-1001).
     * 
     * @param ticketId formatted ticket ID
     * @return {@link ResponseEntity} with ticket receipt data
     */
    @GetMapping("/{ticketId}")
    public ResponseEntity<?> getTicketReceipt(@PathVariable("ticketId") String ticketId) {
        try {
            TicketReceiptDTO receipt = ticketService.getTicketReceipt(ticketId);
            return ResponseEntity.ok(receipt);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Not Found");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Retrieves all submitted tickets in the system.
     * 
     * @return list of {@link Ticket} entities
     */
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }
}
