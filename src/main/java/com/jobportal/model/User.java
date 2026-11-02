package com.jobportal.model;

public class User {

    private int id;
    private String fullName;
    private String email;
    private String password;   // always the BCrypt hash, never plain text
    private String role;       // "SEEKER" or "RECRUITER"
    private String phone;
    private String company;    // recruiters only

    public boolean isRecruiter() {
        return "RECRUITER".equals(role);
    }

    public boolean isSeeker() {
        return "SEEKER".equals(role);
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
}