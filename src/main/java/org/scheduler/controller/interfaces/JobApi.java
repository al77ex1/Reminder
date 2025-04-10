package org.scheduler.controller.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.scheduler.dto.response.JobResponse;
import org.scheduler.interceptor.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Jobs", description = "Операции связанные с заданиями планировщика")
@RequestMapping("/api/v1/jobs")
@SecurityRequirement(name = "bearerAuth")
public interface JobApi {

    @GetMapping
    @Operation(
        summary = "Получить список всех активных заданий",
        description = "Предоставляет информацию о всех заданиях в планировщике"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Успешное получение списка заданий",
        content = @Content(schema = @Schema(implementation = JobResponse.class))
    )
    ResponseEntity<List<JobResponse>> getAllJobs();

    @GetMapping("/groups/{groupName}")
    @Operation(
        summary = "Получить список заданий по группе",
        description = "Предоставляет информацию о заданиях в указанной группе"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Успешное получение списка заданий",
        content = @Content(schema = @Schema(implementation = JobResponse.class))
    )
    @ApiResponse(
        responseCode = "404", 
        description = "Группа не найдена",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    ResponseEntity<List<JobResponse>> getJobsByGroup(
        @PathVariable String groupName
    );

    @GetMapping("/{jobName}")
    @Operation(
        summary = "Получить информацию о задании по имени",
        description = "Предоставляет детальную информацию о задании по его имени"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Успешное получение информации о задании",
        content = @Content(schema = @Schema(implementation = JobResponse.class))
    )
    @ApiResponse(
        responseCode = "404", 
        description = "Задание не найдено",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    ResponseEntity<JobResponse> getJobByName(
        @PathVariable String jobName
    );

    @GetMapping("/running")
    @Operation(
        summary = "Получить список выполняющихся заданий сейчас",
        description = "Предоставляет информацию о заданиях, которые в данный момент выполняются"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Успешное получение списка выполняющихся заданий",
        content = @Content(schema = @Schema(implementation = JobResponse.class))
    )
    ResponseEntity<List<JobResponse>> getRunningJobs();
}
