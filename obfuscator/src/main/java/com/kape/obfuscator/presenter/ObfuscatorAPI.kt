/*
 * Copyright (c) "2023" Private Internet Access, Inc.
 *
 *  This file is part of the Private Internet Access Android Client.
 *
 *  The Private Internet Access Android Client is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published by the Free
 *  Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  The Private Internet Access Android Client is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 *  details.
 *
 *  You should have received a copy of the GNU General Public License along with the Private
 *  Internet Access Android Client.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.kape.obfuscator.presenter

/**
 * Interface defining the API available to the clients.
 */
public interface ObfuscatorAPI {

    /**
     * @param commandLineParams `List<String>`. String representation of the parameters to be used
     * when starting the process. e.g. `-s 1.1.1.1 -p 8080`
     * @param obfuscatorProcessEventHandler `ShadowsocksProcessEventHandler`
     * @param callback `ShadowsocksCallback`.
     */
    fun start(
        commandLineParams: List<String>,
        obfuscatorProcessEventHandler: ObfuscatorProcessEventHandler,
        callback: ObfuscatorCallback
    )

    /**
     * @param callback `ShadowsocksCallback`.
     */
    fun stop(callback: ObfuscatorCallback)
}

/**
 * It defines the callback structure for an API method without a response object.
 */
public typealias ObfuscatorCallback = (Result<Unit>) -> Unit
