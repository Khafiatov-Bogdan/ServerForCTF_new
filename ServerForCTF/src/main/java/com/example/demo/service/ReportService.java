package com.example.demo.service;

import com.example.demo.Report;
import com.example.demo.repository.ReportRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // Сохранение или обновление одного Report
    public Report saveOrUpdateReport(Report newReport) {
        Report existing = reportRepository.findByName(newReport.getName());

        if (existing != null) {
            existing.setPercent(newReport.getPercent());
            existing.setGrade(newReport.getGrade());
            return reportRepository.save(existing);
        }

        return reportRepository.save(newReport);
    }
}
