package com.example.eventmanagement;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findByStudentEmail(String studentEmail);

    boolean existsByStudentEmailAndEventId(String studentEmail, Long eventId);

}