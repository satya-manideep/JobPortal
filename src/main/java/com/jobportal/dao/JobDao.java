package com.jobportal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.jobportal.model.Job;
import com.jobportal.util.DBConnection;

public class JobDao {

    public boolean insert(Job job) throws SQLException {
        String sql = "INSERT INTO jobs (recruiter_id, title, company, location, job_type, "
                   + "salary, description, requirements) VALUES (?,?,?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, job.getRecruiterId());
            ps.setString(2, job.getTitle());
            ps.setString(3, job.getCompany());
            ps.setString(4, job.getLocation());
            ps.setString(5, job.getJobType());
            ps.setString(6, job.getSalary());          // null when left empty
            ps.setString(7, job.getDescription());
            ps.setString(8, job.getRequirements());

            return ps.executeUpdate() > 0;
        }
    }

    // One recruiter's jobs, newest first, each with its applicant count
    public List<Job> findByRecruiter(int recruiterId) throws SQLException {
        String sql = "SELECT j.id, j.recruiter_id, j.title, j.company, j.location, j.job_type, "
                   + "j.salary, j.description, j.requirements, j.posted_at, "
                   + "COUNT(a.id) AS applicant_count "
                   + "FROM jobs j LEFT JOIN applications a ON a.job_id = j.id "
                   + "WHERE j.recruiter_id = ? "
                   + "GROUP BY j.id "
                   + "ORDER BY j.posted_at DESC, j.id DESC";

        List<Job> jobs = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recruiterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Job job = mapJob(rs);
                    job.setApplicantCount(rs.getInt("applicant_count"));
                    jobs.add(job);
                }
            }
        }
        return jobs;
    }

    // Shortlisted applications across all of this recruiter's jobs
    public int countShortlisted(int recruiterId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM applications a "
                   + "JOIN jobs j ON a.job_id = j.id "
                   + "WHERE j.recruiter_id = ? AND a.status = 'SHORTLISTED'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, recruiterId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // Deletes the job only if it belongs to this recruiter
    public boolean deleteOwnJob(int jobId, int recruiterId) throws SQLException {
        String sql = "DELETE FROM jobs WHERE id = ? AND recruiter_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ps.setInt(2, recruiterId);
            return ps.executeUpdate() > 0;
        }
    }
    // Browse and search. Empty filters are ignored.
    public List<Job> search(String keyword, String location, String type) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT id, recruiter_id, title, company, location, job_type, salary, "
              + "description, requirements, posted_at FROM jobs WHERE 1=1");
        List<String> params = new ArrayList<>();

        if (!keyword.isEmpty()) {
            sql.append(" AND (title LIKE ? OR company LIKE ? OR description LIKE ?)");
            String like = "%" + escapeLike(keyword) + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (!location.isEmpty()) {
            sql.append(" AND location LIKE ?");
            params.add("%" + escapeLike(location) + "%");
        }
        if (!type.isEmpty()) {
            sql.append(" AND job_type = ?");
            params.add(type);
        }
        sql.append(" ORDER BY posted_at DESC, id DESC LIMIT 100");

        List<Job> jobs = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapJob(rs));
                }
            }
        }
        return jobs;
    }

    public List<Job> findLatest(int limit) throws SQLException {
        String sql = "SELECT id, recruiter_id, title, company, location, job_type, salary, "
                   + "description, requirements, posted_at FROM jobs "
                   + "ORDER BY posted_at DESC, id DESC LIMIT ?";
        List<Job> jobs = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapJob(rs));
                }
            }
        }
        return jobs;
    }

    // Returns null when there is no job with this id
    public Job findById(int id) throws SQLException {
        String sql = "SELECT id, recruiter_id, title, company, location, job_type, salary, "
                   + "description, requirements, posted_at FROM jobs WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapJob(rs) : null;
            }
        }
    }

    // Stops a typed % or _ from acting as a LIKE wildcard
    private static String escapeLike(String text) {
        return text.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
    // Returns the job only if it belongs to this recruiter, otherwise null
    public Job findOwnById(int jobId, int recruiterId) throws SQLException {
        String sql = "SELECT id, recruiter_id, title, company, location, job_type, salary, "
                   + "description, requirements, posted_at FROM jobs "
                   + "WHERE id = ? AND recruiter_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ps.setInt(2, recruiterId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapJob(rs) : null;
            }
        }
    }

    private Job mapJob(ResultSet rs) throws SQLException {
        Job job = new Job();
        job.setId(rs.getInt("id"));
        job.setRecruiterId(rs.getInt("recruiter_id"));
        job.setTitle(rs.getString("title"));
        job.setCompany(rs.getString("company"));
        job.setLocation(rs.getString("location"));
        job.setJobType(rs.getString("job_type"));
        job.setSalary(rs.getString("salary"));
        job.setDescription(rs.getString("description"));
        job.setRequirements(rs.getString("requirements"));
        Timestamp posted = rs.getTimestamp("posted_at");
        job.setPostedAt(posted == null ? null : posted.toLocalDateTime());
        return job;
    }
}