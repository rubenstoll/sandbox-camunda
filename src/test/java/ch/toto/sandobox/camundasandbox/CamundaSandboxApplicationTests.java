package ch.toto.sandobox.camundasandbox;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import io.camunda.process.test.api.CamundaAssert;
import io.camunda.process.test.api.CamundaProcessTestContext;
import io.camunda.process.test.api.CamundaSpringProcessTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@CamundaSpringProcessTest
class CamundaSandboxApplicationTests {

    @Autowired
    private CamundaClient client;
    @Autowired
    private CamundaProcessTestContext processTestContext;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldCompleteProcessInstance() {
        // given: the processes are deployed
        client
                .newDeployResourceCommand()
                .addResourceFromClasspath("order-process.bpmn")
                .send()
                .join();

        // when
        final ProcessInstanceEvent processInstance = client
                .newCreateInstanceCommand()
                .bpmnProcessId("process1")
                .latestVersion()
                .send()
                .join();
        processTestContext.mockJobWorker("check-inventory").thenComplete();
        processTestContext.mockJobWorker("charge-payment").thenComplete();
        processTestContext.mockJobWorker("ship-items").thenComplete();
        // then
        CamundaAssert.assertThat(processInstance).isCompleted();
    }
}
