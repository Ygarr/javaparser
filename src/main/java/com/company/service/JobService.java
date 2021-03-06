package com.company.service;

import com.company.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface JobService {

    void save(Job job);

    Page<Job> getJobs(PageRequest request);
}
