/*
 * Copyright 2012-2016 Aerospike, Inc.
 *
 * Portions may be licensed to Aerospike, Inc. under one or more contributor
 * license agreements WHICH ARE COMPATIBLE WITH THE APACHE LICENSE, VERSION 2.0.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.aerospike.client.policy;

/**
 * Container object for transaction policy attributes used in all database
 * operation calls.
 */
public class Policy {
	/**
	 * Priority of request relative to other transactions.
	 * Currently, only used for scans.
	 */
	public Priority priority = Priority.DEFAULT;
	
	/**
	 * How replicas should be consulted in a read operation to provide the desired
	 * consistency guarantee. Default to allowing one replica to be used in the
	 * read operation.
	 */
	public ConsistencyLevel consistencyLevel = ConsistencyLevel.CONSISTENCY_ONE;

	/**
	 * Send read commands to the node containing the key's partition replica type.
	 * Write commands are not affected by this setting, because all writes are directed 
	 * to the node containing the key's master partition.
	 * <p>
	 * Default to sending read commands to the node containing the key's master partition.
	 */
	public Replica replica = Replica.MASTER;
	
	/**
	 * Total transaction timeout in milliseconds for both client and server.
	 * The timeout is tracked on the client and also sent to the server along 
	 * with the transaction in the wire protocol.  The client will most likely
	 * timeout first, but the server has the capability to timeout the transaction
	 * as well.
	 * <p>
	 * The timeout is also used as a socket timeout.  Retries will not occur
	 * if the timeout limit has been reached.
	 * Default to no timeout (0).
	 */
	public int timeout;
	
	/**
	 * Delay milliseconds after transaction timeout before closing socket in async mode only.
	 * When a transaction is stopped prematurely, the socket must be closed and not placed back
	 * on the pool. This is done to prevent unread socket data from corrupting the next transaction
	 * that would use that socket.
	 * <p>
	 * This field delays the closing of the socket to give the transaction more time to complete
	 * in the hope that the socket can be reused.  This is helpful when timeouts are aggressive 
	 * and a certain percentage of timeouts is expected.
	 * <p>
	 * The user is still notified of the timeout in async mode at the original timeout value.
	 * The transaction's async timer is then reset to this delay and the transaction is allowed
	 * to continue.  If the transactions succeeds within the delay, then the socket is placed back
	 * on the pool and the transaction response is discarded.  Otherwise, the socket must be closed.
	 * <p>
	 * This field is ignored in sync mode because control must be returned back to user on timeout 
	 * and there is no currently available thread pool to process the delay.
	 * <p>
	 * Default: 0 (no delay, connection closed on timeout)
	 */
	public int timeoutDelay;

	/**
	 * Maximum number of retries before aborting the current transaction.
	 * A retry is attempted when there is a network error other than timeout.  
	 * If maxRetries is exceeded, the abort will occur even if the timeout 
	 * has not yet been exceeded.
	 * <p>
	 * This field is ignored in async mode.  Async transactions only retry on
	 * invalid connections in the connection pool which is not bounded.
	 * <p>
	 * Default: 1
	 */
	public int maxRetries = 1;

	/**
	 * Milliseconds to sleep between retries if a transaction fails and the 
	 * timeout was not exceeded.  Enter zero to skip sleep.
	 * <p>
	 * This field is ignored in async mode.
	 * <p>
	 * Default: 500ms
	 */
	public int sleepBetweenRetries = 500;
	
	/**
	 * Send user defined key in addition to hash digest on both reads and writes.  
	 * The default is to not send the user defined key.
	 */
	public boolean sendKey;
	
	/**
	 * Copy policy from another policy.
	 */
	public Policy(Policy other) {
		this.priority = other.priority;
		this.consistencyLevel = other.consistencyLevel;
		this.replica = other.replica;
		this.timeout = other.timeout;
		this.timeoutDelay = other.timeoutDelay;
		this.maxRetries = other.maxRetries;
		this.sleepBetweenRetries = other.sleepBetweenRetries;
		this.sendKey = other.sendKey;
	}
	
	/**
	 * Default constructor.
	 */
	public Policy() {
	}
}
