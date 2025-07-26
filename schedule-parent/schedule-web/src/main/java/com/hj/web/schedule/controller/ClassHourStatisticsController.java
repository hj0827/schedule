package com.hj.web.schedule.controller;

import com.hj.web.schedule.entity.ClassHourStatistics;
import com.hj.web.schedule.service.ClassHourStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class ClassHourStatisticsController {

    @Autowired
    private ClassHourStatisticsService classHourStatisticsService;

    @GetMapping("/list")
    public ResponseEntity<List<ClassHourStatistics>> getStatistics(
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(classHourStatisticsService.findStatistics(teacherId, courseId, startDate, endDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassHourStatistics> getStatisticsById(@PathVariable Long id) {
        return ResponseEntity.ok(classHourStatisticsService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> createStatistics(@RequestBody ClassHourStatistics statistics) {
        classHourStatisticsService.createStatistics(statistics);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateStatistics(
            @PathVariable Long id,
            @RequestBody ClassHourStatistics statistics) {
        statistics.setId(id);
        classHourStatisticsService.updateStatistics(statistics);
        return ResponseEntity.ok().build();
    }
} 