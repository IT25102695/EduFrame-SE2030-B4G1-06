/**
 * @fileoverview SupportTicket Component (UC-05 - Submit Support Ticket)
 * @description React UI component implementing the frontend presentation boundary for UC-05.
 * Features comprehensive client-side form validation (Subject, Description, File Attachment),
 * dynamic submission loading indicators, duplicate ticket conflict detection, and digital
 * eTicketReceipt confirmation view upon successful HTTP 201 response.
 * 
 * @module SupportTicket
 * @author EduFrame Group B4G1-06 (SE2030)
 * @version 1.0
 */

(function () {
    const { useState, useEffect } = React;

    /**
     * SupportTicket React Component
     * 
     * @param {Object} props Component properties
     * @param {boolean} [props.isModal=false] Whether component is rendered inside a modal overlay
     * @param {Function} [props.onClose] Callback triggered when closing modal
     * @returns {React.ReactElement} React component UI node
     */
    function SupportTicket({ isModal = false, onClose }) {
        // Form field state
        const [subject, setSubject] = useState('');
        const [description, setDescription] = useState('');
        const [studentId, setStudentId] = useState('');
        const [studentEmail, setStudentEmail] = useState('');
        const [file, setFile] = useState(null);

        // Validation & state management
        const [errors, setErrors] = useState({});
        const [isSubmitting, setIsSubmitting] = useState(false);
        const [serverError, setServerError] = useState('');
        const [receipt, setReceipt] = useState(null);

        /**
         * Validates form input fields according to UC-05 rules.
         * 
         * @returns {boolean} True if all inputs pass validation, false otherwise.
         */
        const validateForm = () => {
            const newErrors = {};

            if (!subject.trim()) {
                newErrors.subject = 'Subject is required.';
            } else if (subject.trim().length < 5) {
                newErrors.subject = 'Subject must be at least 5 characters long.';
            }

            if (!description.trim()) {
                newErrors.description = 'Description is required.';
            } else if (description.trim().length < 10) {
                newErrors.description = 'Description must be at least 10 characters long.';
            }

            if (file) {
                const maxSizeBytes = 5 * 1024 * 1024; // 5 MB limit
                const allowedExtensions = ['pdf', 'png', 'jpg', 'jpeg', 'doc', 'docx', 'zip', 'txt'];
                const fileExt = file.name.split('.').pop().toLowerCase();

                if (file.size > maxSizeBytes) {
                    newErrors.file = 'File size exceeds 5MB limit.';
                } else if (!allowedExtensions.includes(fileExt)) {
                    newErrors.file = `Invalid file format (.${fileExt}). Allowed formats: ${allowedExtensions.join(', ')}.`;
                }
            }

            setErrors(newErrors);
            return Object.keys(newErrors).length === 0;
        };

        /**
         * Handles form submission to the Spring Boot REST API (/api/tickets/submit).
         * 
         * @param {React.FormEvent} e Submit event
         */
        const handleSubmit = async (e) => {
            e.preventDefault();
            setServerError('');

            if (!validateForm()) {
                return;
            }

            setIsSubmitting(true);

            try {
                const formData = new FormData();
                formData.append('subject', subject.trim());
                formData.append('description', description.trim());
                if (studentId) formData.append('studentId', studentId.trim());
                if (studentEmail) formData.append('studentEmail', studentEmail.trim());
                if (file) formData.append('file', file);

                const response = await fetch('/api/tickets/submit', {
                    method: 'POST',
                    body: formData
                });

                const data = await response.json();

                if (response.ok) {
                    // Submission successful - Render eTicketReceipt view
                    setReceipt(data);
                    setSubject('');
                    setDescription('');
                    setFile(null);
                    setErrors({});
                } else if (response.status === 409) {
                    // Duplicate ticket conflict error
                    setServerError(data.message || 'Duplicate Ticket Submission Detected. An open ticket with identical details exists.');
                } else if (response.status === 400) {
                    // Validation error
                    setServerError(data.message || 'Validation failed. Please review your entries.');
                } else {
                    setServerError(data.message || 'Failed to submit ticket. Please try again later.');
                }
            } catch (err) {
                console.error('Error submitting support ticket:', err);
                setServerError('Network error. Unable to reach EduFrame Support server.');
            } finally {
                setIsSubmitting(false);
            }
        };

        /**
         * Resets component state to allow submitting a new support ticket.
         */
        const handleReset = () => {
            setReceipt(null);
            setServerError('');
            setErrors({});
            setSubject('');
            setDescription('');
            setFile(null);
        };

        /**
         * Triggers browser print view for saving eTicketReceipt.
         */
        const handlePrint = () => {
            window.print();
        };

        // Render eTicketReceipt View if receipt exists
        if (receipt) {
            return (
                <div className="ticket-receipt-card animate-fade">
                    <div className="receipt-header">
                        <div className="receipt-badge-tag">UC-05 Official Receipt</div>
                        <div className="receipt-icon-wrapper">
                            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                                <polyline points="22 4 12 14.01 9 11.01"></polyline>
                            </svg>
                        </div>
                        <h2 className="receipt-title">Support Ticket Submitted</h2>
                        <p className="receipt-subtitle">{receipt.receiptMessage || 'Your request has been logged in EduFrame portal.'}</p>
                    </div>

                    <div className="receipt-body">
                        <div className="receipt-grid">
                            <div className="receipt-item">
                                <span className="receipt-label">Ticket ID</span>
                                <span className="receipt-value ticket-id-highlight">{receipt.ticketId}</span>
                            </div>
                            <div className="receipt-item">
                                <span className="receipt-label">Current Status</span>
                                <span className="status-badge status-open">{receipt.status || 'Open'}</span>
                            </div>
                            <div className="receipt-item">
                                <span className="receipt-label">Subject</span>
                                <span className="receipt-value">{receipt.subject}</span>
                            </div>
                            <div className="receipt-item">
                                <span className="receipt-label">Submitted On</span>
                                <span className="receipt-value">
                                    {receipt.createdAt ? new Date(receipt.createdAt).toLocaleString() : new Date().toLocaleString()}
                                </span>
                            </div>
                            {receipt.attachmentName && (
                                <div className="receipt-item full-width">
                                    <span className="receipt-label">Attached File</span>
                                    <span className="receipt-value file-attachment-tag">
                                        📎 {receipt.attachmentName}
                                    </span>
                                </div>
                            )}
                            <div className="receipt-item full-width">
                                <span className="receipt-label">Description Body</span>
                                <p className="receipt-desc-box">{receipt.description}</p>
                            </div>
                        </div>
                    </div>

                    <div className="receipt-actions">
                        <button className="btn btn-outline-primary" onClick={handlePrint}>
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ marginRight: '6px' }}>
                                <polyline points="6 9 6 2 18 2 18 9"></polyline>
                                <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"></path>
                                <rect x="6" y="14" width="12" height="8"></rect>
                            </svg>
                            Print Receipt
                        </button>
                        <button className="btn btn-primary" onClick={handleReset}>
                            Submit Another Ticket
                        </button>
                        {isModal && (
                            <button className="btn btn-outline-light" onClick={onClose}>
                                Close Window
                            </button>
                        )}
                    </div>
                </div>
            );
        }

        // Render Form View
        return (
            <div className="ticket-form-card">
                <div className="form-card-header">
                    <div>
                        <h2>Submit Support Ticket</h2>
                        <p className="form-subtitle">Have a question or platform issue? Fill out the details below to reach EduFrame Help Desk.</p>
                    </div>
                    {isModal && (
                        <button className="modal-close-btn" onClick={onClose} aria-label="Close">
                            ✕
                        </button>
                    )}
                </div>

                {serverError && (
                    <div className="alert-banner alert-danger animate-fade">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <circle cx="12" cy="12" r="10"></circle>
                            <line x1="12" y1="8" x2="12" y2="12"></line>
                            <line x1="12" y1="16" x2="12.01" y2="16"></line>
                        </svg>
                        <span>{serverError}</span>
                    </div>
                )}

                <form onSubmit={handleSubmit} noValidate className="ticket-form">
                    {/* Subject Field */}
                    <div className="form-group">
                        <label htmlFor="ticketSubject" className="form-label required-field">
                            Subject / Issue Title
                        </label>
                        <input
                            id="ticketSubject"
                            type="text"
                            className={`form-input ${errors.subject ? 'is-invalid' : ''}`}
                            placeholder="e.g., Cannot access SE2030 lecture video stream"
                            value={subject}
                            onChange={(e) => {
                                setSubject(e.target.value);
                                if (errors.subject) setErrors({ ...errors, subject: null });
                            }}
                            disabled={isSubmitting}
                        />
                        {errors.subject && <span className="field-error">{errors.subject}</span>}
                    </div>

                    {/* Student Information (Optional Metadata) */}
                    <div className="form-row">
                        <div className="form-group col-half">
                            <label htmlFor="studentId" className="form-label">Student / User ID (Optional)</label>
                            <input
                                id="studentId"
                                type="text"
                                className="form-input"
                                placeholder="e.g., IT20104500"
                                value={studentId}
                                onChange={(e) => setStudentId(e.target.value)}
                                disabled={isSubmitting}
                            />
                        </div>
                        <div className="form-group col-half">
                            <label htmlFor="studentEmail" className="form-label">Contact Email (Optional)</label>
                            <input
                                id="studentEmail"
                                type="email"
                                className="form-input"
                                placeholder="e.g., student@my.sliit.lk"
                                value={studentEmail}
                                onChange={(e) => setStudentEmail(e.target.value)}
                                disabled={isSubmitting}
                            />
                        </div>
                    </div>

                    {/* Description Field */}
                    <div className="form-group">
                        <label htmlFor="ticketDescription" className="form-label required-field">
                            Detailed Description
                        </label>
                        <textarea
                            id="ticketDescription"
                            rows="5"
                            className={`form-input form-textarea ${errors.description ? 'is-invalid' : ''}`}
                            placeholder="Provide a step-by-step description of the problem, including course code and lecture name..."
                            value={description}
                            onChange={(e) => {
                                setDescription(e.target.value);
                                if (errors.description) setErrors({ ...errors, description: null });
                            }}
                            disabled={isSubmitting}
                        ></textarea>
                        {errors.description && <span className="field-error">{errors.description}</span>}
                    </div>

                    {/* File Attachment Field */}
                    <div className="form-group">
                        <label className="form-label">File Attachment (Optional - Max 5MB)</label>
                        <div className={`file-upload-dropzone ${errors.file ? 'is-invalid' : ''}`}>
                            <input
                                id="ticketFile"
                                type="file"
                                className="file-input-hidden"
                                onChange={(e) => {
                                    if (e.target.files.length) {
                                        setFile(e.target.files[0]);
                                        if (errors.file) setErrors({ ...errors, file: null });
                                    }
                                }}
                                disabled={isSubmitting}
                            />
                            <label htmlFor="ticketFile" className="dropzone-label">
                                <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                                    <polyline points="17 8 12 3 7 8"></polyline>
                                    <line x1="12" y1="3" x2="12" y2="15"></line>
                                </svg>
                                {file ? (
                                    <span className="file-selected-name">Attached: <strong>{file.name}</strong> ({(file.size / 1024).toFixed(1)} KB)</span>
                                ) : (
                                    <span>Click to upload or drag screenshot/document (.pdf, .png, .jpg, .docx)</span>
                                )}
                            </label>
                        </div>
                        {errors.file && <span className="field-error">{errors.file}</span>}
                    </div>

                    {/* Submit Action Controls */}
                    <div className="form-actions">
                        <button type="submit" className="btn btn-primary btn-submit-ticket" disabled={isSubmitting}>
                            {isSubmitting ? (
                                <span className="loading-spinner-wrapper">
                                    <span className="spinner"></span> Submitting Ticket...
                                </span>
                            ) : (
                                <span>
                                    Submit Ticket & Generate Receipt
                                </span>
                            )}
                        </button>
                    </div>
                </form>
            </div>
        );
    }

    // Expose component to global scope for Thymeleaf mount
    window.SupportTicketComponent = SupportTicket;

    // Auto-mount on elements with id "support-ticket-root" or "support-modal-root" if present
    document.addEventListener('DOMContentLoaded', () => {
        const rootEl = document.getElementById('support-ticket-root');
        if (rootEl && window.ReactDOM) {
            const root = ReactDOM.createRoot(rootEl);
            root.render(React.createElement(SupportTicket, { isModal: false }));
        }
    });
})();
