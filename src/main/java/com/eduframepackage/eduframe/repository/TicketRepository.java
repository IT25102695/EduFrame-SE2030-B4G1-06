package com.eduframepackage.eduframe.repository;

import com.eduframepackage.eduframe.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * <h1>UC-05 Submit Support Ticket - Ticket JPA Repository</h1>
 * 
 * <p>
 * Data access abstraction interface for managing {@link Ticket} persistent entities.
 * Provides custom query methods for retrieving tickets by generated ticket code,
 * verifying duplicate ticket submissions, and supporting sequential ticket ID generation.
 * </p>
 * 
 * @author EduFrame Group B4G1-06 (SE2030)
 * @version 1.0
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Retrieves a Ticket by its unique formatted ticket code (e.g., TKT-1001).
     * 
     * @param ticketId the unique formatted ticket code
     * @return an {@link Optional} containing the found Ticket, or empty if not found
     */
    Optional<Ticket> findByTicketId(String ticketId);

    /**
     * Checks if a ticket already exists with the identical subject, description, and status.
     * Used by {@code TicketService} for UC-05 duplicate prevention.
     * 
     * @param subject     the subject title to check
     * @param description the description body to check
     * @param status      the current status (e.g. "Open")
     * @return {@code true} if a matching duplicate ticket exists, {@code false} otherwise
     */
    boolean existsBySubjectIgnoreCaseAndDescriptionIgnoreCaseAndStatus(String subject, String description, String status);

    /**
     * Retrieves the most recently inserted Ticket entity ordered by primary key descending.
     * Used for sequential ticket ID calculation (e.g., TKT-1001 -> TKT-1002).
     * 
     * @return an {@link Optional} containing the latest Ticket entry
     */
    Optional<Ticket> findTopByOrderByIdDesc();
}
