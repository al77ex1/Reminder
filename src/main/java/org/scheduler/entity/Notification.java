package org.scheduler.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Column(name = "trigger_name", nullable = false)
    private String triggerName;

    @Column(name = "cron_schedule", nullable = false)
    private String cronSchedule;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
