package com.quan_ly.spring.services;

import com.quan_ly.spring.models.Process;

import java.util.List;
import java.util.Optional;

public interface ProcessService {
    List<Process> getAllProcess();
    Optional<Process> getProcessById(Long id);
    Process createProcess(Process process);
}
