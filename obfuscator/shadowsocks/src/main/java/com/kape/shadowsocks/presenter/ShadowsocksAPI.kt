package com.kape.shadowsocks.presenter

/**
 * Interface defining the API available to the clients.
 */
public interface ShadowsocksAPI {

    /**
     * @param commandLineParams `List<String>`. String representation of the parameters to be used
     * when starting the process. e.g. `-s 1.1.1.1 -p 8080`
     * @param shadowsocksProcessEventHandler `ShadowsocksProcessEventHandler`
     * @param callback `ShadowsocksCallback`.
     */
    fun start(
        commandLineParams: List<String>,
        shadowsocksProcessEventHandler: ShadowsocksProcessEventHandler,
        callback: ShadowsocksCallback
    )

    /**
     * @param callback `ShadowsocksCallback`.
     */
    fun stop(callback: ShadowsocksCallback)
}

/**
 * It defines the callback structure for an API method without a response object.
 */
public typealias ShadowsocksCallback = (Result<Unit>) -> Unit
