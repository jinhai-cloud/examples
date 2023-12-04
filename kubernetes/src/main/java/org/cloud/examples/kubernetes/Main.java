package org.cloud.examples.kubernetes;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.utils.Serialization;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class Main {
    public static void main(String[] args) {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            Pod pod = client.pods().load(Main.class.getResourceAsStream("/pod.yaml")).item();
            Pod update = pod.edit().editMetadata().withLabels(Map.of("server", "nginx")).endMetadata().build();

            log.info(Serialization.asYaml(pod));
            log.info(Serialization.asYaml(update));
        }
    }
}
