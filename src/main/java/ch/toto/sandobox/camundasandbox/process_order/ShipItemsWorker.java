package ch.toto.sandobox.camundasandbox.process_order;

import java.util.Map;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ShipItemsWorker {
    private final static Logger LOG = LoggerFactory.getLogger(ShipItemsWorker.class);

    @JobWorker(type = "ship-items")
    public Map<String, String> shipItems(final ActivatedJob job) {
        LOG.info("Processing ship-items job: {}", job.getKey());
        LOG.info("ship-items job completed: {}", job.getKey());
        return Map.of();
    }
}
