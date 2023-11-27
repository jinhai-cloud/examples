package org.cloud.examples.kubernetes;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import org.junit.jupiter.api.Test;

@EnableKubernetesMockClient(crud = true)
public class Main {
    private KubernetesClient client;

    @Test
    void testCrud() {

    }
}
