/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.mytechia.robobo.framework.remotecontrol.ros;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ros.concurrent.DefaultScheduledExecutorService;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.DefaultNodeFactory;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeFactory;
import org.ros.node.NodeListener;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Executes {@link NodeMain}s in separate threads.
 *
 * @author damonkohler@google.com (Damon Kohler)
 */
public class RoboboNodeMainExecutor implements NodeMainExecutor {

    private static final boolean DEBUG = false;
    private static final Log log = LogFactory.getLog(RoboboNodeMainExecutor.class);

    private final NodeFactory nodeFactory;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Multimap<GraphName, ConnectedNode> connectedNodes;
    private final BiMap<Node, NodeMain> nodeMains;

    private class RegistrationListener implements NodeListener {
        @Override
        public void onStart(ConnectedNode connectedNode) {
            registerNode(connectedNode);
        }

        @Override
        public void onShutdown(Node node) {
        }

        @Override
        public void onShutdownComplete(Node node) {
            unregisterNode(node);
        }

        @Override
        public void onError(Node node, Throwable throwable) {
            log.error("Node error.", throwable);
            unregisterNode(node);
        }
    }

    /**
     * @return an instance of {@link RoboboNodeMainExecutor} that uses a
     *         {@link ScheduledExecutorService} that is suitable for both
     *         executing tasks immediately and scheduling tasks to execute in the
     *         future
     */
    public static NodeMainExecutor newDefault() {
        return newDefault(new DefaultScheduledExecutorService());
    }

    /**
     * @param executorService
     * @return an instance of {@link RoboboNodeMainExecutor} that uses the
     *         supplied {@link ExecutorService}
     */
    public static NodeMainExecutor newDefault(ScheduledExecutorService executorService) {
        return new RoboboNodeMainExecutor(new DefaultNodeFactory(executorService), executorService);
    }

    /**
     * @param nodeFactory
     *          {@link NodeFactory} to use for node creation.
     * @param scheduledExecutorService
     *          {@link NodeMain}s will be executed using this
     */
    private RoboboNodeMainExecutor(NodeFactory nodeFactory,
                                   ScheduledExecutorService scheduledExecutorService) {
        this.nodeFactory = nodeFactory;
        this.scheduledExecutorService = scheduledExecutorService;
        connectedNodes =
                Multimaps.synchronizedMultimap(HashMultimap.<GraphName, ConnectedNode>create());
        nodeMains = Maps.synchronizedBiMap(HashBiMap.<Node, NodeMain>create());
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                RoboboNodeMainExecutor.this.shutdown();
            }
        }));
    }

    @Override
    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    @Override
    public void execute(final NodeMain nodeMain, final NodeConfiguration nodeConfiguration,
                        final Collection<NodeListener> nodeListeners) {
        // NOTE(damonkohler): To avoid a race condition, we have to make our copy
        // of the NodeConfiguration in the current thread.
        final NodeConfiguration nodeConfigurationCopy = NodeConfiguration.copyOf(nodeConfiguration);
        nodeConfigurationCopy.setDefaultNodeName(nodeMain.getDefaultNodeName());
        Preconditions.checkNotNull(nodeConfigurationCopy.getNodeName(), "Node name not specified.");
        if (DEBUG) {
            log.info("Starting node: " + nodeConfigurationCopy.getNodeName());
        }
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                Collection<NodeListener> nodeListenersCopy = Lists.newArrayList();
                nodeListenersCopy.add(new RegistrationListener());
                nodeListenersCopy.add(nodeMain);
                if (nodeListeners != null) {
                    nodeListenersCopy.addAll(nodeListeners);
                }
                // The new Node will call onStart().
                Node node = nodeFactory.newNode(nodeConfigurationCopy, nodeListenersCopy);
                nodeMains.put(node, nodeMain);
            }
        });
    }

    @Override
    public void execute(NodeMain nodeMain, NodeConfiguration nodeConfiguration) {
        execute(nodeMain, nodeConfiguration, null);
    }

    @Override
    public void shutdownNodeMain(NodeMain nodeMain) {
        Node node = nodeMains.inverse().get(nodeMain);
        if (node != null) {
            safelyShutdownNode(node);
        }
    }

    @Override
    public void shutdown() {
        synchronized (connectedNodes) {

            List<ConnectedNode> connectNodesToShutDown = new ArrayList<>(connectedNodes.values());

            for (ConnectedNode connectedNode : connectNodesToShutDown) {
                safelyShutdownNode(connectedNode);
            }
        }
    }

    /**
     * Trap and log any exceptions while shutting down the supplied {@link Node}.
     *
     * @param node
     *          the {@link Node} to shut down
     */
    private void safelyShutdownNode(Node node) {
        boolean success = true;
        try {
            node.shutdown();
        } catch (Exception e) {
            // Ignore spurious errors during shutdown.
            log.error("Exception thrown while shutting down node.", e);
            // We don't expect any more callbacks from a node that throws an exception
            // while shutting down. So, we unregister it immediately.
            try {
                unregisterNode(node);
            }catch(Throwable th){
                log.error("Error unregisterNode", th);
            }
            success = false;
        }
        if (success) {
            log.info("Shutdown successful.");
        }
    }

    /**
     * Register a {@link ConnectedNode} with the {@link NodeMainExecutor}.
     *
     * @param connectedNode
     *          the {@link ConnectedNode} to register
     */
    private void registerNode(ConnectedNode connectedNode) {
        GraphName nodeName = connectedNode.getName();
        synchronized (connectedNodes) {
            for (ConnectedNode illegalConnectedNode : connectedNodes.get(nodeName)) {
                System.err.println(String.format(
                        "Node name collision. Existing node %s (%s) will be shutdown.", nodeName,
                        illegalConnectedNode.getUri()));
                illegalConnectedNode.shutdown();
            }
            connectedNodes.put(nodeName, connectedNode);
        }
    }

    /**
     * Unregister a {@link Node} with the {@link NodeMainExecutor}.
     *
     * @param node
     *          the {@link Node} to unregister
     */
    private void unregisterNode(Node node) {
        node.removeListeners();
        connectedNodes.get(node.getName()).remove(node);
        nodeMains.remove(node);
    }
}