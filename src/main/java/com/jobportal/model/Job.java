package com.jobportal.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.jobportal.util.DateUtil;

public class Job {

    private int id;
    private int recruiterId;
    private String title;
    private String company;
    private String location;
    private String jobType;
    private String salary;
    private String description;
    private String requirements;
    private LocalDateTime postedAt;

    private int applicantCount;      // counted from the applications table, not stored

    // "Posted on" text for the pages
    public String getPostedOn() {
        return DateUtil.format(postedAt);
    }

    // Each line of the requirements box becomes one bullet
    public List<String> getRequirementList() {
        List<String> list = new ArrayList<>();
        if (requirements != null) {
            for (String line : requirements.split("\\R")) {
                if (!line.trim().isEmpty()) {
                    list.add(line.trim());
                }
            }
        }
        return list;
    }
    // First ~120 characters of the description, for the job cards
    public String getSummary() {
        if (description == null) {
            return "";
        }
        String text = description.replaceAll("\\s+", " ").trim();
        return text.length() <= 120 ? text : text.substring(0, 117) + "...";
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRecruiterId() {
		return recruiterId;
	}

	public void setRecruiterId(int recruiterId) {
		this.recruiterId = recruiterId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRequirements() {
		return requirements;
	}

	public void setRequirements(String requirements) {
		this.requirements = requirements;
	}

	public LocalDateTime getPostedAt() {
		return postedAt;
	}

	public void setPostedAt(LocalDateTime postedAt) {
		this.postedAt = postedAt;
	}

	public int getApplicantCount() {
		return applicantCount;
	}

	public void setApplicantCount(int applicantCount) {
		this.applicantCount = applicantCount;
	}
}