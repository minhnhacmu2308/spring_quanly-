package com.quan_ly.spring.services.impls;

import com.quan_ly.spring.enums.ApproveStatus;
import com.quan_ly.spring.models.Process;
import com.quan_ly.spring.repositories.ProcessRepository;
import com.quan_ly.spring.services.ProcessService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessServiceImpl implements ProcessService {
    private final ProcessRepository processRepository;

    public ProcessServiceImpl(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    @Override
    public List<Process> getAllProcess() {
        return processRepository.findAll();
    }

    @Override
    public Optional<Process> getProcessById(Long id) {
        return processRepository.findById(id);
    }

    @Override
    public Process createProcess(Process risk) {
        return processRepository.save(risk);
    }


}
