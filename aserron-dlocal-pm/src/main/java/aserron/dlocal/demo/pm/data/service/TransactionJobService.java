/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aserron.dlocal.demo.pm.data.service;

import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.stereotype.Service;

import aserron.dlocal.demo.pm.data.domain.TransactionStatus;
import aserron.dlocal.demo.pm.data.repositories.SaleRepository;

/**
 * Serves all scheduled task functionality.
 * @author Andres
 */
@Service
@EnableScheduling
public class TransactionJobService {

    private static final int JOB_TIME = 30000;
    private static final Logger log   = LoggerFactory.getLogger(ScheduledTask.class);
    
    /**
     * Sale Repository reference passed in the constructor.
     * The repository is meant to be called from the main controller
     * with the reference to the repository to avoid duplicated 
     * repositories and data integrity issues.
     */
    private final SaleRepository saleRepository;

    /**
     * The service instance hold a reference to the sale repository.
     * @param saleRepository A Sale Repository from the main controller.
     */
    public TransactionJobService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;        
        log.info("Task Scheduler Created: {}", LocalTime.now());
    }

    /**
     * Scheduled Task Method
     * Method annotated as scheduled task is triggered based on the timer 
     * configuration.
     * The rate is to the JOB_TIME milliseconds.
     */
    @Scheduled(fixedRate = JOB_TIME)
    public void scheduledTask() {        
    	
        log.info("[{}] JOB: executed scheduled task saleRepository={}",LocalTime.now(), this.saleRepository.hashCode());
        processTransactions();
    }

    /**
     * Get all sales from the repository
     * Inspect for status="PENDING" and performs a check.
     * Set the status to PAID or REJECTED based on the value returned
     * from .isRejected
     */
    private void processTransactions() {        

        // log.info("Task Scheduler saleRepository repo:{}", saleRepository);
        
        // could implement findAllDistinctStatusRejected
        saleRepository.findAll().forEach((t) -> {
            
            boolean pending = isPending(t.getStatus());
            
            if (pending && isRejected()) {
                t.setStatus(TransactionStatus.REJECTED);
                saleRepository.save(t);
            } else if (pending) {
                t.setStatus(TransactionStatus.PAID);
                saleRepository.save(t);
            }

            
            // log.warn("> Transcation is REJECTED: id={} status :{}", 
            //
            // t.getId(),t.getStatus());
            
        });

    }

    private boolean isPending(TransactionStatus status) {
        return status.equals(TransactionStatus.PENDING);
    }

    /**
     * Simulate the calculation of the probability associated with 
     * the payment of a given sale.
     * @return TRUE if the sale fail the test.
     */
    private boolean isRejected() {
        double probability = 0.7;
        return (this.getProbability() <= probability);
    }

    private double getProbability() {
        return Math.random();
    }

}
