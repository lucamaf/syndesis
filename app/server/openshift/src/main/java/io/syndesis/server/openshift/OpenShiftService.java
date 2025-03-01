/*
 * Copyright (C) 2016 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.syndesis.server.openshift;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.User;

public interface OpenShiftService {

    String INTEGRATION_NAME_ANNOTATION = "syndesis.io/integration-name";
    String DEPLOYMENT_ID_ANNOTATION = "syndesis.io/deploy-id";
    String PROMETHEUS_PORT_ANNOTATION = "prometheus.io/port";
    String PROMETHEUS_SCRAPE_ANNOTATION = "prometheus.io/scrape";
    String DEPLOYMENT_REPLICAS_ANNOTATION = "syndesis.io/deploy-replicas";

    String INTEGRATION_ID_LABEL = "syndesis.io/integration-id";
    String INTEGRATION_NAME_LABEL = "syndesis.io/integration";
    String INTEGRATION_TYPE_LABEL = "syndesis.io/type";
    String INTEGRATION_APP_LABEL = "syndesis.io/app";
    String DEPLOYMENT_VERSION_LABEL = "syndesis.io/deployment-version";
    String USERNAME_LABEL = "syndesis.io/username";
    String COMPONENT_LABEL = "syndesis.io/component";

    int INTEGRATION_SERVICE_PORT = 8080;

    /**
     * Start a previously created build with the data from the given directory
     * @param name name of the build
     * @param data the deployment data to use
     * @param tarInputStream input stream representing a tar file containing the project files
     * @return the image digest.
     */
    String build(String name, DeploymentData data, InputStream tarInputStream) throws InterruptedException;

    /**
     * Perform a deployment
     * @param data the deployment data to use
     * @param name name of the deployment to trigger
     * @return the revision of the deployment.
     */
    String deploy(String name, DeploymentData data);

    /**
     * Check whether a deployment is ready
     * @param name name of the deployment to check
     * @return true if deployment is ready, false otherwise
     */
    boolean isDeploymentReady(String name);

    /**
     * Check whether a given build is started
     * @param name name of the build to check
     * @return true if the build is started and running
     */
    boolean isBuildStarted(String name);

    /**
     * Deletes the deployment (Deployment and Build configurations, Image Streams etc)
     * @param name of the deployment to delete
     * @return          Returns True if all resources were deleted, False otherwise.
     */
    boolean delete(String name);

    /**
     * Checks if the deployment (Deployment and Build configurations, Image Streams etc) exists
     * @param name of the deployment to delete
     * @return          Returns True if all resources were deleted, False otherwise.
     */
    boolean exists(String name);

    /**
     * Scale the deployment (Deployment and Build configurations, Image Streams etc)
     * @param name of the deployment to delete
     * @param labels a set of labels that need to be match.
     * @param desiredReplicas how many replicas to scale to
     * @param amount of time to wait for scaling
     * @param timeUnit of the time
     */
    void scale(String name, Map<String, String> labels, int desiredReplicas, long amount, TimeUnit timeUnit) throws InterruptedException;


    /**
     * Checks if the deployment (Deployment and Build configurations, Image Streams etc) is scaled.
     * @param name of the deployment to delete
     * @param desiredMinimumReplicas how many replicas should be running at a minimum for this method to return true
     * @param labels a set of labels that need to be match.
     */
    boolean isScaled(String name, int desiredMinimumReplicas, Map<String, String> labels);

    /**
     * Check whether a given build is failed
     * @param name name of the build to check
     * @return true if the build is failed
     */
    boolean isBuildFailed(String name);

    /**
     * Returns the {@link DeploymentConfig}s that match the specified labels.
     * @param labels            The specified labels.
     * @return                  The list of {@link DeploymentConfig}s.
     */
    List<DeploymentConfig> getDeploymentsByLabel(Map<String, String> labels);

    /**
     * Returns the currently logged in user.
     * @return The currently logged in user.
     */
    User whoAmI(String username);

    /**
     * Returns the hostname exposed by Openshift for the integration, if any.
     * @param name the name of the route to check
     * @return a Optional containing the URL
     */
    Optional<String> getExposedHost(String name);

    /**
     * Create a Custom Resource Definition (CRD) given the Yaml definition as an InputStream
     * @param cdrYamlStream the CRD yaml definition as an {@link InputStream}
     * @return a List of {@link HasMetadata} as the operation result
     */
    List<HasMetadata> createOrReplaceCRD(InputStream cdrYamlStream);

    void createOrReplaceSecret(Secret secret);

    ConfigMap createOrReplaceConfigMap(ConfigMap configMap);
}
