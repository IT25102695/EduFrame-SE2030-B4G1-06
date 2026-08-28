package com.eduframepackage.eduframe.dto;

import java.time.LocalDateTime;

/**
 * <h1>UC-05 Submit Support Ticket - eTicketReceipt DTO</h1>
 * 
 * <p>
 * Official digital confirmation receipt DTO returned to the frontend boundary upon
 * successful processing of a support ticket. Contains unique ticket ID, submission timestamp,
 * status ("Open"), and summary details.
 * </p>
 * 
 * @author EduFrame Group B4G1-06 (SE2030)
 * @version 1.0
 */
public class TicketReceiptDTO {

    private String ticketId;
    private String subject;
    private String description;
    private String status;
    private String attachmentName;
    private LocalDateTime createdAt;
    private String receiptMessage;

    public TicketReceiptDTO() {
    }

    public TicketReceiptDTO(String ticketId, String subject, String description, String status, String attachmentName, LocalDateTime createdAt, String receiptMessage) {
        this.ticketId = ticketId;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.attachmentName = attachmentName;
        this.createdAt = createdAt;
        this.receiptMessage = receiptMessage;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getReceiptMessage() {
        return receiptMessage;
    }

    public void setReceiptMessage(String receiptMessage) {
        this.receiptMessage = receiptMessage;
    }

    @Override
    public String toString() {
        return "TicketReceiptDTO{" +
                "ticketId='" + ticketId + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
