package com.jobportal.model;

import java.time.LocalDateTime;

import com.jobportal.util.DateUtil;

public class Application {

    private int id;
    private int jobId;
    private int seekerId;
    private String resumeName;
    private String coverNote;
    private String status;           // APPLIED, SHORTLISTED or REJECTED
    private LocalDateTime appliedAt;

    // Filled in when we join with other tables
    private String jobTitle;
    private String company;
    private String seekerName;
    private String seekerEmail;
    private String seekerPhone;

    public String getAppliedOn() {
        return DateUtil.format(appliedAt);
    }

    // The CSS badge colour for the current status
    public String getBadgeClass() {
        if ("SHORTLISTED".equals(status)) { return "badge ok"; }
        if ("REJECTED".equals(status))    { return "badge bad"; }
        return "badge";
    }

    // The label shown on the page, e.g. "Shortlisted"
    public String getStatusLabel() {
        if (status == null || status.isEmpty()) { return ""; }
        return status.substring(0, 1) + status.substring(1).toLowerCase();
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public int getSeekerId() {
		return seekerId;
	}

	public void setSeekerId(int seekerId) {
		this.seekerId = seekerId;
	}

	public String getResumeName() {
		return resumeName;
	}

	public void setResumeName(String resumeName) {
		this.resumeName = resumeName;
	}

	public String getCoverNote() {
		return coverNote;
	}

	public void setCoverNote(String coverNote) {
		this.coverNote = coverNote;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getAppliedAt() {
		return appliedAt;
	}

	public void setAppliedAt(LocalDateTime appliedAt) {
		this.appliedAt = appliedAt;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getSeekerName() {
		return seekerName;
	}

	public void setSeekerName(String seekerName) {
		this.seekerName = seekerName;
	}

	public String getSeekerEmail() {
		return seekerEmail;
	}

	public void setSeekerEmail(String seekerEmail) {
		this.seekerEmail = seekerEmail;
	}

	public String getSeekerPhone() {
		return seekerPhone;
	}

	public void setSeekerPhone(String seekerPhone) {
		this.seekerPhone = seekerPhone;
	}
}
