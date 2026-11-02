package com.jobportal.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.jobportal.model.Application;
import com.jobportal.util.DBConnection;

public class ApplicationDao {

    public boolean hasApplied(int jobId, int seekerId) throws SQLException {
        String sql = "SELECT 1 FROM applications WHERE job_id = ? AND seeker_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ps.setInt(2, seekerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Saves one application. The resume is streamed into the LONGBLOB column.
    public boolean insert(int jobId, int seekerId, InputStream resume, long resumeSize,
                          String resumeName, String resumeType, String coverNote) throws SQLException {

        String sql = "INSERT INTO applications "
                   + "(job_id, seeker_id, resume_data, resume_name, resume_type, cover_note) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ps.setInt(2, seekerId);
            ps.setBinaryStream(3, resume, resumeSize);
            ps.setString(4, resumeName);
            ps.setString(5, resumeType);
            ps.setString(6, coverNote.isEmpty() ? null : coverNote);

            return ps.executeUpdate() > 0;
        }
    }
    
    // All applications made by one seeker, newest first, with the job's title and company
    public List<Application> findBySeeker(int seekerId) throws SQLException {
        String sql = "SELECT a.id, a.job_id, a.resume_name, a.cover_note, a.status, a.applied_at, "
                   + "j.title, j.company "
                   + "FROM applications a JOIN jobs j ON a.job_id = j.id "
                   + "WHERE a.seeker_id = ? "
                   + "ORDER BY a.applied_at DESC, a.id DESC";

        List<Application> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, seekerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Application a = new Application();
                    a.setId(rs.getInt("id"));
                    a.setJobId(rs.getInt("job_id"));
                    a.setSeekerId(seekerId);
                    a.setResumeName(rs.getString("resume_name"));
                    a.setCoverNote(rs.getString("cover_note"));
                    a.setStatus(rs.getString("status"));
                    Timestamp applied = rs.getTimestamp("applied_at");
                    a.setAppliedAt(applied == null ? null : applied.toLocalDateTime());
                    a.setJobTitle(rs.getString("title"));
                    a.setCompany(rs.getString("company"));
                    list.add(a);
                }
            }
        }
        return list;
    }
        
        // Applicants for one job, only if the job belongs to this recruiter
        public List<Application> findByJob(int jobId, int recruiterId) throws SQLException {
            String sql = "SELECT a.id, a.job_id, a.seeker_id, a.resume_name, a.cover_note, "
                       + "a.status, a.applied_at, u.full_name, u.email, u.phone "
                       + "FROM applications a "
                       + "JOIN jobs j ON a.job_id = j.id "
                       + "JOIN users u ON a.seeker_id = u.id "
                       + "WHERE a.job_id = ? AND j.recruiter_id = ? "
                       + "ORDER BY a.applied_at DESC, a.id DESC";

            List<Application> list = new ArrayList<>();

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, jobId);
                ps.setInt(2, recruiterId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Application a = new Application();
                        a.setId(rs.getInt("id"));
                        a.setJobId(rs.getInt("job_id"));
                        a.setSeekerId(rs.getInt("seeker_id"));
                        a.setResumeName(rs.getString("resume_name"));
                        a.setCoverNote(rs.getString("cover_note"));
                        a.setStatus(rs.getString("status"));
                        Timestamp applied = rs.getTimestamp("applied_at");
                        a.setAppliedAt(applied == null ? null : applied.toLocalDateTime());
                        a.setSeekerName(rs.getString("full_name"));
                        a.setSeekerEmail(rs.getString("email"));
                        a.setSeekerPhone(rs.getString("phone"));
                        list.add(a);
                    }
                }
            }
            return list;
        }

        // Changes the status only if the application is for one of this recruiter's jobs
        public boolean updateStatus(int applicationId, int recruiterId, String status) throws SQLException {
            String sql = "UPDATE applications a JOIN jobs j ON a.job_id = j.id "
                       + "SET a.status = ? WHERE a.id = ? AND j.recruiter_id = ?";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, status);
                ps.setInt(2, applicationId);
                ps.setInt(3, recruiterId);
                return ps.executeUpdate() > 0;
            }
        }

        // The resume's file name, or null if the application is not for this recruiter's job
        public String findResumeNameForRecruiter(int applicationId, int recruiterId) throws SQLException {
            String sql = "SELECT a.resume_name FROM applications a "
                       + "JOIN jobs j ON a.job_id = j.id "
                       + "WHERE a.id = ? AND j.recruiter_id = ?";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, applicationId);
                ps.setInt(2, recruiterId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getString(1) : null;
                }
            }
        }

        // Streams the stored PDF into the response, again only for the job's recruiter
        public void copyResumeForRecruiter(int applicationId, int recruiterId, OutputStream out)
                throws SQLException, IOException {
            String sql = "SELECT a.resume_data FROM applications a "
                       + "JOIN jobs j ON a.job_id = j.id "
                       + "WHERE a.id = ? AND j.recruiter_id = ?";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, applicationId);
                ps.setInt(2, recruiterId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        try (InputStream in = rs.getBinaryStream(1)) {
                            if (in != null) {
                                in.transferTo(out);
                            }
                        }
                    }
                }
            }
    }
    
}