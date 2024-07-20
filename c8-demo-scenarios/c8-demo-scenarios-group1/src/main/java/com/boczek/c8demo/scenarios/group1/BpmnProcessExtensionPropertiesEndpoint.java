package com.boczek.c8demo.scenarios.group1;

import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.model.bpmn.instance.Process;
import io.camunda.zeebe.model.bpmn.instance.zeebe.ZeebeProperties;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

@Component
// TODO (Adam) Endpoint ID 'bpmn-process-extension-properties' contains invalid characters, please migrate to a valid format.
@Endpoint(id = "bpmn-process-extension-properties")
@SuppressWarnings("unused")
public class BpmnProcessExtensionPropertiesEndpoint {

    @ReadOperation
    @SuppressWarnings("unused")
    public List<Map<String, Object>> showBpmnProcessExtensionProperties() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] resources = resolver.getResources("classpath:bpmn/*.bpmn");

            for (Resource resource : resources) {
                var generalProcessProperties = new LinkedHashMap<String, Object>();
                try (InputStream inputStream = resource.getInputStream()) {
                    BpmnModelInstance modelInstance = Bpmn.readModelFromStream(inputStream);

                    Process process = modelInstance.getModelElementsByType(Process.class).iterator().next();
                    generalProcessProperties.put("processId", process.getId());
                    generalProcessProperties.put("processName", process.getName());

                    var extensionProcessProperties = new LinkedHashMap<String, Object>();
                    Optional.ofNullable(process.getExtensionElements())
                            .map(extensionElements -> extensionElements
                                    .getElementsQuery()
                                    .filterByType(ZeebeProperties.class)
                                    .singleResult())
                            .ifPresentOrElse(
                                    zeebeProperties -> zeebeProperties.getProperties().forEach(
                                            zeebeProperty -> extensionProcessProperties.put(zeebeProperty.getName(), zeebeProperty.getValue())
                                    ),
                                    () -> {
                                        extensionProcessProperties.put("warning", "No extension properties for found!");
                                    }
                            );

                    if (!extensionProcessProperties.isEmpty()) {
                        generalProcessProperties.put("extensionProperties", extensionProcessProperties);
                    }

                } catch (Exception e) {
                    generalProcessProperties.put("error", "Error reading BPMN resource %s due to %s.".formatted(resource.getFilename(), e.getMessage()));
                }
                resultList.add(generalProcessProperties);
            }
        } catch (Exception e) {
            var errorMap = new HashMap<String, Object>();
            errorMap.put("error", "Error loading BPMN resource due to %s.".formatted(e.getMessage()));
            resultList.add(errorMap);
        }

        return resultList;
    }
}

