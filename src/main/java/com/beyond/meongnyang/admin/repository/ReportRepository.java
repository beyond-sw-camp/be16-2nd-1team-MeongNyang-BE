package com.beyond.meongnyang.admin.repository;

import com.beyond.meongnyang.admin.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
}
