package com.eduframepackage.eduframe.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * <h1>UC-05 Submit Support Ticket - Ticket Domain Entity</h1>
 * 
 * <p>
 * This JPA Entity models the core domain object for the UC-05 "Submit Support Ticket" module 
 * within the EduFrame academic video and learning support platform.
 * </p>
 * 
 * <h2>ECE Architectural Context:</h2>
 * <ul>
 *   <li><b>Entity Layer:</b> Encapsulates support ticket state, metadata, and persistence mapping.</li>
 *   <li><b>Control Layer:</b> Processed via {@code TicketService} for validation, duplicate prevention, and ID generation.</li>
 *   <li><b>Boundary Layer:</b> Exposed through {@code TicketController} REST API and rendered on React UI.</li>
 * </ul>
 * 
 * <h2>UC-05 Key Attributes:</h2>
 * <ul>
 *   <li>{@code ticketId}: Unique formatted ticket code (e.g., TKT-1001, TKT-1002).</li>
 *   <li>{@code subject}: Concise title describing the student/user issue.</li>
 *   <li>{@code description}: Detailed explanation of the support request.</li>
 *   <li>{@code attachmentName}: Original file name of optional attached documentation.</li>
 *   <li>{@code status}: Current ticket workflow status. Defaults to "Open" upon creation.</li>
 * </ul>
 * 
 * @author EduFrame Group B4G1-06 (SE2030)
 * @version 1.0
 */
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false, unique = true, length = 30)
    private String ticketId;

    @Column(name = "subject", nullable = false, length = 200)
    private String subject;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Column(name = "attachment_name", length = 255)
    private String attachmentName;

    @Column(name = "attachment_type", length = 100)
    private String attachmentType;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "Open";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Default no-argument constructor required by JPA specification.
     */
    public Ticket() {
    }

    /**
     * Parameterized constructor for initializing a new Support Ticket instance.
     * 
     * @@param ticketId       the formatted unique ticket identifier (e.g. TKT-1001)
     * @param subject        the ticket subject title
     * @param description    the ticket detailed message body
     * @param attachmentName the optional file attachment name
     * @param attachmentType the optional file attachment MIME type
     */
    public Ticket(String ticketId, String subject, String description, String attachmentName, String attachmentType) {
        this.ticketId = ticketId;
        this.subject = subject;
        this.description = description;
        this.attachmentName = attachmentName;
        this.attachmentType = attachmentType;
        this.status = "Open";
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null || this.status.trim().isEmpty()) {
            this.status = "Open";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", ticketId='" + ticketId + '\'' +
                ", subject='" + subject + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
