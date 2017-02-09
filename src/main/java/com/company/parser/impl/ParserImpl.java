package com.company.parser.impl;

import com.company.exception.ParserException;
import com.company.parser.Parser;
import com.company.parser.siteparsers.JobParser;
import com.company.entity.Job;
import com.company.notifier.Notifier;
import com.company.service.JobService;
import com.company.sites.JobSite;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ParserImpl implements Parser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParserImpl.class);

    private JobService jobService;
    private Notifier notifier;

    @Autowired
    public ParserImpl(JobService jobService, Notifier notifier) {
        this.jobService = jobService;
        this.notifier = notifier;
    }

    public void parse(JobSite jobSite) {

        JobParser jobParser = jobSite.getParser();

        try {
            Document doc = jobParser.getDoc(jobSite.getSiteUrl());
            Elements jobBlocks = jobParser.getJobBlocks(doc);

            for (Element job : jobBlocks) {

                Elements titleBlock = jobParser.getTitleBlock(job);
                String url = jobParser.getUrl(titleBlock);
                String title = jobParser.getTitle(titleBlock);
                String description = jobParser.getDescription(job, url);
                String company = jobParser.getCompany(job, url);
                LocalDateTime date = jobParser.getDate(job, url);

                Job parsedJob = new Job(title, description, company, jobSite.getSiteName(), url, date);
                jobService.save(parsedJob);
            }
            LOGGER.info("Parsing of {} completed\n", jobSite.getSiteName());
        } catch (ParserException e) {
            notifier.notifyAdmin(e.getMessage());
        }
    }
}